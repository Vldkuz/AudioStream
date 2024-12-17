package kt.main.api

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.server.application.*
import kt.main.infra.repositories.RoomRepository
import kt.main.infra.repositories.TrackRepository
import kt.main.infra.repositories.UserRepository
import kt.webDav.YandexFileManager
import org.jetbrains.exposed.sql.Database


// Сделал так, поскольку в DI получается, что почему-то кастит пользователя к комнате и т.п непонятно, зачем.

class ServiceLocator(app: Application) {
    private val mainCfg = app.environment.config

    private val db = Database.connect(
        mainCfg.property("database.dbUrl").getString(),
        user = mainCfg.property("database.dbUser").getString(),
        password = mainCfg.property("database.dbPassword").getString()
    )

    private val webDav = YandexFileManager(
        mainCfg.property("webdav.token").getString(),
        HttpClient(CIO)
    )

    val userRep = UserRepository(db)
    val trackRep = TrackRepository(db, webDav, userRep)
    val roomRep = RoomRepository(db, trackRep, userRep)
}


fun Application.configure(): ServiceLocator {
    return ServiceLocator(this)
}