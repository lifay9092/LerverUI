package cn.lifay.ui.tree

import javafx.collections.FXCollections
import javafx.scene.control.TreeItem

class LerverTreeItem<T : Any, P : Any>(
    private val data: T,
) : TreeItem<T>(data) {
    //是否加载过子节点
    var isChildrenLoaded = false

    //是否正在加载子节点
    private var isLoading = false
    private val originalChildren = FXCollections.observableArrayList<TreeItem<T>>()
    private var isFiltered = false

    fun addOriginalChild(item: TreeItem<T>) {
        originalChildren.add(item)
        if (!isFiltered) super.getChildren().add(item)
    }

    fun removeOriginalChild(item: TreeItem<T>) {
        originalChildren.remove(item)
        if (!isFiltered) super.getChildren().remove(item)
    }

    fun applyFilter(predicate: (T) -> Boolean) {
        isFiltered = true
        super.getChildren().setAll(
            originalChildren
                .filter { it is LerverTreeItem<*, *> }
                .map { child ->
                    (child as LerverTreeItem<T, P>).apply { applyFilter(predicate) }
                    if (child.isAnyMatch(predicate)) child else null
                }
                .filterNotNull()
        )
    }

    fun clearFilter() {
        isFiltered = false
        super.getChildren().setAll(originalChildren)
        originalChildren.forEach {
            (it as? LerverTreeItem<T, P>)?.clearFilter()
        }
    }

    private fun isAnyMatch(predicate: (T) -> Boolean): Boolean {
        return predicate(data) || originalChildren.any {
            (it as? LerverTreeItem<T, P>)?.isAnyMatch(predicate) == true
        }
    }


//    private fun updateChildren(children: List<T>) {
//        val newItems = children.map { childValue ->
//            LerverTreeItem(childValue, fetchChildren)
//        }
//        super.getChildren().setAll(newItems)
//    }
}