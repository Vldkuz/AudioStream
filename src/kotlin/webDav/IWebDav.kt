package kotlin.webDav

import io.ktor.client.statement.*
import io.ktor.utils.io.*


interface IFileManager {
    suspend fun upload(path: String, data: ByteReadChannel): HttpResponse
    suspend fun download(path: String): Pair<HttpResponse, ByteArray>
    suspend fun delete(path: String): HttpResponse
}

interface IStorageManager {
    suspend fun getPropertiesFileFolder(path: String): HttpResponse
    suspend fun getContentFolder(path: String): HttpResponse
    suspend fun changeProperty(path: String, propertyMapper: HashMap<String, String>): HttpResponse
    suspend fun makeFolder(folderName: String): HttpResponse
    suspend fun copy(srcPath: String, dstPath: String, overwrite: Boolean = false): HttpResponse
    suspend fun move(srcPath: String, dstPath: String, overwrite: Boolean = false): HttpResponse
    suspend fun spaceFree(): HttpResponse
}
