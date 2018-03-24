package de.hgv.data

enum class ContentType {
    PICTURE, HEIGHT, TEMPERATURE, MAP, LATITUDE, LONGITUDE;

    override fun toString() = when (this) {
        PICTURE -> "Bilder"
        HEIGHT -> "Höhe"
        TEMPERATURE -> "Temperatur"
        MAP -> "Karte"
        LATITUDE -> "Breitengrad"
        LONGITUDE -> "Längengrad"
    }

    fun getApiType() = super.toString().toLowerCase()
}