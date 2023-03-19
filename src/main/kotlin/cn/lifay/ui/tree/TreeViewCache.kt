package cn.lifay.ui.tree

import cn.lifay.ui.tree.TreeViewCache.DATA_TYPE
import cn.lifay.ui.tree.TreeViewCache.ITEM_TO_TREE_MAP
import cn.lifay.ui.tree.TreeViewCache.LIST_HELP_MAP
import cn.lifay.ui.tree.TreeViewCache.TREE_HELP_MAP
import cn.lifay.ui.tree.TreeViewCache.TREE_TO_ITEM_MAP
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import kotlin.reflect.KProperty1

object TreeViewCache {

    val LIST_HELP_MAP = HashMap<String, Pair<KProperty1<*, *>, KProperty1<*, *>>>()
    val TREE_HELP_MAP = HashMap<String, Pair<KProperty1<*, *>, KProperty1<*, List<*>>>>()
    var DATA_TYPE = DataType.LIST

    enum class DataType {
        LIST,
        TREE
    }

    val ITEM_TO_TREE_MAP = HashMap<Int, String>()
    val TREE_TO_ITEM_MAP = HashMap<String, ArrayList<Int>>()
}
/*树视图部分*/

val <T> TreeView<T>.treeId: String
    get() {
        return this.hashCode().toString()
    }

var <T> TreeItem<T>.treeViewId: String
    get() {
        return ITEM_TO_TREE_MAP[this.hashCode()]!!
    }
    set(value) {
        ITEM_TO_TREE_MAP[this.hashCode()] = value
        var ints = TREE_TO_ITEM_MAP[value]
        if (ints == null) {
            ints = arrayListOf()
        }
        ints.add(this.hashCode())
        TREE_TO_ITEM_MAP[value] = ints
    }


/**
 * 通过集合类型的数据源，注册当前TreeView视图,初始化树结构
 */
@JvmName("RegisterByList")
inline fun <reified V : Any, B : Any> TreeView<V>.Register(
    idProp: KProperty1<V, B>,
    parentProp: KProperty1<V, B>,
    datas: List<V>
) {
    //添加到缓存
    this.root.treeViewId = treeId
    DATA_TYPE = TreeViewCache.DataType.LIST
    LIST_HELP_MAP[treeId] = Pair(idProp, parentProp)
    initList<V, B>(this.root, ListProps(), datas)
}

/**
 * 通过树类型的数据源，注册当前TreeView视图,初始化树结构
 */
@JvmName("RegisterByTree")
inline fun <reified V : Any, B : Any> TreeView<V>.Register(
    idProp: KProperty1<V, B>,
    childrenProp: KProperty1<V, List<V>>,
    datas: List<V>
) {
    DATA_TYPE = TreeViewCache.DataType.TREE
    TREE_HELP_MAP[treeId] = Pair(idProp, childrenProp)
    initTree(this.root, idProp, childrenProp, datas)
}

inline fun <reified V, B> TreeView<V>.ListProps(): Pair<KProperty1<V, B>, KProperty1<V, B>> {
    return LIST_HELP_MAP[treeId] as Pair<KProperty1<V, B>, KProperty1<V, B>>
}

/**
 * 将子元素列表添加到指定item下（递归）
 */
fun <V, B> TreeView<V>.initList(
    panTreeItem: TreeItem<V>,
    prop: Pair<KProperty1<V, B>, KProperty1<V, B>>,
    datas: List<V>
) {
    //获取子节点
    val childtren = datas.filter { prop.first.get(panTreeItem.value!!) == prop.second.get(it) }.map {
        val item = TreeItem(it)
        item.treeViewId = treeId
        initList(item, prop, datas)
        item
    }
    //添加子节点
    if (childtren.isNotEmpty()) {
        panTreeItem.children.addAll(childtren)
    }
}

/**
 * 刷新元素列表
 */
inline fun <reified V, reified B> TreeView<V>.RefreshTree(
    datas: List<V>
) {
    //println("RefreshTree")
    //清除旧数据和缓存
    this.root.children.clear()
    clearTreeIdMap(treeId)
    //重载
    if (DATA_TYPE == TreeViewCache.DataType.LIST) {
        val prop = ListProps<V, B>()
        //获取子节点
        val childtren = datas.filter { prop.first.get(this.root.value!!) == prop.second.get(it) }.map {
            val item = TreeItem(it)
            initList(item, prop, datas)
            item
        }
        //添加子节点
        if (childtren.isNotEmpty()) {
            this.root.children.addAll(childtren)
        }
    } else {
        val props = TreeProps<V, B>()
        //获取子节点
        val childtren = datas.map {
            val item = TreeItem(it)
            initTree(item, props.first, props.second, props.second.get(it))
            item
        }
        //添加子节点
        if (childtren.isNotEmpty()) {
            this.root.children.addAll(childtren)
        }
    }

}

fun clearTreeIdMap(treeId: String) {
    TREE_TO_ITEM_MAP[treeId]?.forEach {
        ITEM_TO_TREE_MAP.remove(it)
    }
    TREE_TO_ITEM_MAP.remove(treeId)
}

inline fun <reified V, reified B> TreeView<V>.TreeProps(): Pair<KProperty1<V, B>, KProperty1<V, List<V>>> {
    return TREE_HELP_MAP[treeId] as Pair<KProperty1<V, B>, KProperty1<V, List<V>>>
}

/**
 * 将子元素列表添加到指定item下（递归）
 */
fun <V, B> TreeView<V>.initTree(
    panTreeItem: TreeItem<V>,
    idProp: KProperty1<V, B>,
    childrenProp: KProperty1<V, List<V>>,
    datas: List<V>
) {
    //获取子节点
    val childtren = datas.map {
        val item = TreeItem(it)
        initTree(item, idProp, childrenProp, childrenProp.get(it))
        item
    }
    //添加子节点
    if (childtren.isNotEmpty()) {
        panTreeItem.children.addAll(childtren)
    }
}

fun <V> TreeView<V>.ClearCache() {
    LIST_HELP_MAP[treeId].let {
        LIST_HELP_MAP.remove(treeId)
    }
    TREE_HELP_MAP[treeId].let {
        TREE_HELP_MAP.remove(treeId)
    }
    clearTreeIdMap(treeId)
}

/**
 * item添加子元素
 */
fun <V> TreeItem<V>.AddChild(
    data: V
) {
    //获取子节点
    val child = TreeItem(data)
    //添加子节点
    children.add(child)

    this.isExpanded = true
}

/**
 * item添加子元素
 */
fun <V : Any> TreeItem<V>.UpdateChild(
    data: V
) {
    if (DATA_TYPE == TreeViewCache.DataType.LIST) {
        val idProp = LIST_HELP_MAP[this.treeViewId]!!.first as KProperty1<V, *>
        for (child in children) {
            if (idProp.get(child.value) == idProp.get(data)) {
                child.value = data
                break
            }
        }
    } else {
        val idProp = TREE_HELP_MAP[this.treeViewId]!!.first as KProperty1<V, *>
        for (child in children) {
            if (idProp.get(child.value) == idProp.get(data)) {
                child.value = data
                break
            }
        }
    }
    this.isExpanded = true
}

/**
 * item添加子元素
 */
fun <V> TreeItem<V>.AddChildren(
    vararg datas: V
) {
    for (data in datas) {
        //获取子节点
        val child = TreeItem(data)
        //添加子节点
        children.add(child)
    }
    this.isExpanded = true
}

/**
 * item添加子元素
 */
fun <V> TreeItem<V>.AddChildrenList(
    datas: List<V>
) {
    for (data in datas) {
        //获取子节点
        val child = TreeItem(data)
        //添加子节点
        children.add(child)
    }
    this.isExpanded = true
}

/**
 * item添加子元素
 */
fun <V> TreeItem<V>.UpdateItem(
    data: V
) {
    this.value = data
}


/**
 * item添加子元素
 */
fun <V> TreeItem<V>.DeleteThis() {
    this.parent.children?.remove(this)
}


/**
 * item添加子元素
 */
fun <V> TreeItem<V>.DeleteChildItem(
    block: (V) -> Boolean
) {
    this.children?.removeIf {
        block(it.value)
    }
}

