package de.hgv.view

import de.hgv.app.CloudlinkApi
import de.hgv.controller.WebSocketController
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Pos
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.Priority
import org.apache.logging.log4j.LogManager
import tornadofx.*


class PictureContentView: Fragment() {

    val api: CloudlinkApi by inject()

    val webSocketController: WebSocketController by inject()
    val pictureWebSocket = webSocketController.pictureWebSocket

    val pictureProperty = pictureWebSocket.pictureProperty

    val imageProperty = SimpleObjectProperty<Image>()
    var image by imageProperty

    var imageView by singleAssign<ImageView>()

    override val root = hbox(alignment = Pos.TOP_CENTER) {
        useMaxSize = true
        hgrow = Priority.ALWAYS
        vgrow = Priority.ALWAYS

        minWidth = 30.0
        minHeight = 30.0


        pane {
            hgrow = Priority.ALWAYS
            vgrow = Priority.ALWAYS

            alignment = Pos.TOP_CENTER

            imageView = imageview(imageProperty) {
                hgrow = Priority.ALWAYS
                vgrow = Priority.ALWAYS
                alignment = Pos.TOP_CENTER

                isPreserveRatio = true
                isSmooth = true

                fitWidthProperty().bind(this@pane.widthProperty())
                fitHeightProperty().bind(this@pane.heightProperty())
            }
        }
    }

    init {
        //TODO Add error handling
        pictureProperty.select { it.idProperty }.onChange { id ->
            runAsync {
                api.get("pictures/$id")
            } ui { response ->
                image = Image(response.content())
            }
        }

        imageView.runAsyncWithOverlay {
            api.get("picture")
        } ui { response ->
            image = Image(response.content())
        }
    }

    companion object {
        private val LOGGER = LogManager.getLogger()
    }
}