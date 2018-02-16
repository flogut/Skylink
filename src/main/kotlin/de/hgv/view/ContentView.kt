package de.hgv.view

import de.hgv.data.ContentType
import javafx.beans.property.SimpleObjectProperty
import tornadofx.*

class ContentView(type: ContentType): Fragment() {

    val typeProperty = SimpleObjectProperty<ContentType>(type)
    var type: ContentType by typeProperty

    private var contentView: Fragment = getContentView(type)

    override val root = vbox {
        add(contentView)
    }

    init {
        typeProperty.onChange { newType ->
            if (newType != null) {
                contentView.removeFromParent()
                contentView = getContentView(newType)
                root.add(contentView)
            }
        }
    }

    private fun getContentView(type: ContentType) = when (type) {
        ContentType.PICTURE -> find<PictureContentView>()
        ContentType.HEIGHT -> find<DataContentView>(mapOf("type" to type))
        ContentType.TEMPERATURE -> find<DataContentView>(mapOf("type" to type))
    }
}
