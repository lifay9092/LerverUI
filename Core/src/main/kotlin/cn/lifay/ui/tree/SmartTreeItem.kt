package cn.lifay.ui.tree

import cn.lifay.mq.BaseEventBusId
import cn.lifay.mq.EventBus
import cn.lifay.mq.event.BodyEvent
import javafx.scene.control.TreeItem

enum class SmartTreeBusId : BaseEventBusId {

    ITEM_UPT,
    ITEM_UPT_CHILD,

    ITEM_ADD_LIST,
    ITEM_DEL,
}

/* TreeItem操作事件实体类*/
data class SmartTreeItemEventCodeBody(
    val codes: List<Int>
)

data class SmartTreeItemEventValueBody<T>(
    val code: Int,
    val value: T
)

data class SmartTreeItemEventListBody<T>(
    val code: Int,
    val list: List<T>
)

val ITEM_TO_TREE_MAP = HashMap<Int, LerverTreeView<*, *>>()

/**
 * 获取 TreeItem 的 TreeView对象
 */
var <T> TreeItem<T>.treeView: LerverTreeView<*, *>
    get() {
        return ITEM_TO_TREE_MAP[this.hashCode()]!!
    }
    set(value) {
//        println("$this ${this.hashCode()} set了 treeViewIdddd:${value.id}")
        ITEM_TO_TREE_MAP[this.hashCode()] = value
    }

/**
 * 获取 TreeItem 的自定义树id
 */
var <T> TreeItem<T>.treeViewId: String
    get() {
        //println(this.hashCode())
        return treeView.treeId
    }
    set(value) {
    }

inline fun <reified T : Any> TreeItem<T>.AddChildren(vararg datas: T) {
    //treeView!!.printFunc {
        EventBus.publish(
            BodyEvent(
                "${SmartTreeBusId.ITEM_ADD_LIST}_${this.treeViewId}",
                SmartTreeItemEventListBody(hashCode(), datas.toList())
            ), false
        )
    //  }

}

inline fun <reified T : Any> TreeItem<T>.AddChildrenList(datas: List<T>) {
    // this.treeView!!.printFunc {
        EventBus.publish(
            BodyEvent(
                "${SmartTreeBusId.ITEM_ADD_LIST}_${this.treeViewId}",
                SmartTreeItemEventListBody(hashCode(), datas.toList())
            ), false
        )
    //  }
}

inline fun <reified T : Any> TreeItem<T>.UpdateItem(data: T) {
    // this.treeView!!.printFunc {
        EventBus.publish(
            BodyEvent(
                "${SmartTreeBusId.ITEM_UPT}_${this.treeViewId}",
                SmartTreeItemEventValueBody(hashCode(), data)
            ), false
        )
    //  }

}

// 删除当前节点
inline fun <reified T : Any> TreeItem<T>.DeleteThis() {
    // this.treeView!!.printFunc {
        EventBus.publish(
            BodyEvent(
                "${SmartTreeBusId.ITEM_DEL}_${this.treeViewId}",
                SmartTreeItemEventCodeBody(listOf(hashCode()))
            ), false
        )
    // }
}

/**
 * 根据过滤条件删除当前item下的子元素
 */
fun <T : Any> TreeItem<T>.DeleteChildItem(
    block: (T) -> Boolean
) {
    this.children?.let {
        val items = it.filter {
            block(it.value)
        }.toList()
        EventBus.publish(
            BodyEvent(
                "${SmartTreeBusId.ITEM_DEL}_${this.treeViewId}",
                SmartTreeItemEventCodeBody(items.map { it.hashCode() })
            ), false
        )
    }
}

// 获取当前节点的完整路径
fun <T> TreeItem<T>.GetTreeFullPath(): String {
    val treePath = StringBuffer()
    getParentPath(this, treePath)
    treePath.append("/${this.value.toString()}")
    return treePath.toString()
}

private fun <T> getParentPath(treeItem: TreeItem<T>, treePath: StringBuffer) {
    treeItem.parent?.let {
        getParentPath(it, treePath)
        treePath.append("/${it.value.toString()}")
    }
}