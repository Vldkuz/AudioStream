package kt.test.mock

import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.testng.annotations.Test
import io.ktor.client.engine.mock.MockEngine
import io.ktor.util.*
import io.ktor.utils.io.*
import org.testng.Assert.assertEquals
import kt.webDav.IFileManager
import kt.webDav.YandexFileManager.HashUtils.md5

/*class WebDavTests {
    object MockConfiguration {
        const val TOKEN = "mock_token"
        const val CONTENT = "mock_content"
        const val PATH = "mock/path"
    }

    @Test
    fun testUploadRequest() = runBlocking {
        val mockEngine = MockEngine { _ ->
            respond (
                content = ByteReadChannel.Empty,
                status = HttpStatusCode.Created,
                headers = headersOf()
            )
        }
        val fileManagerInstance: IFileManager =
            kt.webDav.YandexFileManager(MockConfiguration.TOKEN, HttpClient(mockEngine))

        val response = fileManagerInstance.upload(
            MockConfiguration.PATH,
            ByteReadChannel(MockConfiguration.CONTENT)
        )
    }

    @Test
    fun testDownloadRequest() = runBlocking {
        val mockEngine = MockEngine { _ ->
            val etag = MockConfiguration.CONTENT.toByteArray().md5()

            respond(
                content = GZipEncoder.encode(ByteReadChannel(MockConfiguration.CONTENT)).toByteArray(),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ETag, etag),
            )
        }

        val fileManagerInstance: IFileManager =
            kt.webDav.YandexFileManager(MockConfiguration.TOKEN, HttpClient(mockEngine))

        val downloaded = fileManagerInstance.download(MockConfiguration.PATH)
        assertEquals(
            ByteReadChannel(downloaded).readUTF8Line(),
            MockConfiguration.CONTENT
        )
    }

    @Test
    fun testDeleteRequest() = runBlocking {
        val mockEngine = MockEngine { _ ->
            respond(
                content = ByteReadChannel.Empty,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentLength, "0")
            )
        }

        val fileManagerInstance: IFileManager =
            kt.webDav.YandexFileManager(MockConfiguration.TOKEN, HttpClient(mockEngine))

        val response = fileManagerInstance.delete(
            MockConfiguration.PATH
        )
    }
}*/

//TODO(Переписать тесты под обновленный WebDav)