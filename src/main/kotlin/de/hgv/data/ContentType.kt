package de.hgv.data

enum class ContentType {
    PICTURE, HEIGHT, TEMPERATURE;

    override fun toString(): String {
        return super.toString().toLowerCase().capitalize()
    }
}