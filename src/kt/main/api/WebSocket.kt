package kt.main.api

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kt.main.utils.WebSocketSessionHandler
import java.util.UUID

object WebSocketApi {
    fun Application.configureWebSocketRoutes() {
        install(WebSockets)

        routing {
            webSocket("") {
                val sessionUUID = call.parameters["sessionUUID"] ?: return@webSocket close(
                    CloseReason(CloseReason.Codes.CANNOT_ACCEPT, "UUID missed")
                )
                WebSocketSessionManager.createSession(UUID.fromString(sessionUUID), this)
            }
        }

        suspend fun closeSession(sessionUUID: UUID) {
            WebSocketSessionManager.closeSession(sessionUUID)
        }
    }
}

object WebSocketSessionManager {
    private val sessions = mutableMapOf<UUID, WebSocketSessionHandler>()
    private val mutex = Mutex()

    suspend fun createSession(sessionUUID: UUID, session: DefaultWebSocketServerSession) {
        mutex.withLock {
            if (sessions.containsKey(sessionUUID)) {
                return
            }

            val handler = WebSocketSessionHandler()
            sessions[sessionUUID] = handler

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    handler.handleSession(session)
                } finally {
                    closeSession(sessionUUID)
                }
            }
        }
    }

    suspend fun closeSession(sessionUUID: UUID) {
        mutex.withLock {
            sessions.remove(sessionUUID)
        }
    }

    suspend fun getSession(sessionUUID: UUID): WebSocketSessionHandler? {
        return mutex.withLock { sessions[sessionUUID] }
    }
}