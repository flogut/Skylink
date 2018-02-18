package de.hgv.app

import de.hgv.controller.WebSocketController
import de.hgv.view.LoginScreen
import tornadofx.*

class Skylink: App(LoginScreen::class, Styles::class) {

    private val webSocketController: WebSocketController by inject()


    init {
        importStylesheet("/stylesheet.css")
    }

    override fun stop() {
        super.stop()
        webSocketController.shutdown()

        System.exit(0)
    }

}