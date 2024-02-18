package cn.lifay.ui.tree

import javafx.collections.ObservableList
import javafx.scene.control.TreeItem

interface CustomTreeItemBase<T> {

    val treeViewId: String

    val children: ObservableList<TreeItem<T>>

    var value: T?
}