package cn.lifay.ui.tree

import cn.lifay.ui.tree.TreeViewCache.DATA_TYPE
import cn.lifay.ui.tree.TreeViewCache.ITEM_BUSI_TO_TREEITEM_MAP
import cn.lifay.ui.tree.TreeViewCache.ITEM_KEYWORD_MAP
import cn.lifay.ui.tree.TreeViewCache.ITEM_TO_TREE_MAP
import cn.lifay.ui.tree.TreeViewCache.LIST_HELP_MAP
import cn.lifay.ui.tree.TreeViewCache.TREE_DATA_CALL_MAP
import cn.lifay.ui.tree.TreeViewCache.TREE_HELP_MAP
import cn.lifay.ui.tree.TreeViewCache.TREE_IMG_CALL_MAP
import cn.lifay.ui.tree.TreeViewCache.TREE_TO_ITEM_MAP
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import kotlin.reflect.KProperty1

object TreeViewCache {
    /* treeViewId=获取数据的函数 */
    val TREE_DATA_CALL_MAP = HashMap<String, () -> List<*>>()

    /* treeViewId=获取图标的函数 */
    val TREE_IMG_CALL_MAP = HashMap<String, (TreeItem<*>) -> Unit>()

    /*k=TreeView的hascode, v=id和父id属性委托 */
    val LIST_HELP_MAP = HashMap<String, Pair<KProperty1<*, *>, KProperty1<*, *>>>()

    /*k=TreeView的hascode, v=id和children属性委托 */
    val TREE_HELP_MAP = HashMap<String, Pair<KProperty1<*, *>, KProperty1<*, List<*>>>>()
    var DATA_TYPE = DataType.LIST

    enum class DataType {
        LIST,
        TREE
    }

    val ITEM_TO_TREE_MAP = HashMap<Int, String>()
    val TREE_TO_ITEM_MAP = HashMap<String, ArrayList<Int>>()
    val ITEM_BUSI_TO_TREEITEM_MAP = HashMap<String, TreeItem<*>>()

    val ITEM_KEYWORD_MAP = HashMap<Int, String>()

}

/*树视图部分*/

/**
 * 获取 TreeView 的自定义树id
 */
val <T> TreeView<T>.treeId: String
    get() {
        return this.hashCode().toString()
    }

/**
 * 获取 TreeItem 的自定义树id
 */
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
 * 获取 TreeItem 的自定义树id
 */
/*
var <T> TreeItem<T>.keywordStr: String
    get() {
        return ITEM_KEYWORD_MAP[this.hashCode()]!!
    }
    set(value) {
        println(this.hashCode())
        ITEM_KEYWORD_MAP[this.hashCode()] = value
    }
*/


/**
 * 通过集合类型的数据源，注册当前TreeView视图,id和父id属性、获取数据的函数
 */
@JvmName("RegisterByList")
inline fun <reified V : Any, reified B : Any> TreeView<V>.Register(
    idProp: KProperty1<V, B>,
    parentProp: KProperty1<V, B>,
    init: Boolean = false,
    noinline imgCall: ((TreeItem<V>) -> Unit)? = null,
    noinline getInitDataCall: () -> List<V>,
) {
    //添加到缓存
    this.root.treeViewId = treeId
    DATA_TYPE = TreeViewCache.DataType.LIST
    LIST_HELP_MAP[treeId] = Pair(idProp, parentProp)
    TREE_DATA_CALL_MAP[treeId] = getInitDataCall
    imgCall?.let {
        TREE_IMG_CALL_MAP[treeId] = it as (TreeItem<*>) -> Unit
    }
    if (init) {
        RefreshTree<V, B>()
    }
}

/**
 * 通过树类型的数据源，注册当前TreeView视图,id和children属性、获取数据的函数
 */
@JvmName("RegisterByTree")
inline fun <reified V : Any, reified B : Any> TreeView<V>.Register(
    idProp: KProperty1<V, B>,
    childrenProp: KProperty1<V, List<V>>,
    init: Boolean = false,
    noinline imgCall: ((TreeItem<V>) -> Unit)? = null,
    noinline getInitDataCall: () -> List<V>
) {
    DATA_TYPE = TreeViewCache.DataType.TREE
    TREE_HELP_MAP[treeId] = Pair(idProp, childrenProp)
    TREE_DATA_CALL_MAP[treeId] = getInitDataCall
    imgCall?.let {
        TREE_IMG_CALL_MAP[treeId] = it as (TreeItem<*>) -> Unit
    }
    if (init) {
        RefreshTree<V, B>()
    }
}

/**
 * 根据注册的[获取数据的函数],获取基础数据
 */
inline fun <reified V : Any> TreeView<V>.initDatas(): List<V> {
    return TREE_DATA_CALL_MAP[treeId]!!.invoke() as List<V>
}

/**
 * 刷新元素列表,可传入指定元素列表，默认为初始化元素列表
 */
inline fun <reified V : Any, reified B : Any> TreeView<V>.RefreshTree(
    noinline getInitDataCall: (() -> List<V>)? = null,
    noinline filterFunc: ((V) -> Boolean)? = null
) {
    //println("RefreshTree")
    if (getInitDataCall != null) {
        TREE_DATA_CALL_MAP[treeId] = getInitDataCall
    }
    val datas = initDatas()
    //清除旧item的数据和缓存
    this.root.children.clear()
    clearTreeItemMap(treeId)
    this.root.treeViewId = treeId
    val imgCall = TREE_IMG_CALL_MAP[treeId]
    //重载
    if (DATA_TYPE == TreeViewCache.DataType.LIST) {
        val prop = listProps<V, B>()
        initList(this.root, prop, datas, filterFunc, imgCall)
    } else {
        val props = treeProps<V, B>()
        val childtren = datas.map {
            val item = TreeItem(it)
            item.treeViewId = treeId
            initTree(item, props.first, props.second, filterFunc, imgCall)
            item
        }.filter {
            if (filterFunc == null || filterFunc(it.value)) {
                return@filter true
            }
            it.children.isNotEmpty()
        }
        //添加子节点
        if (childtren.isNotEmpty()) {
            this.root.children.addAll(childtren)
        }
    }
}

inline fun <reified V, B> TreeView<V>.listProps(): Pair<KProperty1<V, B>, KProperty1<V, B>> {
    return LIST_HELP_MAP[treeId] as Pair<KProperty1<V, B>, KProperty1<V, B>>
}

/**
 * 将子元素列表添加到指定item下（递归）
 */
fun <V, B> TreeView<V>.initList(
    panTreeItem: TreeItem<V>,
    prop: Pair<KProperty1<V, B>, KProperty1<V, B>>,
    datas: List<V>,
    filterFunc: ((V) -> Boolean)? = null,
    imgCall: ((TreeItem<*>) -> Unit)?
) {
    //获取子节点
    val childtren = datas
        .filter { prop.first.get(panTreeItem.value!!) == prop.second.get(it) }
        .map {
            val item = TreeItem(it)
            item.treeViewId = treeId
            if (imgCall != null) {
                imgCall(item)
            }
            initList(item, prop, datas, filterFunc, imgCall)
            val busiId = prop.first.get(it)
            ITEM_BUSI_TO_TREEITEM_MAP[busiId.toString()] = item
            item
        }.filter {
            if (filterFunc == null || filterFunc(it.value)) {
                return@filter true
            }
            it.children.isNotEmpty()
        }
    //添加子节点
    if (childtren.isNotEmpty()) {
        panTreeItem.children.addAll(childtren)
//        //添加到关键字缓存
//        panTreeItem.keywordStr =
//            panTreeItem.value.toString() + "[" + childtren.map { it.value.toString() }.joinToString(",") + "]"
    } else {
        //添加到关键字缓存
        // panTreeItem.keywordStr = panTreeItem.value.toString()
    }
}

inline fun <reified V, reified B> TreeView<V>.treeProps(): Pair<KProperty1<V, B>, KProperty1<V, List<V>>> {
    return TREE_HELP_MAP[treeId] as Pair<KProperty1<V, B>, KProperty1<V, List<V>>>
}

/**
 * 将子元素列表添加到指定item下（递归）
 */
fun <V, B> TreeView<V>.initTree(
    panTreeItem: TreeItem<V>,
    idProp: KProperty1<V, B>,
    childrenProp: KProperty1<V, List<V>>,
    filterFunc: ((V) -> Boolean)? = null,
    imgCall: ((TreeItem<*>) -> Unit)?
) {
    //获取子节点
    val datas = childrenProp.get(panTreeItem.value)
    val childtren = datas.map {
        val item = TreeItem(it)
        item.treeViewId = panTreeItem.treeViewId
        if (imgCall != null) {
            imgCall(item)
        }
        initTree(item, idProp, childrenProp, filterFunc, imgCall)
        val busiId = idProp.get(it)
        ITEM_BUSI_TO_TREEITEM_MAP[busiId.toString()] = item
        item
    }.filter {
        if (filterFunc == null || filterFunc(it.value)) {
            return@filter true
        }
        it.children.isNotEmpty()
    }
    //添加子节点
    if (childtren.isNotEmpty()) {
        panTreeItem.children.addAll(childtren)
    }
}

/**
 * 清理TreeView的缓存数据，窗口关闭的时候回调此方法
 */
fun <V> TreeView<V>.ClearCache() {
//    clearTreeViewMap(treeId)
//    clearTreeItemMap(treeId)
    LIST_HELP_MAP.clear()
    TREE_HELP_MAP.clear()
    TREE_DATA_CALL_MAP.clear()
    TREE_IMG_CALL_MAP.clear()
    TREE_TO_ITEM_MAP.clear()
    ITEM_BUSI_TO_TREEITEM_MAP.clear()
}

fun clearTreeViewMap(treeId: String) {
    LIST_HELP_MAP[treeId].let {
        LIST_HELP_MAP.remove(treeId)
    }
    TREE_HELP_MAP[treeId].let {
        TREE_HELP_MAP.remove(treeId)
    }
    TREE_DATA_CALL_MAP.remove(treeId)
    TREE_IMG_CALL_MAP.remove(treeId)
}

fun clearTreeItemMap(treeId: String) {
    TREE_TO_ITEM_MAP[treeId]?.forEach {
        ITEM_TO_TREE_MAP.remove(it)
        ITEM_KEYWORD_MAP.remove(it)
    }
    TREE_TO_ITEM_MAP.remove(treeId)
}

/**
 * 更新item下的子元素
 */
fun <V : Any> TreeItem<V>.UpdateChild(
    data: V
) {
    if (DATA_TYPE == TreeViewCache.DataType.LIST) {
        val idProp = LIST_HELP_MAP[this.treeViewId]!!.first as KProperty1<V, *>
        for (child in children) {
            if (idProp.get(child.value) == idProp.get(data)) {
                child.value = data
                child.CacheBusiIdMap()
                break
            }
        }
    } else {
        val idProp = TREE_HELP_MAP[this.treeViewId]!!.first as KProperty1<V, *>
        for (child in children) {
            if (idProp.get(child.value) == idProp.get(data)) {
                child.value = data
                child.CacheBusiIdMap()
                break
            }
        }
    }
    this.isExpanded = true
}

/**
 * 为item添加子元素
 */
fun <V> TreeItem<V>.AddChildren(
    vararg datas: V
) {
    val imgCall = TREE_IMG_CALL_MAP[this.treeViewId]
    for (data in datas) {
        //获取子节点
        val child = TreeItem(data)
        imgCall?.let { it(child) }
        child.treeViewId = this.treeViewId
        child.CacheBusiIdMap()
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
    val imgCall = TREE_IMG_CALL_MAP[this.treeViewId]
    for (data in datas) {
        //获取子节点
        val child = TreeItem(data)
        imgCall?.let { it(child) }
        child.treeViewId = this.treeViewId
        child.CacheBusiIdMap()
        //添加子节点
        children.add(child)
    }
    this.isExpanded = true
}

/**
 * 更新当前item元素
 */
fun <V> TreeItem<V>.UpdateItem(
    data: V
) {
    this.value = data
    val imgCall = TREE_IMG_CALL_MAP[this.treeViewId]
    imgCall?.let { it(this) }
    CacheBusiIdMap()
}


fun <V> TreeView<V>.GetItemByBusiId(id: String): TreeItem<V>? {
    val item = ITEM_BUSI_TO_TREEITEM_MAP[id] ?: return null
    return item as? TreeItem<V>
}

fun <V> TreeItem<V>.CacheBusiIdMap() {
    if (DATA_TYPE == TreeViewCache.DataType.LIST) {
        val props = LIST_HELP_MAP[this.treeViewId] as Pair<KProperty1<V, Any>, KProperty1<V, Any>>
        val busiId = props.first.get(value)
        ITEM_BUSI_TO_TREEITEM_MAP[busiId.toString()] = this
    } else {
        val props = TREE_HELP_MAP[this.treeViewId] as Pair<KProperty1<V, Any>, KProperty1<V, List<V>>>
        val busiId = props.first.get(value)
        ITEM_BUSI_TO_TREEITEM_MAP[busiId.toString()] = this
    }
}

/**
 * 删除当前item元素
 */
fun <V> TreeItem<V>.DeleteThis() {
    this.parent?.children?.remove(this)
}


/**
 * 根据过滤条件删除当前item下的子元素
 */
fun <V> TreeItem<V>.DeleteChildItem(
    block: (V) -> Boolean
) {
    this.children?.removeIf {
        block(it.value)
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

