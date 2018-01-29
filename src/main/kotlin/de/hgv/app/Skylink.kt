package de.hgv.app

import de.hgv.view.MainView
import tornadofx.*

class Skylink: App(MainView::class, Styles::class) {

    val api: Rest by inject()

    init {
        api.baseURI = "localhost:7000"
    }

}