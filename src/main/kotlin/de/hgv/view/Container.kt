package de.hgv.view

import de.hgv.data.ContentType
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Orientation
import javafx.scene.control.SplitPane
import javafx.scene.layout.Priority
import tornadofx.*

class Container(parent: Container? = null, splitOrientation: Orientation = Orientation.VERTICAL): Fragment() {
    private val parentProperty = SimpleObjectProperty(parent)
    private var parent by parentProperty

    private val splitOrientationProperty = SimpleObjectProperty(splitOrientation)
    private var splitOrientation: Orientation by splitOrientationProperty

    private val innerSplitOrientationProperty = SimpleObjectProperty(splitOrientation.invert())
    private var innerSplitOrientation: Orientation by innerSplitOrientationProperty

    private var innerSplitPane by singleAssign<SplitPane>()

    private val innerChildProperty = SimpleObjectProperty<Container>()
    private var innerChild: Container? by innerChildProperty

    private val outerChildProperty = SimpleObjectProperty<Container>()
    private var outerChild: Container? by outerChildProperty

    private val contentView = ContentView(ContentType.PICTURE)


    override val root = splitpane {
        orientationProperty().bind(splitOrientationProperty)
        useMaxWidth = true
        hgrow = Priority.ALWAYS

        innerSplitPane = splitpane {
            orientationProperty().bind(innerSplitOrientationProperty)
            borderpane {
                top {
                    vbox(3.0) {
                        hbox {
                            combobox<ContentType>(contentView.typeProperty, ContentType.values().toList())

                            //TODO Add icons for buttons
                            //TODO Unify height of header components

                            button("+") {
                                enableWhen { innerChildProperty.isNull }

                                setOnAction {
                                    addChild()
                                }
                            }

                            button("X") {
                                enableWhen { parentProperty.isNotNull }

                                setOnAction {
                                    closeContainer()
                                }
                            }
                        }

                        separator(Orientation.HORIZONTAL)
                    }
                }

                center {
                    add(contentView)
                }
            }
        }

    }

    init {
        splitOrientationProperty.onChange {
            innerSplitOrientation = it?.invert() ?: Orientation.VERTICAL
        }

        currentStage?.isMaximized = true
    }

    private fun addChild() {
        if (outerChild == null) {
            val child = Container(this, splitOrientation.invert())
            root.add(child.root)
            outerChild = child
        } else if (innerChild == null) {
            val child = Container(this, splitOrientation)
            innerSplitPane.add(child.root)
            innerChild = child
        }
    }

    private fun addChild(child: Container) {
        if (outerChild == null) {
            child.parent = this
            root.add(child.root)
            outerChild = child
        } else if (innerChild == null) {
            child.parent = this
            innerSplitPane.add(child.root)
            innerChild = child
        } else {
            outerChild?.addChild(child)
        }
    }

    private fun closeContainer() {
        parent?.removeChild(this)
    }

    private fun removeChild(child: Container, rearrange: Boolean = true) {
        val removedOuter = child == outerChild
        if (removedOuter) {
            root.items.remove(child.root)
            outerChild = null
        } else if (child == innerChild) {
            innerSplitPane.items.remove(child.root)
            innerChild = null
        }

        if (!rearrange) return

        child.outerChild?.let { outer ->
            val inner = child.innerChild
            if (inner == null) {
                outer.invertOrientation()
                addChild(outer)
                outer.reverseChildren()
            } else {
                addChild(inner)
                inner.addChild(outer)
            }
        }

        if (removedOuter) {
            innerChild?.let {
                val ic = it
                removeChild(it)
                addChild(ic)
            }
        }

        setOrientation()
    }

    private fun invertOrientation() {
        splitOrientation = splitOrientation.invert()

        outerChild?.invertOrientation()
        innerChild?.invertOrientation()
    }

    private fun setOrientation() {
        outerChild?.splitOrientation = splitOrientation.invert()
        outerChild?.setOrientation()

        innerChild?.splitOrientation = splitOrientation
        innerChild?.setOrientation()
    }

    private fun reverseChildren() {
        val oc = outerChild
        val ic = innerChild

        if (ic != null && oc != null) {
            innerSplitPane.items.remove(ic.root)
            root.items.remove(oc.root)

            root.add(ic)
            innerSplitPane.add(oc)

            innerChild = oc
            outerChild = ic
        }
    }

    private fun Orientation.invert() =
        if (this == Orientation.VERTICAL) Orientation.HORIZONTAL else Orientation.VERTICAL
}