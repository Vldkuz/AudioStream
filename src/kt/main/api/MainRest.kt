package kt.main.api

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.server.application.*
import kt.main.core.Room
import kt.main.core.Track
import kt.main.core.User
import kt.main.infra.IAuthCheck
import kt.main.infra.IDataRepository
import kt.main.infra.repositories.RoomRepository
import kt.main.infra.repositories.TrackRepository
import kt.main.infra.repositories.UserRepository
import kt.webDav.IFileManager
import kt.webDav.YandexFileManager
import org.jetbrains.exposed.sql.Database
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin


fun Application.mainRestAPI() {
    val mainCfg = environment.config

    val appModule = module {
        single<IDataRepository<User>>(createdAtStart = true) {UserRepository(get())}
        // single<IDataRepository<Track>>(createdAtStart = true) { TrackRepository(get(), get(), get()) }
        // single<IDataRepository<Room>> { RoomRepository(get(), get(), get())  }

        single <IAuthCheck>(createdAtStart = true) { UserRepository(get())}

        single<IFileManager>(createdAtStart = true) {
            YandexFileManager(
                mainCfg.property("webdav.token").getString(),
                HttpClient(CIO)
            )
        }

        single<Database>(createdAtStart = true) { Database.connect(
            mainCfg.property("database.dbUrl").getString(),
            user = mainCfg.property("database.dbUser").getString(),
            password = mainCfg.property("database.dbPassword").getString()
        )
        }
    }



    install(Koin) {
        modules(appModule)
    }
}