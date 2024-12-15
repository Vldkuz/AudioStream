package kt.main.infra.repositories

import kotlinx.coroutines.Dispatchers
import kt.main.infra.IDataRepository
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

abstract class RepositoryBase<T>(database: Database, vararg tables: Table) : IDataRepository<T> {
    private val db = database

    init {
        transaction(db) {
            SchemaUtils.create(*tables)
        }
    }

    protected suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}


