package de.hgv.app

import tornadofx.*

class CloudlinkApi: Rest() {

    var token: String? = null

    init {
        Rest.useApacheHttpClient()
        baseURI = "http://localhost:7000"
    }

    fun setBasicAuth(username: String, password: String, reset: Boolean) {
        if (reset) {
            reset()
        }

        setBasicAuth(username, password)
    }
}