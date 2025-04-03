package cn.lifay.temp

import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView

class TreeViewOperator<T>(private val treeView: TreeView<T>) {
    fun addNode(parent: TreeItem<T>?, item: T) {
        val newItem = TreeItem(item)
        parent?.children?.add(newItem) ?: run {
            treeView.root.children.add(newItem)
        }
        treeView.refresh()
    }

    fun updateNode(item: TreeItem<T>, newValue: T) {
        item.value = newValue
        treeView.refresh()
    }

    fun deleteNode(item: TreeItem<T>) {
        findParent(item)?.children?.remove(item)
        treeView.refresh()
    }

    private fun findParent(item: TreeItem<T>): TreeItem<T>? {
        fun searchParent(current: TreeItem<T>): TreeItem<T>? {
            if (current.children.contains(item)) return current
            current.children.forEach {
                searchParent(it)?.let { return it }
            }
            return null
        }
        return searchParent(treeView.root)
    }
}

// 扩展函数
fun <T> TreeView<T>.createOperator(): TreeViewOperator<T> {
    return TreeViewOperator(this)
}