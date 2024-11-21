package kt.webDav


// Эти исключения будут генерироваться, если код ответа от WebDav сервера отличен от 200

open class WebDavExceptions(reason: String): Exception(reason)

class UploadError(reason: String): WebDavExceptions(reason)

class DeleteError(reason: String): WebDavExceptions(reason)

class DownloadError(reason: String): WebDavExceptions(reason)
