package de.hgv.view

import de.hgv.controller.LoginController
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.Button
import tornadofx.*

class LoginScreen: View("Skylink") {

    private val loginController: LoginController by inject()
    private val model = ViewModel()
    private val username = model.bind { SimpleStringProperty(this, "username", config.string("username")) }
    private val password = model.bind { SimpleStringProperty(this, "password", config.string("password")) }

    private var loginButton: Button by singleAssign()

    override val root = form {
        fieldset {
            field("Username") {
                textfield(username).required()
            }

            field("Password") {
                passwordfield(password).required()
            }

            loginButton = button("Login") {
                useMaxWidth = true
                isDefaultButton = true
                enableWhen(model.valid)

                setOnAction {
                    this@form.runAsyncWithOverlay {
                        loginController.tryLogin(username.value, password.value)
                    }
                }
            }
        }

        //TODO Add styling
        label(loginController.statusProperty) {
            visibleWhen { loginController.statusProperty.isNotEmpty }
            whenVisible { primaryStage.sizeToScene() }
            isWrapText = true
        }
    }

    init {
        runLater {
            if (username.value != null && password.value != null) {
                loginButton.fire()
            }
        }
    }
}
