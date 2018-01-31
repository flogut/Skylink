package de.hgv.controller

import de.hgv.app.CloudlinkApi
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.message.SimpleMessage
import tornadofx.*

class LoginController: Controller() {

    val api: CloudlinkApi by inject()

    fun tryLogin(username: String, password: String): Boolean {
        with(api) {
            api.reset()
            engine.setBasicAuth(username, password)
            val response = get("login")
            return try {
                when {
                    response.ok() -> {
                        val token = String(response.content().readBytes())
                        api.token = token
                        true
                    }
                    response.statusCode == 401 -> false
                    else -> {
                        LOGGER.error { SimpleMessage("login returned ${response.statusCode} ${response.reason}") }
                        false
                    }
                }
            } finally {
                response.consume()
            }
        }
    }

    companion object {
        val LOGGER = LogManager.getLogger(LoginController::class.java)
    }

}