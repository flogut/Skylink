package de.hgv.data

enum class ContentType {
    PICTURE, HEIGHT, TEMPERATURE;

    override fun toString() = when (this) {
        PICTURE -> "Bilder"
        HEIGHT -> "Höhe"
        TEMPERATURE -> "Temperatur"
    }

    fun getApiType() = super.toString().toLowerCase()
}