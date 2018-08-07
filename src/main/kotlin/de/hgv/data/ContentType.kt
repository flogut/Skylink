package de.hgv.data

enum class ContentType {
    PICTURE, MAP, HEIGHT, TEMPERATURE, LATITUDE, LONGITUDE, PRESSURE, DUST, VOLTAGE, INTERNAL_TEMPERATURE, TIME;

    override fun toString() = when (this) {
        PICTURE -> "Bilder"
        HEIGHT -> "Höhe"
        TEMPERATURE -> "Temperatur"
        MAP -> "Karte"
        LATITUDE -> "Breitengrad"
        LONGITUDE -> "Längengrad"
        PRESSURE -> "Druck"
        DUST -> "Feinstaubdichte"
        VOLTAGE -> "Spannung"
        INTERNAL_TEMPERATURE -> "Interne Temperatur"
        TIME -> "Zeit"
    }

    fun getApiType() = super.toString().toLowerCase().replace(' ', '_')

    fun isInternal() = when (this) {
        LATITUDE -> true
        LONGITUDE -> true
        TIME -> true
        else -> false
    }
}