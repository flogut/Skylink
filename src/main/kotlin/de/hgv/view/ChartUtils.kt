package de.hgv.view

import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.scene.chart.XYChart
import tornadofx.*

fun <X, Y> XYChart<X, Y>.data(seriesName: String, data: ObservableList<Pair<X, Y>>) {
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