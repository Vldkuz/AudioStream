package kt.main.infra

open class InfraExceptions : Exception()

class InsertDbError(private val reason: String) : InfraExceptions()

class DownloadError(private val reason: String) : InfraExceptions()
