package cn.lifay.ui.tree

import javafx.scene.Node
import javafx.scene.control.TreeItem

open class CustomTreeItem<T> @JvmOverloads constructor(
    var1: T? = null,
    var2: Node? = null
): TreeItem<T>(var1,var2) {


}