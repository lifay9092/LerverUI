package cn.lifay.ui.tree

import cn.lifay.ui.tree.TreeViewCache.DATA_TYPE
import cn.lifay.ui.tree.TreeViewCache.LIST_HELP_MAP
import cn.lifay.ui.tree.TreeViewCache.TREE_HELP_MAP
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import kotlin.reflect.KProperty1

object TreeViewCache {

    val LIST_HELP_MAP = HashMap<String, Pair<KProperty1<*, *>, KProperty1<*, *>>>()
    val TREE_HELP_MAP = HashMap<String, KProperty1<*, List<*>>>()
    var DATA_TYPE = DataType.LIST
    enum class DataType{
        LIST,
        TREE
    }

}
/*树视图部分*/

val <T> TreeView<T>.treeId: String
    get() {
        return this.id ?: this.hashCode().toString()
    }


/**
 * 通过集合类型的数据源，注册当前TreeView视图,初始化树结构
 */
inline fun <reified V : Any, B : Any> TreeView<V>.Register(
    idProp: KProperty1<V, B>,
    parentProp: KProperty1<V, B>,
    datas: List<V>
) {
    //添加到缓存
    DATA_TYPE = TreeViewCache.DataType.LIST
    LIST_HELP_MAP[treeId] = Pair(idProp, parentProp)
    initList<V, B>(this.root, ListProps(), datas)
}

/**
 * 通过树类型的数据源，注册当前TreeView视图,初始化树结构
 */
inline fun <reified V> TreeView<V>.Register(
    childrenProp: KProperty1<V, List<V>>,
    datas: List<V>
) {
    DATA_TYPE = TreeViewCache.DataType.TREE
    TREE_HELP_MAP[treeId] = childrenProp
    initTree(this.root, TreeProps(), datas)
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
inline fun <reified V,reified B> TreeView<V>.RefreshTree(
    datas: List<V>
) {
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
        val prop = TreeProps()
        //获取子节点
        val childtren = datas.map {
            val item = TreeItem(it)
            initTree(item, prop, prop.get(it))
            item
        }
        //添加子节点
        if (childtren.isNotEmpty()) {
            this.root.children.addAll(childtren)
        }
    }

}

inline fun <reified V> TreeView<V>.TreeProps(): KProperty1<V, List<V>> {
    return TREE_HELP_MAP[treeId] as KProperty1<V, List<V>>
}

/**
 * 将子元素列表添加到指定item下（递归）
 */
fun <V> TreeView<V>.initTree(
    panTreeItem: TreeItem<V>,
    prop: KProperty1<V, List<V>>,
    datas: List<V>
) {
    //获取子节点
    val childtren = datas.map {
        val item = TreeItem(it)
        initTree(item, prop, prop.get(it))
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
}

/**
 * item添加子元素
 */
fun <V> TreeItem<V>.AddChild(
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
fun <V> TreeItem<V>.AddChild(
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

