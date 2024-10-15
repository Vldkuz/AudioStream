package webdav

import java.util.UUID
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.*
import utils.HashExtensions.md5String
import utils.HashExtensions.sha256String


class Transactor (host: String, token: String, client: HttpClient, auth: String = "Basic") {
    private val host = host
    private val token = token
    private val client = client
    private val auth = auth

    suspend fun put(holderUUID: UUID, trackUUID: UUID, contentChannel: ByteReadChannel) {
        val path = "/${holderUUID}/${trackUUID}"
        val content = contentChannel.toByteArray()

        val response = client.put {
            url {
                protocol = URLProtocol.HTTPS
                host = this@Transactor.host
                path(path)
            }

            headers {
                append(HttpHeaders.Authorization, "$auth $token")
                append(HttpHeaders.ETag, content.md5String())
                append(WebDavHeaders.Sha256, content.sha256String())
                append(HttpHeaders.ContentType, ContentType.Audio.MPEG.toString())
                append(HttpHeaders.ContentLength, content.size.toString())
            }

            setBody(contentChannel)
        }
    }

    suspend fun get(holderUUID: UUID, trackUUID: UUID, outputChannel: ByteWriteChannel) {
        val path = "/${holderUUID}/${trackUUID}"

        val response = client.get {
            url {
                protocol = URLProtocol.HTTPS
                host = this@Transactor.host
                path(path)
            }

            headers {
                append(HttpHeaders.Authorization, "$auth $token")
                append(HttpHeaders.Accept, ContentType.Audio.MPEG.toString())
            }
        }
        outputChannel.writeByteArray(response.bodyAsBytes())
    }

    suspend fun mkcol(holderUUID: UUID) {
        val path = "/${holderUUID}"

        val response = client.request {
            method = WebDavMethods.MkCol

            url {
                protocol = URLProtocol.HTTPS
                host = this@Transactor.host
                path(path)
            }

            headers {
                append(HttpHeaders.Authorization, "$auth $token")
                append(HttpHeaders.Accept, ContentType.Audio.MPEG.toString())
            }
        }
    }
}