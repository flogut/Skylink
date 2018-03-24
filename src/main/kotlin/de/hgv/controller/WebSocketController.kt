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

    val pictureWebSocket = PictureWebSocket(this)
    val dataWebSocket = DataWebSocket(this)

    private val client = WebSocketClient()

    private var shutdown: Boolean = false

    init {
        thread(true, true) {
            try {
                client.start()
                client.isStopAtShutdown = true

                val pictureUri = URI("ws://${CloudlinkApi.BASE_URI}/receivePictures?token=${api.token}")
                val dataUri = URI("ws://${CloudlinkApi.BASE_URI}/receiveData?token=${api.token}")

                val pictureSession = client.connect(pictureWebSocket, pictureUri).get()
                val dataSession = client.connect(dataWebSocket, dataUri).get()

                while (!shutdown && (client.isRunning || pictureSession.isOpen || dataSession.isOpen)) {
                    Thread.sleep(100)
                }
            } catch (exception: Exception) {
                LOGGER.error("Error connecting to WebSockets: ${exception.localizedMessage}")

                runLater {
                    error("Verbindung zum Server konnte nicht aufgebaut werden", exception.localizedMessage)
                }
            } finally {
                try {
                    if (shutdown) {
                        client.stop()
                    }
                } catch (exception: Exception) {
                    LOGGER.error("Error stopping WebSocket: ${exception.localizedMessage}")
                }
            }
        }
    }

    fun shutdown() {
        shutdown = true

        for (session in client.openSessions) {
            session.disconnect()
        }

        client.stop()
    }

    fun reconnect(webSocket: WebSocket) {
        if (!shutdown) {
            thread(true, true) {
                try {
                    if (webSocket == WebSocket.PICTURES) {
                        val pictureUri = URI("ws://${CloudlinkApi.BASE_URI}/receivePictures?token=${api.token}")
                        val pictureSession = client.connect(pictureWebSocket, pictureUri).get()

                        while (!shutdown && client.isRunning && pictureSession.isOpen) {
                            Thread.sleep(100)
                        }
                    } else if (webSocket == WebSocket.DATA) {
                        val dataUri = URI("ws://${CloudlinkApi.BASE_URI}/receiveData?token=${api.token}")
                        val dataSession = client.connect(dataWebSocket, dataUri).get()

                        while (!shutdown && client.isRunning && dataSession.isOpen) {
                            Thread.sleep(100)
                        }

                        println("Ending thread")
                    }
                } catch (exception: Exception) {
                    LOGGER.error("Error connecting to WebSockets: ${exception.localizedMessage}")

                    runLater {
                        error("Verbindung zum Server konnte nicht aufgebaut werden", exception.localizedMessage)
                    }
                }
            }
        }
    }

    companion object {
        private val LOGGER = LogManager.getLogger(WebSocketController::class.java)
    }

    enum class WebSocket {
        PICTURES, DATA
    }

}