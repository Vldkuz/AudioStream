package kt.main.infra.repositories

import kt.main.infra.IDataRepository
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table

data class DBConfig(
    val url: String,
    val driver: String,
    val user: String,
    val password: String
)

abstract class RepositoryBase<T>(dbConfig: DBConfig, vararg tables: Table) : IDataRepository<T> {
    protected val connection = Database.connect(dbConfig.url, dbConfig.driver, dbConfig.user, dbConfig.password)

    init {
        SchemaUtils.create(*tables)
    }
}

