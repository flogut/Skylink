package de.hgv.app

import tornadofx.*

class CloudlinkApi: Rest() {

    var token: String? = null

    init {
        Rest.useApacheHttpClient()
        baseURI = "http://$BASE_URI"
    }

    fun setBasicAuth(username: String, password: String, reset: Boolean) {
        if (reset) {
            reset()
        }

        setBasicAuth(username, password)
    }

    companion object {

        const val BASE_URI = "localhost:7000"

    }
}