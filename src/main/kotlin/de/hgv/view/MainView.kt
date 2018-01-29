package de.hgv.view

import de.hgv.app.Styles
import tornadofx.*

class MainView : View("Skylink") {

    override val root = hbox {
        label("Test") {
            addClass(Styles.heading)
        }
    }

}