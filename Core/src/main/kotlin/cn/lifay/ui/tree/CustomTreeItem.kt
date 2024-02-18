package cn.lifay.ui.tree

import javafx.collections.ObservableList
import javafx.scene.Node
import javafx.scene.control.TreeItem

class CustomTreeItem<T : Any>
@JvmOverloads constructor(override val treeViewId: String, entity: T? = null, node: Node? = null) :
    TreeItem<T>(entity, node), CustomTreeItemBase<T> {


    lateinit var BUSI_ID: String

    init {

    }

    override val children: ObservableList<TreeItem<T>>
        get() = this.getChildren()

    override var value: T?
        get() = this.getValue()
        set(value) {
            this.setValue(value)
        }


    fun d() {

    }
}