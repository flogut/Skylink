package de.hgv.view

import tornadofx.*
import java.util.concurrent.ThreadLocalRandom

class ContainerContentView: Fragment() {

    val number = ThreadLocalRandom.current().nextInt(0, 100)

    override val root = label("Content $number")

}
