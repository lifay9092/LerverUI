package cn.lifay.ui.tree

import TreeViewCache.ITEM_TO_TREE_MAP
import cn.lifay.mq.EventBus
import cn.lifay.mq.EventBusId
import cn.lifay.mq.event.BodyEvent
import javafx.scene.control.TreeItem
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1


/**
 * 树节点属性(List:id prentId)：主键-泛型P、子列表-泛型T、是否叶子节点bool
 */
class LerverTreeNodeListProp<T : Any, P : Any>(
    val idProp: KProperty1<T, P>,
    val parentIdProp: KProperty1<T, P>,
    val leafProp: KProperty1<T, Boolean>? = null,
) {
}

/**
 * 树节点属性(List:id children)：主键-泛型P、子列表-泛型T、是否叶子节点bool
 */
class LerverTreeNodeTreeProp<T : Any, P : Any>(
    val idProp: KProperty1<T, P>,
    val childrenProp: KMutableProperty1<T, ArrayList<T>?>,
    val leafProp: KProperty1<T, Boolean>? = null,
) {
}

enum class LerverTreeDataType {
    LIST,
    TREE
}

/*
    数据节点，用来缓存TreeItem数据和筛选后迅速还原
 */
class LerverTreeTempNode<T : Any>(
    val entity: T,
    var children: Collection<LerverTreeTempNode<T>>?
) {
    constructor(entity: T) : this(entity, null) {
    }

    override fun toString(): String {
        return "TempDataNode(entity=$entity, children=$children)"
    }

}

enum class LerverTreeBusId : EventBusId {

    REFRESH_NODE_LIST,

    ITEM_UPT,
    ITEM_UPT_CHILD,

    ITEM_ADD_LIST,

    ITEM_DEL,

}

/* TreeItem操作事件实体类*/
data class LerverTreeItemEventCodeBody(
    val codes: List<Int>
)

data class LerverTreeItemEventValueBody<T>(
    val code: Int,
    val value: T
)

data class LerverTreeItemEventListBody<T>(
    val code: Int,
    val list: List<T>
)

/**
 * 获取 TreeItem 的自定义树id
 */
var <T> TreeItem<T>.treeViewId: String
    get() {
        return ITEM_TO_TREE_MAP[this.hashCode()]!!.treeId
    }
    set(value) {
//        ITEM_TO_TREE_MAP[this.hashCode()] = value
//        var ints = TREE_TO_ITEM_MAP[value]
//        if (ints == null) {
//            ints = arrayListOf()
//        }
//        ints.add(this.hashCode())
//        TREE_TO_ITEM_MAP[value] = ints

    }

/**
 * 获取 TreeItem 的 TreeView对象
 */
var <T> TreeItem<T>.treeView: LerverTreeView<*, *>
    get() {
        return ITEM_TO_TREE_MAP[this.hashCode()]!!
    }
    set(value) {
        ITEM_TO_TREE_MAP[this.hashCode()] = value
    }

/**
 * 判断当前节点是否可以进行加载子节点操作
 * true 未指定leaf属性，或者leaf=false,并且children为空
 * false 指定了leaf并且leaf=true
 */
fun <T : Any> TreeItem<T>.AllowLoadChildren(): Boolean {
    if (children.isNotEmpty()) {
        return false
    }
    val treeView = ITEM_TO_TREE_MAP[this.hashCode()]!!
    val leafProp = when (treeView.DATA_TYPE) {
        LerverTreeDataType.LIST -> treeView.TREE_NODE_LIST_PROP!!.leafProp
        LerverTreeDataType.TREE -> treeView.TREE_NODE_TREE_PROP!!.leafProp
    }
    if (leafProp != null) {
        val isLeaf = (leafProp as KProperty1<T, Boolean>).get(this.value!!)
        if (!isLeaf) {
            return true
        }
        return false
    } else {
        return true
    }
}

/**
 * 为item添加子元素
 */
fun <T : Any> TreeItem<T>.AddChildren(
    vararg datas: T
) {
    EventBus.publish(
        BodyEvent(
            "${LerverTreeBusId.ITEM_ADD_LIST}_${this.treeViewId}",
            LerverTreeItemEventListBody(hashCode(), datas.toList())
        )
    )
}

/**
 * item添加子元素
 */
fun <T : Any> TreeItem<T>.AddChildrenList(
    datas: List<T>
) {
    EventBus.publish(
        BodyEvent(
            "${LerverTreeBusId.ITEM_ADD_LIST}_${this.treeViewId}",
            LerverTreeItemEventListBody(hashCode(), datas)
        )
    )
}

/**
 * 更新当前item元素
 */
fun <T : Any> TreeItem<T>.UpdateItem(
    data: T
) {
    EventBus.publish(
        BodyEvent(
            "${LerverTreeBusId.ITEM_UPT}_${this.treeViewId}",
            LerverTreeItemEventValueBody(hashCode(), data)
        )
    )
}

//fun <T : Any> TreeItem<T>.CacheBusiIdMap(idPropParam: KProperty1<T, *>? = null) {
//    val treeView = ITEM_TO_TREE_MAP[this.hashCode()]!!
//    if (treeView.DATA_TYPE == DataType.LIST) {
//        val idProp = if (idPropParam != null) idPropParam else treeView.TREE_NODE_LIST_PROP!!.idProp as KProperty1<T, *>
//        val busiId = idProp.get(value).toString()
//
//        treeView.ITEM_BUSI_TO_TREEITEM_MAP[busiId] = this
//        TreeViewCache.TREE_TO_BUSI_TO_MAP[this.treeViewId]!!.add(busiId)
//    } else {
//        val idProp =
//            if (idPropParam != null) idPropParam else TreeViewCache.TREE_NODE_TREE_PROP_MAP[this.treeViewId]!!.idProp as KProperty1<T, Any>
//        val busiId = idProp.get(value).toString()
//        TreeViewCache.ITEM_BUSI_TO_TREEITEM_MAP[busiId] = this
//        TreeViewCache.TREE_TO_BUSI_TO_MAP[this.treeViewId]!!.add(busiId)
//    }
//}

/**
 * 更新item下的子元素
 */
fun <T : Any> TreeItem<T>.UpdateChild(
    data: T
) {
//    val treeView = ITEM_TO_TREE_MAP[this.hashCode()]!!
//    val dataType = treeView.DATA_TYPE
//    if (dataType == DataType.LIST) {
//        val idProp = treeView.TREE_NODE_LIST_PROP!!.idProp as KProperty1<T, *>
//        for (child in children) {
//            if (idProp.get(child.value) == idProp.get(data)) {
//                child.value = data
//                child.CacheBusiIdMap()
//                break
//            }
//        }
//    } else {
//        val idProp = treeView.TREE_NODE_TREE_PROP!!.idProp as KProperty1<T, *>
//        for (child in children) {
//            if (idProp.get(child.value) == idProp.get(data)) {
//                child.value = data
//                child.CacheBusiIdMap()
//                break
//            }
//        }
//    }
//    this.isExpanded = true

    EventBus.publish(
        BodyEvent(
            "${LerverTreeBusId.ITEM_UPT_CHILD}_${this.treeViewId}",
            LerverTreeItemEventValueBody(hashCode(), data)
        )
    )
}

/**
 * 删除当前item元素
 */
fun <T : Any> TreeItem<T>.DeleteThis() {
    EventBus.publish(
        BodyEvent(
            "${LerverTreeBusId.ITEM_DEL}_${this.treeViewId}",
            LerverTreeItemEventCodeBody(listOf(hashCode()))
        )
    )
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
                "${LerverTreeBusId.ITEM_DEL}_${this.treeViewId}",
                LerverTreeItemEventCodeBody(items.map { hashCode() })
            )
        )
    }
}

fun <T> TreeItem<T>.GetTreePath(): String {
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