package de.hgv.data

enum class ContentType {
    PICTURE, MAP, HEIGHT, TEMPERATURE, LATITUDE, LONGITUDE, PRESSURE, DUST, VOLTAGE, INTERNAL_TEMPERATURE, TIME, DATACOUNTER;

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
        DATACOUNTER -> "Datacounter"
    }

    fun getApiType() = super.toString().toLowerCase().replace(' ', '_')

    fun isInternal() = when (this) {
        LATITUDE -> true
        LONGITUDE -> true
        TIME -> true
        DATACOUNTER -> true
        else -> false
    }
}