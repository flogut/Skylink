package de.hgv.view

import de.hgv.data.ContentType
import javafx.beans.property.SimpleObjectProperty
import tornadofx.*
import java.util.concurrent.ThreadLocalRandom

class ContentView(type: ContentType): Fragment() {

    val typeProperty = SimpleObjectProperty<ContentType>(type)
    var type by typeProperty

    val number = ThreadLocalRandom.current().nextInt(0, 100)

    override val root = vbox {
        add(PictureContentView())
    }
}
