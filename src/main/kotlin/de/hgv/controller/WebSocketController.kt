package de.hgv.controller

import de.hgv.app.CloudlinkApi
import de.hgv.websockets.DataWebSocket
import de.hgv.websockets.PictureWebSocket
import org.apache.logging.log4j.LogManager
import org.eclipse.jetty.websocket.client.WebSocketClient
import tornadofx.*
import java.net.URI
import kotlin.concurrent.thread

class WebSocketController: Controller() {

    private val api: CloudlinkApi by inject()

    val pictureWebSocket = PictureWebSocket()
    val dataWebSocket = DataWebSocket()

    private val client = WebSocketClient()

    private var shutdown: Boolean = false

    init {
        thread(true, true) {
            try {
                client.start()

                val pictureUri = URI("ws://${CloudlinkApi.BASE_URI}/receivePictures?token=${api.token}")
                val dataUri = URI("ws://${CloudlinkApi.BASE_URI}/receiveData?token=${api.token}")

                client.connect(pictureWebSocket, pictureUri)
                client.connect(dataWebSocket, dataUri)

                while (!shutdown) {
                    Thread.sleep(100)
                }
            } catch (exception: Exception) {
                LOGGER.error("Error connecting to WebSockets: ${exception.localizedMessage}")
            } finally {
                try {
                    client.stop()
                } catch (exception: Exception) {
                    LOGGER.error("Error stopping WebSocket: ${exception.localizedMessage}")
                }
            }
        }
    }

    fun shutdown() {
        shutdown = true
    }

    companion object {
        private val LOGGER = LogManager.getLogger(WebSocketController::class.java)
    }

}