package de.hgv.view

import de.hgv.controller.LoginController
import de.hgv.model.User
import de.hgv.model.UserModel
import javafx.scene.control.Button
import javafx.scene.control.ButtonType
import tornadofx.*

class LoginScreen: View("Skylink") {

    private val loginController: LoginController by inject()
    private val userModel = UserModel(User(config.string("username"), config.string("password")))
//    val username = SimpleStringProperty(this, "username", config.string("username"))
//    val password = SimpleStringProperty(this, "password", config.string("password"))

    private var loginButton: Button by singleAssign()

    override val root = form {
        fieldset {
            field("Username") {
                textfield(userModel.username).required()
            }

            field("Password") {
                passwordfield(userModel.password).required()
            }

            loginButton = button("Login") {
                setOnAction {
                    this@form.runAsyncWithOverlay {
                        loginController.tryLogin(userModel.username.value, userModel.password.value)
                    } ui { success ->
                        if (success) {
                            with(config) {
                                set("username" to userModel.username.value)
                                set("password" to userModel.password.value)
                                save()
                            }

                            showMainScreen()
                        } else {
                            error("Falsche Anmeldedaten", "Username oder Passwort sind inkorrekt", ButtonType.OK)
                        }
                    }
                }

                useMaxWidth = true
                isDefaultButton = true
                enableWhen(userModel.valid)
            }
        }
    }

    init {
        runLater {
            if (userModel.username.value != null && userModel.password.value == null) {
//                loginButton.fire()
            }
        }
    }

    private fun showMainScreen() {
        //TODO Add suitable Transition
        replaceWith(MainView::class, sizeToScene = true, centerOnScreen = true)
    }
}
