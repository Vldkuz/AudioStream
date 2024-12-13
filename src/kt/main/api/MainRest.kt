package kt.main.api

import io.ktor.server.application.*
import kt.main.infra.repositories.UserRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin

val appModule = module {
    singleOf(::UserRepository) {bind<UserRepository> ()}
    singleOf(::){}
}

fun Application.mainRestAPI() {
    install(Koin) {
        modules(appModule)
    }
}