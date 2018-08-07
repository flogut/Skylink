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

    fun getData(type: ContentType): List<Data> = when (mode) {
        Mode.DOWNLOAD -> downloadData(type)
        Mode.FILE -> getFromFile(type)
    }

    fun loadFromFile(file: File) {
        mode = Mode.FILE
        this.file = file
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
        val types = arrayOf(
            ContentType.TEMPERATURE,
            ContentType.PRESSURE,
            ContentType.DUST,
            ContentType.VOLTAGE,
            ContentType.LATITUDE,
            ContentType.LONGITUDE,
            ContentType.TIME,
            ContentType.HEIGHT,
            ContentType.INTERNAL_TEMPERATURE
        )

        val index = types.indexOf(type)
        val timeIndex = types.indexOf(ContentType.TIME)

        val lines =
            file
                ?.readText()
                ?.lines()
                ?.filter { it.isNotEmpty() }
                ?.map { it.removeSuffix("\\") }
                ?.map { it.split(",").map { it.substringAfter(":") } } ?: return emptyList()

        val dataList = mutableListOf<Data>()
        for (line in lines) {
            val id = UUID.randomUUID().toString()
            val value = line[index].toDoubleOrNull() ?: continue
            val time = SimpleDateFormat("hh;mm;ss;SSS").parse(line[timeIndex])

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
}