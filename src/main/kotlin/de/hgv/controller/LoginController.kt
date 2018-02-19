package de.hgv.controller

import de.hgv.app.CloudlinkApi
import de.hgv.view.LoginScreen
import de.hgv.view.MainView
import javafx.beans.property.SimpleStringProperty
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import tornadofx.*

class LoginController: Controller() {

    private val api: CloudlinkApi by inject()

    val statusProperty = SimpleStringProperty()
    var status: String by statusProperty

    fun tryLogin(username: String, password: String, stayLoggedIn: Boolean) {
        runLater { status = "" }

        api.setBasicAuth(username, password, true)

        try {
            val response = api.get("login")

            if (response.ok()) {
                api.token = String(response.content().readBytes())

                with(app.config) {
                    if (stayLoggedIn) {
                        set("username" to username)
                        set("password" to password)
                    } else {
                        remove("username")
                        remove("password")
                    }
                    set("stayLoggedIn" to stayLoggedIn)
                    save()
                }

                //Delay is a workaround to make the overlay go away
                runLater(5.millis) {
                    //TODO Add suitable Transition
                    find(LoginScreen::class).replaceWith(MainView::class)
                }
            } else if (response.statusCode == 401) {
                runLater {
                    status = "Nutzername oder Passwort sind inkorrekt"
                }
            } else {
                runLater {
                    LOGGER.error("login returned ${response.statusCode} ${response.status.name}: ${response.reason}")
                    status = "${response.status.name}: ${response.reason}"
                }
            }
        } catch (exception: RestException) {
            LOGGER.error("Login failed: ${exception.localizedMessage}")

            //TODO Display German message
            runLater {
                status = "Login gescheitert: ${exception.localizedMessage}"
            }
        }
    }

    companion object {
        val LOGGER: Logger = LogManager.getLogger(LoginController::class.java)
    }

}