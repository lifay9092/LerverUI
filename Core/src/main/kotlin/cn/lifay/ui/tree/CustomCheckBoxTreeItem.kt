package cn.lifay.ui.tree

import javafx.scene.Node
import javafx.scene.control.CheckBoxTreeItem

class CustomCheckBoxTreeItem<T : Any>
@JvmOverloads constructor(val treeId: String, entity: T? = null, node: Node? = null) :
    CheckBoxTreeItem<T>(entity, node) {


}