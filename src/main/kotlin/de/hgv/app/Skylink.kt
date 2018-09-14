package de.hgv.app

import com.gluonhq.charm.down.ServiceFactory
import com.gluonhq.charm.down.Services
import com.gluonhq.charm.down.plugins.StorageService
import de.hgv.controller.WebSocketController
import de.hgv.view.MainView
import javafx.scene.image.Image
import tornadofx.*
import java.io.File
import java.util.*

class Skylink: App(Image(Skylink::class.java.getResourceAsStream("/icon.png")), MainView::class, Styles::class) {
//class Skylink: App(MainView::class, Styles::class) {

    private val webSocketController: WebSocketController by inject()


    init {
        importStylesheet("/stylesheet.css")

        System.setProperty("javafx.platform", "Desktop")

        val storageService = object: StorageService {
            override fun getPrivateStorage(): Optional<File> = Optional.of(File(System.getProperty("user.home")))

            override fun getPublicStorage(subdirectory: String?): Optional<File> = privateStorage

            override fun isExternalStorageReadable(): Boolean = privateStorage.get().canRead()

            override fun isExternalStorageWritable(): Boolean = privateStorage.get().canWrite()
        }

        val storageServiceFactory = object: ServiceFactory<StorageService> {
            override fun getServiceType(): Class<StorageService> {
                return StorageService::class.java
            }

            override fun getInstance(): Optional<StorageService> {
                return Optional.of(storageService)
            }
        }

        Services.registerServiceFactory(storageServiceFactory)
    }

    override fun stop() {
        super.stop()
        webSocketController.shutdown()

        System.exit(0)
    }

}