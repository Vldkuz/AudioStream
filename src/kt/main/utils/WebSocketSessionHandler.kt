package kt.main.utils

import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel

class WebSocketSessionHandler {
    private val dataChannel: Channel<ByteArray> = Channel()

    suspend fun addData(data: ByteArray) {
        dataChannel.send(data)
    }

    fun handleSession(session: DefaultWebSocketSession) {
        try {
            val senderJob = getSenderJob(session)
            val receiverJob = getReceiverJob(session)
        } catch (e: Exception) {
            throw WebSocketHandlerException("${e.message}")
        }
    }

    private fun getSenderJob(session: DefaultWebSocketSession) = session.launch {
        try {
            for (data in dataChannel) {
                session.send(Frame.Binary(false, data))
            }
        } catch (e: Exception) {
            throw DataSendingHandlerException("Exception during data sending: ${e.message}")
        }
    }

    private fun getReceiverJob(session: DefaultWebSocketSession) = session.launch {
        try {
            //TODO(Тут будет логика ответа на сообщения клиента)
        } catch (e: Exception) {
            throw DataReceivingHandlerException("Exception during data receiving: ${e.message}")
        }
    }
}