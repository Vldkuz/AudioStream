package kotlin.main.Infra

import org.jetbrains.exposed.sql.Database

// Будем использовать подход DSL

class DbConnection(
    private val urlDb: String,
    private val driverDb: String,
    private val userDb: String,
    private val passwordDb: String
) {
    companion object Connection {
        val Conn = Database.connect(
            url = urldb
        )
    }
}





