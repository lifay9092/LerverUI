package cn.lifay.ui.tree

import TreeViewCache.ITEM_TO_TREE_MAP
import cn.lifay.mq.EventBus
import cn.lifay.mq.EventBusId
import cn.lifay.mq.event.DefaultEvent
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1


/**
 * 树节点属性(List:id prentId)：主键-泛型P、子列表-泛型T、是否叶子节点bool
 */
class TreeNodeListProp<T : Any, P : Any>(
    val idProp: KProperty1<T, P>,
    val parentIdProp: KProperty1<T, P>,
    val leafProp: KProperty1<T, Boolean>? = null,
) {
}

/**
 * 树节点属性(List:id children)：主键-泛型P、子列表-泛型T、是否叶子节点bool
 */
class TreeNodeTreeProp<T : Any, P : Any>(
    val idProp: KProperty1<T, P>,
    val childrenProp: KMutableProperty1<T, ArrayList<T>?>,
    val leafProp: KProperty1<T, Boolean>? = null,
) {
}

enum class DataType {
    LIST,
    TREE
}

/*
    数据节点，用来缓存TreeItem数据和筛选后迅速还原
 */
class TempDataNode<T : Any>(
    val entity: T,
    var children: Collection<TempDataNode<T>>?
) {
    constructor(entity: T) : this(entity, null) {
    }

    override fun toString(): String {
        return "TempDataNode(entity=$entity, children=$children)"
    }

}

enum class TreeBusId : EventBusId {

    REFRESH_NODE_LIST

}

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
 * 获取 TreeItem 的自定义树id
 */
var <T> TreeItem<T>.treeView: TestTreeViewKT<*,*>
    get() {
        return ITEM_TO_TREE_MAP[this.hashCode()]!!
    }
    set(value) {
        ITEM_TO_TREE_MAP[this.hashCode()] = value
//        var ints = TREE_TO_ITEM_MAP[value]
//        if (ints == null) {
//            ints = arrayListOf()
//        }
//        ints.add(this.hashCode())
//        TREE_TO_ITEM_MAP[value] = ints

    }

/**
 * 判断当前节点是否可以进行加载子节点操作
 * true 未指定leaf属性，或者leaf=false,并且children为空
 * false 指定了leaf并且leaf=true
 */
fun <T : Any> TreeItem<T>.CanLoadChildren(): Boolean {
    if (children.isNotEmpty()) {
        return false
    }
    val treeView = ITEM_TO_TREE_MAP[this.hashCode()]!!
    val leafProp = when (treeView.DATA_TYPE) {
        DataType.LIST -> treeView.TREE_NODE_LIST_PROP!!.leafProp
        DataType.TREE -> treeView.TREE_NODE_TREE_PROP!!.leafProp
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
 * 清理TreeView的缓存数据，窗口关闭的时候回调此方法
 */
fun <T> TreeView<T>.ClearCache() {
    println("ClearCache")
    clearTreeViewMap(treeId)
    clearTreeItemMap(treeId)
}


/**
 * 更新item下的子元素
 */
fun <T : Any> TreeItem<T>.UpdateChild(
    data: T
) {
    val treeView = ITEM_TO_TREE_MAP[this.hashCode()]!!
    val dataType = treeView.DATA_TYPE
    if (dataType == DataType.LIST) {
        val idProp = treeView.TREE_NODE_LIST_PROP!!.idProp as KProperty1<T, *>
        for (child in children) {
            if (idProp.get(child.value) == idProp.get(data)) {
                child.value = data
                child.CacheBusiIdMap()
                break
            }
        }
    } else {
        val idProp = treeView.TREE_NODE_TREE_PROP!!.idProp as KProperty1<T, *>
        for (child in children) {
            if (idProp.get(child.value) == idProp.get(data)) {
                child.value = data
                child.CacheBusiIdMap()
                break
            }
        }
    }
    this.isExpanded = true

    EventBus.publish(DefaultEvent("${TreeBusId.REFRESH_NODE_LIST}_${this.treeViewId}"))

}

/**
 * 为item添加子元素
 */
fun <T : Any> TreeItem<T>.AddChildren(
    vararg datas: T
) {
    val treeView = ITEM_TO_TREE_MAP[this.hashCode()]!!
    for (data in datas) {
        //获取子节点
        val child = TreeItem(data)
        treeView.loadImg(child)
        child.treeViewId = this.treeViewId
        child.CacheBusiIdMap()
        //添加子节点
        children.add(child)
    }
    this.isExpanded = true

    EventBus.publish(DefaultEvent("${TreeBusId.REFRESH_NODE_LIST}_${this.treeViewId}"))
}

/**
 * item添加子元素
 */
fun <T> TreeItem<T>.AddChildrenList(
    datas: List<T>
) {
    val treeView = this.treeView
    for (data in datas) {
        //获取子节点
        val child = TreeItem(data)
        treeView.loadImg(child)

        child.treeViewId = this.treeViewId
        child.CacheBusiIdMap()
        //添加子节点
        children.add(child)
    }
    this.isExpanded = true

    EventBus.publish(DefaultEvent("${TreeBusId.REFRESH_NODE_LIST}_${this.treeViewId}"))
}

/**
 * 更新当前item元素
 */
fun <T> TreeItem<T>.UpdateItem(
    data: T
) {
    this.value = data
    val treeView = this.treeView
    treeView.loadImg(this)
    CacheBusiIdMap()

    EventBus.publish(DefaultEvent("${TreeBusId.REFRESH_NODE_LIST}_${this.treeViewId}"))
}

fun <V> TreeItem<V>.CacheBusiIdMap(idPropParam: KProperty1<V, Any>? = null) {
    if (TreeViewCache.TREE_DATE_TYPE_MAP[this.treeViewId] == TreeViewCache.DataType.LIST) {
        val idProp =
            if (idPropParam != null) idPropParam else TreeViewCache.TREE_NODE_LIST_PROP_MAP[this.treeViewId]!!.idProp as KProperty1<V, Any>
        val busiId = idProp.get(value).toString()
        TreeViewCache.ITEM_BUSI_TO_TREEITEM_MAP[busiId] = this

        TreeViewCache.TREE_TO_BUSI_TO_MAP[this.treeViewId]!!.add(busiId)
    } else {
        val idProp =
            if (idPropParam != null) idPropParam else TreeViewCache.TREE_NODE_TREE_PROP_MAP[this.treeViewId]!!.idProp as KProperty1<V, Any>
        val busiId = idProp.get(value).toString()
        TreeViewCache.ITEM_BUSI_TO_TREEITEM_MAP[busiId] = this
        TreeViewCache.TREE_TO_BUSI_TO_MAP[this.treeViewId]!!.add(busiId)
    }
}

/**
 * 删除当前item元素
 */
fun <V> TreeItem<V>.DeleteThis() {
    this.parent?.children?.remove(this)

    EventBus.publish(DefaultEvent("${TreeBusId.REFRESH_NODE_LIST}_${this.treeViewId}"))
}


/**
 * 根据过滤条件删除当前item下的子元素
 */
fun <V> TreeItem<V>.DeleteChildItem(
    block: (V) -> Boolean
) {
    this.children?.let {
        it.removeIf {
            block(it.value)
        }
        EventBus.publish(DefaultEvent("${TreeBusId.REFRESH_NODE_LIST}_${this.treeViewId}"))
    }
}

fun <V> TreeItem<V>.GetTreePath(): String {
    val treePath = StringBuffer()
    getParentPath(this, treePath)
    treePath.append("/${this.value.toString()}")
    return treePath.toString()
}

fun <V> getParentPath(treeItem: TreeItem<V>, treePath: StringBuffer) {
    treeItem.parent?.let {
        getParentPath(it, treePath)
        treePath.append("/${it.value.toString()}")
    }
}