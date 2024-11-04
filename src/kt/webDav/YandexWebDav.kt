package kt.webDav

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.*
import io.ktor.utils.io.*
import java.security.MessageDigest
import kt.webDav.YandexFileManager.HashUtils.md5
import kt.webDav.YandexFileManager.HashUtils.sha256
import kt.webDav.YandexFileManager.WebDavSpecies


class YandexFileManager(private val token: String, private val client: HttpClient) : kt.webDav.IFileManager {
    object WebDavSpecies {
        const val SERVER = "webdav.yandex.ru"
        const val SHA_HEADER = "Sha256"
    }

    object HashUtils {
        @OptIn(ExperimentalStdlibApi::class)
        fun ByteArray.md5(): String {
            return MessageDigest.getInstance("MD5").digest(this).toHexString()
        }

        @OptIn(ExperimentalStdlibApi::class)
        fun ByteArray.sha256(): String {
            return MessageDigest.getInstance("SHA-256").digest(this).toHexString()
        }

        private fun ByteArray.toHexString(): String {
            return joinToString(separator = "") { eachByte -> "%02x".format(eachByte) }
        }
    }

    override suspend fun upload(path: String, data: ByteReadChannel): HttpResponse {
        val dataBytes = GZipEncoder.encode(data).toByteArray()
        val response = client.put {
            url {
                protocol = URLProtocol.HTTPS
                host = WebDavSpecies.SERVER
                path(path)
            }

            headers {
                append(HttpHeaders.Accept, "*/*")
                append(HttpHeaders.Authorization, token)
                append(HttpHeaders.ETag, dataBytes.md5())
                append(WebDavSpecies.SHA_HEADER, dataBytes.sha256())
                append(HttpHeaders.Expect, "100-continue")
                append(HttpHeaders.ContentType, "application/binary")
                append(HttpHeaders.ContentEncoding, "gzip")
                append(HttpHeaders.ContentLength, dataBytes.size.toString())
            }


            setBody(dataBytes)
        }

        return response
    }

    override suspend fun download(path: String): Pair<HttpResponse, ByteArray> {
        val response = client.get {
            url {
                protocol = URLProtocol.HTTPS
                host = WebDavSpecies.SERVER
                path(path)
            }

            headers {
                append(HttpHeaders.TE, "chunked")
                append(HttpHeaders.AcceptEncoding, "gzip")
                append(HttpHeaders.Authorization, token)
                append(HttpHeaders.Accept, "true")
            }
        }

        val data = try {
            GZipEncoder.decode(response.bodyAsChannel()).toByteArray()
        } catch (_: Exception) {
            response.bodyAsChannel().toByteArray()
        }

        return Pair(response, data)
    }


    override suspend fun delete(path: String): HttpResponse {
        val response = client.delete {
            url {
                protocol = URLProtocol.HTTPS
                host = WebDavSpecies.SERVER
                path(path)
            }

            headers {
                append(HttpHeaders.Accept, "*/*")
                append(HttpHeaders.Authorization, token)
            }
        }

        return response
    }
}


class YandexStorageManager(private val client: HttpClient, private val token: String) : kt.webDav.IStorageManager {
    override suspend fun getPropertiesFileFolder(path: String): HttpResponse {
        // Получаем свойства по-умолчанию, которые декларированы в доке
        // https://yandex.ru/dev/disk/doc/ru/reference/propfind-resource-properties

        val response = client.request {
            method = HttpMethod("PROPFIND")
            url {
                protocol = URLProtocol.HTTPS
                host = WebDavSpecies.SERVER
                path(path)
            }

            headers {
                append(HttpHeaders.Accept, "*/*")
                append(HttpHeaders.Authorization, token)
                append(HttpHeaders.Depth, "0")
            }
        }

        return response
    }

    override suspend fun getContentFolder(path: String): HttpResponse {
        val response = client.request {
            method = HttpMethod("PROPFIND")

            url {
                protocol = URLProtocol.HTTPS
                host = WebDavSpecies.SERVER
                path(path)
            }

            headers {
                append(HttpHeaders.Accept, "*/*")
                append(HttpHeaders.Authorization, token)
                append(HttpHeaders.Depth, "1")
            }
        }

        return response
    }

    override suspend fun changeProperty(path: String, propertyMapper: HashMap<String, String>): HttpResponse {
        TODO("It will be implemented some day")
    }

    override suspend fun makeFolder(folderName: String): HttpResponse {
        val response = client.request {
            method = HttpMethod("MKCOL")

            url {
                protocol = URLProtocol.HTTPS
                host = WebDavSpecies.SERVER
                path(folderName)
            }

            headers {
                append(HttpHeaders.Accept, "*/*")
                append(HttpHeaders.Authorization, token)
            }
        }

        return response
    }

    override suspend fun copy(srcPath: String, dstPath: String, overwrite: Boolean): HttpResponse {
        val rewrite = if (overwrite) "T" else "F"
        val response = client.request {
            method = HttpMethod("COPY")

            url {
                protocol = URLProtocol.HTTPS
                host = WebDavSpecies.SERVER
                path(srcPath)
            }

            headers {
                append(HttpHeaders.Accept, "*/*")
                append(HttpHeaders.Authorization, token)
                append(HttpHeaders.Destination, dstPath)
                append(HttpHeaders.Overwrite, rewrite)
            }
        }

        return response
    }

    override suspend fun move(srcPath: String, dstPath: String, overwrite: Boolean): HttpResponse {
        val rewrite = if (overwrite) "T" else "F"
        val response = client.request {
            method = HttpMethod("MOVE")

            url {
                protocol = URLProtocol.HTTPS
                host = WebDavSpecies.SERVER
                path(srcPath)
            }

            headers {
                append(HttpHeaders.Accept, "*/*")
                append(HttpHeaders.Authorization, token)
                append(HttpHeaders.Destination, dstPath)
                append(HttpHeaders.Overwrite, rewrite)
            }
        }

        return response
    }

    override suspend fun spaceFree(): HttpResponse {
        val response = client.request {
            method = HttpMethod("PROPFIND")

            url {
                protocol = URLProtocol.HTTPS
                host = WebDavSpecies.SERVER
            }

            headers {
                append(HttpHeaders.Accept, "*/*")
                append(HttpHeaders.Authorization, token)
                append(HttpHeaders.Depth, "0")
            }

            setBody(
                """<D:propfind xmlns:D="DAV:">
                <D:prop>
                <D:quota-available-bytes/>
                <D:quota-used-bytes/>
                </D:prop>
                </D:propfind>"""
            )
        }

        return response
    }

}