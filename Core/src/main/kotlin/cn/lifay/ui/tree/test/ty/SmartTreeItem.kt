package cn.lifay.ui.tree.test.ty

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

//
//class SmartTreeItem<T : Any, P : Any>(
//    value: T
//) : TreeItem<T>(value) {
//    var treeViewddd: SmartTreeView<T, P>? = null
//
//    constructor(
//        value: T,
//        treeViewddd: SmartTreeView<T, P>
//    ) : this(value) {
//        this.treeViewddd = treeViewddd
//    }
//}
//

//class SmartCheckBoxTreeItem<T : Any, P : Any>(
//    value: T
//) : CheckBoxTreeItem<T>(value) {
//    var treeViewddd: SmartTreeView<T, P>? = null
//
//    constructor(
//        value: T,
//        treeViewddd: SmartTreeView<T, P>
//    ) : this(value) {
//        this.treeViewddd = treeViewddd
//    }
//}

val ITEM_TO_TREE_MAP_NEW = HashMap<Int, SmartTreeView<*, *>>()

/**
 * 获取 TreeItem 的 TreeView对象
 */
var <T> TreeItem<T>.treeViewddd: SmartTreeView<*, *>
    get() {
        return ITEM_TO_TREE_MAP_NEW[this.hashCode()]!!
    }
    set(value) {
        println("$this ${this.hashCode()} set了 treeViewIdddd:${value.id}")
        ITEM_TO_TREE_MAP_NEW[this.hashCode()] = value
    }

/**
 * 获取 TreeItem 的自定义树id
 */
var <T> TreeItem<T>.treeViewIdddd: String
    get() {
        //println(this.hashCode())
        return treeViewddd.treeId
    }
    set(value) {


    }

inline fun <reified T : Any> TreeItem<T>.AddChild(vararg datas: T) {
    treeViewddd!!.printFunc {
        EventBus.publish(
            BodyEvent(
                "${SmartTreeBusId.ITEM_ADD_LIST}_${this.treeViewIdddd}",
                SmartTreeItemEventListBody(hashCode(), datas.toList())
            ), false
        )
    }

}

inline fun <reified T : Any> TreeItem<T>.AddChildList(datas: List<T>) {
    this.treeViewddd!!.printFunc {
        EventBus.publish(
            BodyEvent(
                "${SmartTreeBusId.ITEM_ADD_LIST}_${this.treeViewIdddd}",
                SmartTreeItemEventListBody(hashCode(), datas.toList())
            ), false
        )
    }
}

inline fun <reified T : Any> TreeItem<T>.UptItem(data: T) {
    this.treeViewddd!!.printFunc {
        EventBus.publish(
            BodyEvent(
                "${SmartTreeBusId.ITEM_UPT}_${this.treeViewIdddd}",
                SmartTreeItemEventValueBody(hashCode(), data)
            ), false
        )
    }

}

inline fun <reified T : Any> TreeItem<T>.removeNode() {
    this.treeViewddd!!.printFunc {
        EventBus.publish(
            BodyEvent(
                "${SmartTreeBusId.ITEM_DEL}_${this.treeViewIdddd}",
                SmartTreeItemEventCodeBody(listOf(hashCode()))
            ), false
        )
    }
}
