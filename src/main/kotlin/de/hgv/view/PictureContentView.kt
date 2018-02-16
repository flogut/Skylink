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

    private val api: CloudlinkApi by inject()

    private val webSocketController: WebSocketController by inject()
    private val pictureWebSocket = webSocketController.pictureWebSocket

    private val pictureProperty = pictureWebSocket.pictureProperty

    private val imageProperty = SimpleObjectProperty<Image>()
    private var image: Image by imageProperty

    private var imageView by singleAssign<ImageView>()

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
        pictureProperty.select { it.idProperty }.onChange { id ->
            runAsync {
                try {
                    true to api.get("pictures/$id")
                } catch (exception: RestException) {
                    LOGGER.error("Downloading data failed: ${exception.localizedMessage}")

                    runLater {
                        error("Daten konnten nicht heruntergeladen werden", exception.localizedMessage)
                    }
                    false to null
                }
            } ui { (success, response) ->
                if (success && response != null) {
                    image = Image(response.content())
                }
            }
        }

        imageView.runAsyncWithOverlay {
            try {
                true to api.get("picture")
            } catch (exception: RestException) {
                LOGGER.error("Downloading data failed: ${exception.localizedMessage}")

                runLater {
                    error("Daten konnten nicht heruntergeladen werden", exception.localizedMessage)
                }
                false to null
            }
        } ui { (success, response) ->
            if (success && response != null) {
                image = Image(response.content())
            }
        }
    }

    companion object {
        private val LOGGER = LogManager.getLogger(PictureContentView::class.java)
    }
}