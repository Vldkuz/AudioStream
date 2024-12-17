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
import kt.main.core.Room
import kt.main.core.Track
import kt.main.core.UProfile
import kt.main.core.User
import kt.main.infra.IAuthCheck
import kt.main.infra.IDataRepository
import kt.main.utils.AuthForm
import kt.main.utils.CredentialsFormer
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.koin.ktor.ext.inject
import org.postgresql.util.PSQLException
import java.util.*

fun Application.restAPI() {
    val secret = environment.config.property("jwt.secret").getString()
    val authJWTName = "auth-jwt"

    val components = configure()

    val userRepository = components.userRep as IDataRepository<User>
    val authRepository = components.userRep as IAuthCheck
    val trackRepository = components.trackRep as IDataRepository<Track>
    val roomRepository = components.roomRep as IDataRepository<Room>

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
                    val nameUser = call.parameters["name"].orEmpty()
                    call.respond(userRepository.getByName(nameUser))
                }

                get("/byId/{id}") {
                    val idUser = UUID.fromString(call.parameters["id"])
                    val user = userRepository.getById(idUser)
                    val users = listOf(user)
                    call.respond(users)
                }


                put("/update/auth/{id}") {
                    val id = UUID.fromString(call.parameters["id"].orEmpty())
                    val newAuth = call.receive<AuthForm>()
                    val principal = call.principal<JWTPrincipal>()
                    val loginPrincipal = principal?.payload?.getClaim("username")?.asString()

                    when (val userInDb = userRepository.getById(id)) {
                        null -> call.respond(HttpStatusCode.NotFound)
                        else -> {
                            val loginUser = userInDb.auth?.login

                            when (loginUser) {
                                loginPrincipal -> {
                                    val auth = CredentialsFormer.form2Auth(newAuth)
                                    val newUser = User(userInDb.uProfile, auth, userInDb.id)
                                    userRepository.update(newUser)
                                    call.respond(HttpStatusCode.OK)
                                }

                                else -> call.respond(HttpStatusCode.Forbidden)
                            }

                        }
                    }
                }

                put("/update/profile/{id}") {
                    val id = UUID.fromString(call.parameters["id"].orEmpty())
                    val newProfile = call.receive<UProfile>()
                    val principal = call.principal<JWTPrincipal>()
                    val loginPrincipal = principal?.payload?.getClaim("username")?.asString()

                    when (val userInDb = userRepository.getById(id)) {
                        null -> call.respond(HttpStatusCode.NotFound)
                        else -> {
                            val loginUser = userInDb.auth?.login

                            when (loginUser) {
                                loginPrincipal -> {
                                    val newUser = User(newProfile, userInDb.auth, userInDb.id)
                                    userRepository.update(newUser)
                                    call.respond(HttpStatusCode.OK)
                                }

                                else -> call.respond(HttpStatusCode.Forbidden)
                            }
                        }
                    }
                }
                delete("/remove/{id}") {
                    val id = UUID.fromString(call.parameters["id"])

                    when (val userInDb = userRepository.getById(id)) {
                        null -> call.respond(HttpStatusCode.NotFound)
                        else -> {
                            val jwt = call.principal<JWTPrincipal>()
                            val loginPrincipal = jwt!!.payload.getClaim("username").asString()
                            val login = userInDb.auth!!.login
                            when (loginPrincipal) {
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

            post("/register") {
                val user = call.receive<User>()

                val fmtAuth = CredentialsFormer.form2Auth(CredentialsFormer.auth2Form(user.auth!!))
                val fmtUser = User(user.uProfile, fmtAuth, user.id)

                try {
                    userRepository.add(fmtUser)
                } catch (_: ExposedSQLException) {
                    call.respond(HttpStatusCode.BadRequest)
                }

                call.respond(HttpStatusCode.Created)
            }

            post("/login") {
                val auth = CredentialsFormer.form2Auth(call.receive<AuthForm>())

                when (authRepository.checkAuth(auth)) {
                    true -> {
                        val token = JWT.create()
                            .withClaim("username", auth.login)
                            .withExpiresAt(Date(System.currentTimeMillis() + 3600000))
                            .sign(Algorithm.HMAC256(secret))
                        call.respond(hashMapOf("token" to token))
                    }

                    else -> call.respond(HttpStatusCode.Unauthorized)
                }
            }
        }

        authenticate(authJWTName) {
            route("/rooms") {
                get{call.respond(roomRepository.getAll())}

                get("/byName/{name}") {
                    val room = call.parameters["name"].orEmpty()
                    call.respond(roomRepository.getByName(room))
                }

                get("/byId/{id}") {
                    val id = UUID.fromString(call.parameters["id"])
                    val room = roomRepository.getById(id)
                    val rooms = listOf(room)
                    call.respond(rooms)
                }

                put("/update/{id}") {
                    val id = UUID.fromString(call.parameters["id"])
                    val userRoom = call.receive<Room>()

                    when(val dbRoom = roomRepository.getById(id)) {
                        null -> call.respond(HttpStatusCode.NotFound)
                        else -> {
                            val room = Room(userRoom.rProfile, userRoom.participants, userRoom.trackQueue, dbRoom.id)
                            roomRepository.update(room)
                            call.respond(HttpStatusCode.OK)
                        }
                    }
                }

                delete("/remove/{id}") {
                    val id = UUID.fromString(call.parameters["id"].orEmpty())

                    when (val dbRoom = roomRepository.getById(id)) {
                        null -> call.respond(HttpStatusCode.NotFound)
                        else -> {roomRepository.remove(dbRoom)}
                    }
                }

                post("/add") {
                    val room = call.receive<Room>()
                    roomRepository.add(room)
                    call.respond(HttpStatusCode.OK)
                }
            }

            route("/tracks") {
                get { call.respond(trackRepository.getAll()) }

                get("/byName/{name}") {
                    val name = call.parameters["name"].orEmpty()
                    call.respond(trackRepository.getByName(name))
                }

                get("/byId/{id}") {
                    val id = UUID.fromString(call.parameters["id"].orEmpty())
                    val track = trackRepository.getById(id)
                    val tracks = listOf(track)
                    call.respond(tracks)
                }

                put("/update/{id}") {
                    val id = UUID.fromString(call.parameters["id"])
                    val track = call.receive<Track>()
                    when (val dbTrack = trackRepository.getById(id)) {
                        null -> call.respond(HttpStatusCode.NotFound)
                        else -> {
                            val newTrack = Track(track.tProfile, track.data, dbTrack.id)
                            trackRepository.update(newTrack)
                        }
                    }
                }

                delete("/remove/{id}") {
                    val id = UUID.fromString(call.parameters["id"].orEmpty())
                    when (val dbTrack = trackRepository.getById(id)) {
                        null -> call.respond(HttpStatusCode.NotFound)
                        else -> {trackRepository.remove(dbTrack)}
                    }
                }

                post("/add") {
                    val track = call.receive<Track>()
                    trackRepository.add(track)
                    call.respond(HttpStatusCode.OK)
                }
            }
        }

    }
}