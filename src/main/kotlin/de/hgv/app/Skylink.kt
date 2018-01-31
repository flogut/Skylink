package de.hgv.app

import de.hgv.view.LoginScreen
import tornadofx.*

class Skylink: App(LoginScreen::class, Styles::class) {

    val api: CloudlinkApi by inject()

    init {
        Rest.useApacheHttpClient()
        api.baseURI = "http://localhost:7000"
    }

}