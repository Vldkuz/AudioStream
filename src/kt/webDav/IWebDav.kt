package kt.webDav

import io.ktor.client.statement.*
import io.ktor.utils.io.*


interface IFileManager {
    suspend fun upload(path: String, data: ByteReadChannel)
    suspend fun download(path: String): ByteArray
    suspend fun delete(path: String)
}

// TODO(Проработать интерфейс для управления файловым хранилищем более глубоко, не отдавая HTTP Response)

interface IStorageManager {
    suspend fun getPropertiesFileFolder(path: String): HttpResponse
    suspend fun getContentFolder(path: String): HttpResponse
    suspend fun changeProperty(path: String, propertyMapper: HashMap<String, String>): HttpResponse
    suspend fun makeFolder(folderName: String): HttpResponse
    suspend fun copy(srcPath: String, dstPath: String, overwrite: Boolean = false): HttpResponse
    suspend fun move(srcPath: String, dstPath: String, overwrite: Boolean = false): HttpResponse
    suspend fun spaceFree(): HttpResponse
}
