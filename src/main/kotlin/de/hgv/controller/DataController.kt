package de.hgv.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import de.hgv.app.CloudlinkApi
import de.hgv.data.ContentType
import de.hgv.model.Data
import org.apache.logging.log4j.LogManager
import tornadofx.*

class DataController: Controller() {

    val api: CloudlinkApi by inject()

    fun downloadData(type: ContentType): List<Data> {
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

    companion object {
        private val LOGGER = LogManager.getLogger(DataController::class.java)
    }

}