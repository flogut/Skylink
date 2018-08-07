package de.hgv.view

import de.hgv.controller.DataController
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.ButtonType
import javafx.scene.control.Dialog
import javafx.scene.input.KeyCode
import javafx.scene.layout.VBox
import javafx.stage.FileChooser
import tornadofx.*
import java.io.File

class MainView: View("Skylink") {
    val dataController: DataController by inject()

    override val root = hbox {
        add(Container::class)

        setOnKeyPressed { e ->
            if (e.isControlDown && e.code == KeyCode.O) {
                e.consume()

                val dialog = Dialog<String>()
                dialog.title = "Lade Datei"
                dialog.headerText = "Lade Daten aus Datei"

                val content = VBox(5.0).apply {
                    style += "-fx-background-color: #f4f4f4;"

                    label("Lädt die Daten statt vom Server aus einer Datei.\nWird durch einen Neustart der Anwendung rückgängig gemacht.") {
                        isWrapText = true
                    }

                    hbox(spacing = 5.0) {
                        val pathProperty = SimpleStringProperty()

                        textfield(pathProperty)

                        button("Datei auswählen") {
                            setOnAction {
                                val files = chooseFile(
                                    "Datei auswählen",
                                    arrayOf(
                                        FileChooser.ExtensionFilter(
                                            "Text-Dateien",
                                            "*.txt", "*.csv"
                                        )
                                    )
                                )

                                if (files.isNotEmpty()) {
                                    pathProperty.value = files[0].path
                                }
                            }
                        }

                        dialog.setResultConverter { buttonType ->
                            if (buttonType == ButtonType.OK) {
                                pathProperty.value
                            } else {
                                null
                            }
                        }
                    }
                }

                dialog.dialogPane.buttonTypes.addAll(ButtonType.CANCEL, ButtonType.OK)

                dialog.dialogPane.content = content

                val result = dialog.showAndWait()

                if (result.isPresent) {
                    val path = result.get()
                    val file = File(path)

                    if (file.exists()) {
                        dataController.loadFromFile(file)
                    }
                }
            }
        }
    }
}