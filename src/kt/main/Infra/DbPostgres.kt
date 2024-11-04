package kt.main.Infra

import org.jetbrains.exposed.sql.Database

// Будем использовать подход DSL

class DbConnection(
    private val urlDb: String,
    private val driverDb: String,
    private val userDb: String,
    private val passwordDb: String
) {
    companion object Connection {
        fun Connect(conn: DbConnection): Database = Database.connect(
            url = conn.urlDb
        )
    }
}





