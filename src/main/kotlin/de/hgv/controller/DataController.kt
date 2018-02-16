package de.hgv.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import de.hgv.app.CloudlinkApi
import de.hgv.data.ContentType
import de.hgv.model.Data
import tornadofx.*

class DataController: Controller() {

    val api: CloudlinkApi by inject()

    fun downloadData(type: ContentType): List<Data> {
        //TODO Add error handling
        val response = api.get("data?type=${type.toString().toLowerCase()}")

        val mapper = jacksonObjectMapper()

        return mapper.readValue<List<Data>>(response.content()).sortedBy { it.time }
    }

}