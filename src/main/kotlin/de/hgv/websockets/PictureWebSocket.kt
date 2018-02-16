package de.hgv.websockets

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import de.hgv.model.Picture
import javafx.beans.property.SimpleObjectProperty
import org.apache.logging.log4j.LogManager
import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage
import org.eclipse.jetty.websocket.api.annotations.WebSocket
import tornadofx.*

// maxIdleTime = 1 day
@WebSocket(maxBinaryMessageSize = 5 * 1024 * 1024, maxIdleTime = 1000 * 60 * 60 * 24)
class PictureWebSocket {

    private lateinit var session: Session

    val pictureProperty = SimpleObjectProperty<Picture>(Picture())
    var picture by pictureProperty


    @OnWebSocketConnect
    fun onConnect(session: Session) {
        LOGGER.info("Connected to ${session.remoteAddress}")

        this.session = session
    }

    @OnWebSocketClose
    fun onClose(statusCode: Int, reason: String) {
        LOGGER.info("Conntection closed with code $statusCode: $reason")

        //TODO Reconnect
    }

    @OnWebSocketMessage
    fun onMessage(message: String) {
        val mapper = jacksonObjectMapper()

        val pic = mapper.readValue<Picture>(message)

        with(pic) {
            picture.id = id
            picture.time = time
            picture.type = type
        }

        LOGGER.debug(picture)
    }

    companion object {

        private val LOGGER = LogManager.getLogger(PictureWebSocket::class.java)

    }

}