package kt.main.utils

open class WebSocketHandlerException(reason: String): Exception(reason)
class DataSendingHandlerException(reason: String): WebSocketHandlerException(reason)
class DataReceivingHandlerException(reason: String): WebSocketHandlerException(reason)