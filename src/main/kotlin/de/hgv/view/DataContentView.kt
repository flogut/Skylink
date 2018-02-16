package de.hgv.view

import de.hgv.controller.DataController
import de.hgv.controller.WebSocketController
import de.hgv.data.ContentType
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.scene.chart.CategoryAxis
import javafx.scene.chart.LineChart
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import tornadofx.*

class DataContentView: Fragment() {
    val type = params["type"] as ContentType

    val webSocketController: WebSocketController by inject()

    val dataWebSocket = webSocketController.dataWebSocket
    var chart: LineChart<String, Number> by singleAssign()

    val dataController: DataController by inject()

    val dataList = mutableListOf<Pair<String, Number>>().observable()

    override val root = vbox {
        chart = linechart(type.toString(), CategoryAxis(), NumberAxis()) {
            //TODO Use Custom X Axis that doesn't show the date
            useMaxSize = true
            animated = false

            data(type.toString(), dataList)
        }
    }

    init {
        dataWebSocket.onType(type) { data ->
            dataList.add(data.time.toString() to data.value)
        }

        runAsync {
            dataController.downloadData(type)
        } ui { data ->
            data.mapTo(dataList) { it.time.toString() to it.value }
        }
    }

    private fun <X, Y> XYChart<X, Y>.data(seriesName: String, data: ObservableList<Pair<X, Y>>) {
        data.addListener { change: ListChangeListener.Change<out Pair<X, Y>> ->
            if (change.next()) {
                runLater {
                    val series = this.data.find { it.name == seriesName }
                    val addition = change.addedSubList.first()
                    if (series != null) {
                        series.data.add(XYChart.Data(addition.first, addition.second))
                    } else {
                        series(seriesName) {
                            data(addition.first, addition.second)
                        }
                    }
                }
            }
        }
    }
}
