package kt.main.api

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.util.collections.*
import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kt.main.utils.WebSocketSessionHandler
import java.util.UUID


object WebSocketSessionManager {
    private val sessions = ConcurrentMap<UUID, WebSocketSessionHandler>()

    fun Routing.addWebSocketSession(sessionUUID: UUID): String {
        val sessionPath = "/session/$sessionUUID"

        webSocket(sessionPath) {
            createSession(sessionUUID, this)
        }

        return sessionPath
    }

    private fun createSession(sessionUUID: UUID, session: DefaultWebSocketServerSession) {
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

    private fun closeSession(sessionUUID: UUID) {
        sessions.remove(sessionUUID)
    }

    fun getSession(sessionUUID: UUID): WebSocketSessionHandler? {
        return sessions[sessionUUID]
    }
}