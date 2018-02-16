package de.hgv.websockets

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import de.hgv.controller.WebSocketController
import de.hgv.data.ContentType
import de.hgv.model.Data
import org.apache.logging.log4j.LogManager
import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage
import org.eclipse.jetty.websocket.api.annotations.WebSocket

@WebSocket(maxIdleTime = 1000 * 60 * 60 * 24)
class DataWebSocket(private val controller: WebSocketController) {
    private lateinit var session: Session

    private val listeners = hashMapOf<ContentType, MutableList<(Data) -> Unit>>()

    @OnWebSocketConnect
    fun onConnect(session: Session) {
        LOGGER.info("Connected to ${session.remoteAddress}")

        this.session = session
    }

    @OnWebSocketClose
    fun onClose(statusCode: Int, reason: String) {
        LOGGER.info("Conntection closed with code $statusCode: $reason")

        controller.reconnect(WebSocketController.WebSocket.DATA)
    }

    @OnWebSocketMessage
    fun onMessage(message: String) {
        val mapper = jacksonObjectMapper()

        val data = mapper.readValue<Data>(message)

        listeners[data.type]?.forEach { it(data) }
    }

    fun onType(type: ContentType, block: (Data) -> Unit) {
        val list = listeners[type]
        if (list != null) {
            list.add(block)
        } else {
            listeners[type] = mutableListOf(block)
        }
    }

    companion object {

        private val LOGGER = LogManager.getLogger(DataWebSocket::class.java)

    }

}