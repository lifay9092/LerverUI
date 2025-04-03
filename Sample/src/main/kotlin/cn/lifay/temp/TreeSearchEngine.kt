package cn.lifay.temp

import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView

class TreeSearchEngine<T>(private val treeView: TreeView<T>) {
    private val originalItems = mutableListOf<TreeItem<T>>()

    fun search(keyword: String, matcher: (T, String) -> Boolean) {
        if (originalItems.isEmpty()) {
            originalItems.addAll(treeView.root.children)
        }

        val filtered = originalItems.filter { item ->
            depthFirstSearch(item, keyword, matcher)
        }

        treeView.root.children.setAll(filtered)
    }

    private fun depthFirstSearch(
        item: TreeItem<T>,
        keyword: String,
        matcher: (T, String) -> Boolean
    ): Boolean {
        val matchSelf = matcher(item.value, keyword)
        val childrenMatches = item.children.any { child ->
            depthFirstSearch(child, keyword, matcher)
        }

        item.isExpanded = matchSelf || childrenMatches
        return matchSelf || childrenMatches
    }
}

// 扩展函数
fun <T> TreeView<T>.createSearchEngine(): TreeSearchEngine<T> {
    return TreeSearchEngine(this)
}