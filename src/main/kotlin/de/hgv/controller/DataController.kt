package de.hgv.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import de.hgv.app.CloudlinkApi
import de.hgv.data.ContentType
import de.hgv.model.Data
import org.apache.logging.log4j.LogManager
import tornadofx.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class DataController: Controller() {

    private val api: CloudlinkApi by inject()

    private var mode: Mode = Mode.DOWNLOAD
    private var file: File? = null
    private var fileType: FileType? = null

    fun getData(type: ContentType): List<Data> = when (mode) {
        Mode.DOWNLOAD -> downloadData(type)
        Mode.FILE -> getFromFile(type)
    }

    fun loadFromFile(file: File) {
        mode = Mode.FILE
        this.file = file
        this.fileType = FileType.of(file)
    }

    private fun downloadData(type: ContentType): List<Data> {
        return try {
            val response = api.get("data?type=${type.getApiType()}")

            val mapper = jacksonObjectMapper()

            mapper.readValue<List<Data>>(response.content()).sortedBy { it.time }
        } catch (exception: RestException) {
            LOGGER.error("Downloading data failed: ${exception.localizedMessage}")

            runLater {
                error("Daten konnten nicht heruntergeladen werden", exception.localizedMessage)
            }

            listOf()
        }
    }

    private fun getFromFile(type: ContentType): List<Data> {
        return when (fileType) {
            FileType.SINGLE -> getFromFileSingle(type)
            FileType.MULTIPLE -> getFromFileMultiple(type)
            null -> emptyList()
        }
    }

    private fun getFromFileSingle(type: ContentType): List<Data> {
        val values =
            file
                ?.readText()
                ?.lines()
                ?.mapNotNull { it.toDoubleOrNull() } ?: return emptyList()

        val millis = System.currentTimeMillis()

        val dataList = mutableListOf<Data>()
        values.forEachIndexed { i, value ->
            val id = UUID.randomUUID().toString()
            val time = Date(millis + 1000 * i)

            dataList.add(Data(id, type, value, time))
        }

        return dataList
    }

    private fun getFromFileMultiple(type: ContentType): List<Data> {
        val types = mapOf(
            "T" to ContentType.TEMPERATURE,
            "P" to ContentType.PRESSURE,
            "D" to ContentType.DUST,
            "VO" to ContentType.VOLTAGE,
            "LA" to ContentType.LATITUDE,
            "LO" to ContentType.LONGITUDE,
            "TI" to ContentType.TIME,
            "H" to ContentType.HEIGHT,
            "TC" to ContentType.INTERNAL_TEMPERATURE
        )

        val lines =
            file
                ?.readText()
                ?.lines()
                ?.filter { it.isNotBlank() }
                ?.map { it.removeSuffix("\\").removeSuffix("}") }
                ?.map { line ->
                    line
                        .split(",")
                        .map { it.split(":") }
                        .filter { it.size == 2 }
                        .map { types[it[0]] to it[1] }
                        .filterNot { it.first == null }
                        .toMap()
                } ?: return emptyList()

        val dataList = mutableListOf<Data>()
        for (line in lines) {
            val value = line[type]?.toDoubleOrNull() ?: continue

            val date = line[ContentType.TIME] ?: continue
            val simpleDateFormat = when (date.count { it == ';' }) {
                2 -> SimpleDateFormat("hh;mm;ss")
                3 -> SimpleDateFormat("hh;mm;ss;SSS")
                else -> null
            } ?: continue
            val time = simpleDateFormat.parse(line[ContentType.TIME])

            val id = UUID.randomUUID().toString()

            val data = Data(id, type, value, time)

            dataList.add(data)
        }

        return dataList
    }

    companion object {
        private val LOGGER = LogManager.getLogger(DataController::class.java)
    }

    private enum class Mode {
        DOWNLOAD, FILE
    }

    private enum class FileType {
        SINGLE, MULTIPLE;

        companion object {

            fun of(file: File): FileType? {
                val text = file.readText()
                return when {
                // Eine oder mehr Zeilen mit je einer Dezimalzahl
                    text.matches(Regex("(\\d+.?\\d*\\r?\\n?)+")) -> SINGLE
                // Eine oder mehr Zeilen im Format TYP:0.00, TYP2:0.00 [,...]
                    text.matches(Regex("((\\w+:\\d+.?\\d*,?)+\\r?\\n?)+")) -> MULTIPLE
                    else -> null
                }
            }
        }
    }
}