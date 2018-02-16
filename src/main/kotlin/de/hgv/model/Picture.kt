package de.hgv.model

import de.hgv.app.CloudlinkApi
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.*
import java.util.*

class Picture {
    val idProperty = SimpleStringProperty()
    var id: String by idProperty

    val timeProperty = SimpleObjectProperty<Date>()
    var time: Date by timeProperty

    val typeProperty = SimpleStringProperty()
    var type: String by typeProperty

    val urlProperty = SimpleStringProperty("http://${CloudlinkApi.BASE_URI}/picture")
    var url: String by urlProperty

    init {
        idProperty.onChange { newId ->
            url = "http://${CloudlinkApi.BASE_URI}/pictures/$newId"
        }
    }

    override fun toString(): String {
        return "Picture(idProperty=$idProperty, timeProperty=$timeProperty, typeProperty=$typeProperty, urlProperty=$urlProperty)"
    }

}
