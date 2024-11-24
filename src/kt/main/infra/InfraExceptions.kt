package kt.main.infra

open class InfraExceptions(reason: String) : Exception(reason)

class InsertDbError(reason: String) : InfraExceptions(reason)

class DownloadDbError(reason: String) : InfraExceptions(reason)

class UpdateDbError(reason: String) : InfraExceptions(reason)

class DeleteDbError(reason: String) : InfraExceptions(reason)