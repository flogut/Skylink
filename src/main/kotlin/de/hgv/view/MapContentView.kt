package de.hgv.view

import com.gluonhq.charm.down.plugins.Position
import com.gluonhq.maps.MapLayer
import com.gluonhq.maps.MapPoint
import com.gluonhq.maps.MapView
import com.gluonhq.maps.demo.PoiLayer
import de.hgv.controller.WebSocketController
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import tornadofx.*

class MapContentView: Fragment() {
    private val webSocketController: WebSocketController by inject()
    private val dataWebSocket = webSocketController.dataWebSocket

    private val mapView = MapView()

    private val positionProperty = dataWebSocket.positionProperty
    private var position: Position by positionProperty

    override val root = vbox {
        add(mapView)
    }

    init {
        mapView.addLayer(positionLayer())
        mapView.setZoom(10.0)
    }

    private fun positionLayer(): MapLayer {
        val mapPoint = MapPoint(position.latitude, position.longitude)

        val layer = PoiLayer()
        layer.addPoint(mapPoint, Circle(7.0, Color.RED))

        positionProperty.onChange { newPosition ->
            if (newPosition != null) {
                mapPoint.update(newPosition.latitude, newPosition.longitude)
//                mapView.flyTo(0.0, mapPoint, 0.05)
                mapView.setCenter(mapPoint)
            }
        }

        mapView.setCenter(mapPoint)

        return layer
    }
}
