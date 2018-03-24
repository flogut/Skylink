package de.hgv.app

import tornadofx.*

class CloudlinkApi: Rest() {

    var token: String? = null

    init {
        Rest.useApacheHttpClient()
        baseURI = "http://$BASE_URI"
    }

    companion object {

        const val BASE_URI = "localhost:7000"

    }
}