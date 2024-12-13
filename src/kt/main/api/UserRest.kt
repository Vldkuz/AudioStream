package kt.main.api

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kt.main.core.User
import kt.main.infra.repositories.UserRepository
import kt.main.utils.AuthForm
import kt.main.utils.CredentialsFormer
import org.koin.ktor.ext.inject
import java.util.*

fun Application.userRestAPI() {
    val secret = environment.config.property("jwt.secret").getString()
    val authJWTName = "auth-jwt"
    val userRepository by inject<UserRepository>()

    install(ContentNegotiation) {
        json()
    }

    install(Authentication) {
        jwt {
            jwt(authJWTName) {
                verifier(
                    JWT
                        .require(Algorithm.HMAC256(secret))
                        .build()
                )

                validate { credential ->
                    if (credential.payload.getClaim("username").asString() != "") {
                        JWTPrincipal(credential.payload)
                    } else {
                        null
                    }
                }

                challenge { _, _ ->
                    call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired")
                }
            }
        }
    }


    routing {
        route("/users") {
            authenticate(authJWTName) {
                get { call.respond(userRepository.getAll()) }

                get("/byName/{name}") {
                    val nameUser = call.parameters["name"]
                    call.respond(userRepository.getByName(nameUser!!))
                }

                get("/byId/{id}") {
                    val idUser = UUID.fromString(call.parameters["id"])
                    val user = userRepository.getById(idUser)
                    val users = listOf(user)
                    call.respond(users)
                }
            }

            post("/register") {
                val user = call.receive<User>()

                val fmtAuth = CredentialsFormer.form2Auth(CredentialsFormer.auth2Form(user.auth!!))
                val fmtUser = User(user.uProfile, fmtAuth, user.id)

                userRepository.add(fmtUser)
                call.respond(HttpStatusCode.Created)
            }

            post("/login") {
                val auth = CredentialsFormer.form2Auth(call.receive<AuthForm>())

                when(userRepository.checkAuth(auth)) {
                    true -> {
                        val token = JWT.create()
                        .withClaim("username", auth.login)
                        .withExpiresAt(Date(System.currentTimeMillis() + 60000))
                        .sign(Algorithm.HMAC256(secret))
                        call.respond(hashMapOf("token" to token))
                    }

                    else -> call.respond(HttpStatusCode.Unauthorized)
                }
            }
            }

            authenticate(authJWTName) {
                put("/update/{id}") {
                    val id = UUID.fromString(call.parameters["id"])
                    val updatedUser = call.receive<User>()
                    val principal = call.principal<JWTPrincipal>()
                    val loginPrincipal = principal!!.payload.getClaim("username").asString()

                    when(val userInDb = userRepository.getById(id)) {
                        null -> call.respond(HttpStatusCode.NotFound)
                        else -> {
                            val loginUser = userInDb.auth!!.login

                            when (loginUser) {
                                loginPrincipal -> {
                                    userRepository.update(updatedUser)
                                }

                                else -> call.respond(HttpStatusCode.Forbidden)
                            }
                        }
                }
            }
                delete("/remove/{id}") {
                    val id = UUID.fromString(call.parameters["id"])

                    when(val userInDb = userRepository.getById(id)) {
                        null -> call.respond(HttpStatusCode.NotFound)
                        else -> {
                            val jwt = call.principal<JWTPrincipal>()
                            val loginPrincipal = jwt!!.payload.getClaim("username").asString()
                            val login = userInDb.auth!!.login
                            when(loginPrincipal) {
                                login -> {
                                    userRepository.remove(userInDb)
                                    call.respond(HttpStatusCode.OK)
                                }
                                else -> call.respond(HttpStatusCode.Forbidden)
                            }
                        }
                    }
                }
        }
    }

}