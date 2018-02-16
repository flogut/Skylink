package de.hgv.model

import de.hgv.data.ContentType
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.*
import java.util.*

class Data {

    val idProperty = SimpleStringProperty()
    var id by idProperty

    val typeProperty = SimpleObjectProperty<ContentType>()
    var type by typeProperty

    val valueProperty = SimpleDoubleProperty()
    var value by valueProperty

    val timeProperty = SimpleObjectProperty<Date>()
    var time by timeProperty

}