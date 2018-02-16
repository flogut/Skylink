package de.hgv.view

import de.hgv.controller.DataController
import de.hgv.controller.WebSocketController
import de.hgv.data.ContentType
import javafx.scene.chart.LineChart
import javafx.scene.chart.NumberAxis
import tornadofx.*

class DataContentView: Fragment() {
    val type = params["type"] as ContentType

    val webSocketController: WebSocketController by inject()

    val dataWebSocket = webSocketController.dataWebSocket
    var chart: LineChart<Number, Number> by singleAssign()

    val dataController: DataController by inject()

    val dataList = mutableListOf<Pair<Number, Number>>().observable()

    override val root = vbox {
        chart = linechart(type.toString(), NumberAxis(), NumberAxis()) {
            useMaxSize = true
            animated = false

            data(type.toString(), dataList)

            xAxis.isTickLabelsVisible = false
            xAxis.isAutoRanging = true
            (xAxis as NumberAxis).isForceZeroInRange = false
        }
    }

    init {
        dataWebSocket.onType(type) { data ->
            dataList.add(data.time.time to data.value)
        }

        chart.runAsyncWithOverlay {
            dataController.downloadData(type)
        } ui { data ->
            data.mapTo(dataList) { it.time.time to it.value }
        }
    }
}
