import cn.lifay.ui.tree.TestTreeViewKT

//package cn.lifay.ui.tree
//
//import cn.lifay.extension.asyncTask
//import cn.lifay.mq.EventBus
//import cn.lifay.mq.event.DefaultEvent
//import cn.lifay.ui.tree.TreeViewCache.ITEM_TO_TREE_MAP
//import cn.lifay.ui.tree.TreeViewCache.TREE_CHECKBOX_MAP
//import cn.lifay.ui.tree.TreeViewCache.TREE_DATA_CALL_MAP
//import cn.lifay.ui.tree.TreeViewCache.TREE_DATA_NODE_LIST_MAP
//import cn.lifay.ui.tree.TreeViewCache.TREE_DATE_TYPE_MAP
//import cn.lifay.ui.tree.TreeViewCache.TREE_IMG_CALL_MAP
//import cn.lifay.ui.tree.TreeViewCache.TREE_NODE_LIST_PROP_MAP
//import cn.lifay.ui.tree.TreeViewCache.TREE_NODE_TREE_PROP_MAP
//import cn.lifay.ui.tree.TreeViewCache.TREE_TO_BUSI_TO_MAP
//import cn.lifay.ui.tree.TreeViewCache.TREE_TO_ITEM_MAP
//import javafx.scene.control.CheckBoxTreeItem
//import javafx.scene.control.TreeItem
//import javafx.scene.control.TreeView
//import kotlin.reflect.KProperty1
//
//
object TreeViewCache {
    /* 定义属性缓存 */
    // treeViewId=prop属性
//    val TREE_NODE_LIST_PROP_MAP = HashMap<String, TreeNodeListProp<*, *>>()
//    val TREE_NODE_TREE_PROP_MAP = HashMap<String, TreeNodeTreeProp<*, *>>()
//    val TREE_DATE_TYPE_MAP = HashMap<String, DataType>()
//
//    // treeViewId=获取初始化数据的函数
//    val TREE_DATA_CALL_MAP = HashMap<String, () -> List<*>>()
//
//    // treeViewId=获取图标的函数
//    val TREE_IMG_CALL_MAP = HashMap<String, (TreeItem<*>) -> Unit>()
//
//    // treeViewId=CheckBox标识
//    val TREE_CHECKBOX_MAP = HashMap<String, Boolean>()

    /*数据缓存*/
    // treeItem的hascode=TreeViewId
    val ITEM_TO_TREE_MAP = HashMap<Int, TestTreeViewKT<*,*>>()

    // TreeViewId=treeItem的hascode数组
    val TREE_TO_ITEM_MAP = HashMap<String, ArrayList<Int>>()

    // value业务ID=treeItem 辅助提供ID直接查询
//    val ITEM_BUSI_TO_TREEITEM_MAP = HashMap<String, TreeItem<*>>()
//    val TREE_TO_BUSI_TO_MAP = HashMap<String, ArrayList<String>>()

    // TreeViewId=数据节点,用来缓存TreeItem数据和筛选后迅速还原,TreeView数据变更后进行刷新
//    val TREE_DATA_NODE_LIST_MAP = HashMap<String, TempDataNode<*>>()



}
//
///*树视图部分*/
//
///**
// * 获取 TreeView 的自定义树id
// */
////val <T> TreeView<T>.treeId: String
////    get() {
////        return this.hashCode().toString()
////    }
//
///**
// * 获取 TreeItem 的自定义树id
// */
//var <T> TreeItem<T>.treeViewId: String
//    get() {
//
//        return ITEM_TO_TREE_MAP[this.hashCode()]!!
//    }
//    set(value) {
//        ITEM_TO_TREE_MAP[this.hashCode()] = value
////        var ints = TREE_TO_ITEM_MAP[value]
////        if (ints == null) {
////            ints = arrayListOf()
////        }
////        ints.add(this.hashCode())
////        TREE_TO_ITEM_MAP[value] = ints
//
//    }
//
///**
// * 通过集合列表类型的数据源，注册当前TreeView视图,id和父id属性、获取数据的函数
// * @param idProp id属性引用
// * @param parentProp parentId属性引用
// * @param init 是否注册后立即初始化
// * @param checkBox 构建勾选框的树
// * @param imgCall 获取树元素的图标
// * imgCall = {
// *                 if ("GROUP" == it.value.type) {
// *                     it.graphic = FontIcon(Feather.FOLDER)
// *                 }
// *             }
// * @param getInitDataCall 获取初始化数据的回调函数
// *        val test1 = TreeListVO("3", "2", "4", SimpleStringProperty("3"))
// *        val test2 = TreeListVO("2", "1", "2", SimpleStringProperty("2"))
// *        val test3 = TreeListVO("4", "1", "4", SimpleStringProperty("4"))
// *        listOf(test1,test2,test3)
// */
////@JvmName("RegisterByList")
////inline fun <reified V : Any, reified B : Any> TreeView<V>.Register(
////    idProp: KProperty1<V, B>,
////    parentProp: KProperty1<V, B>,
////    init: Boolean = false,
////    checkBox: Boolean = false,
////    noinline imgCall: ((TreeItem<V>) -> Unit)? = null,
////    noinline getInitDataCall: () -> List<V>,
////) {
////    //添加到缓存
////    this.root.treeViewId = treeId
////    TREE_DATE_TYPE_MAP[treeId] = TreeViewCache.DataType.LIST
////    LIST_HELP_MAP[treeId] = Pair(idProp, parentProp)
////    TREE_DATA_CALL_MAP[treeId] = getInitDataCall
////    TREE_CHECKBOX_MAP[treeId] = checkBox
////    imgCall?.let {
////        TREE_IMG_CALL_MAP[treeId] = it as (TreeItem<*>) -> Unit
////    }
////    if (init) {
////        RefreshTree<V, B>()
////    }
////}
//@JvmName("RegisterByList")
//inline fun <reified V : Any, reified B : Any> TreeView<V>.RegisterByList(
//    treeNodeTreeProp: TreeNodeListProp<V, B>,
//    checkBox: Boolean = false,
//    init: Boolean = false,
//    noinline imgCall: ((TreeItem<V>) -> Unit)? = null,
//    noinline initDataCall: () -> List<V>
//) {
//    //判断
//    if (TREE_DATE_TYPE_MAP.containsKey(treeId)) {
//        throw Exception("TreeView已经注册过,请勿重复注册")
//    }
//    //添加到缓存
//    this.root.treeViewId = treeId
//
//    TREE_DATE_TYPE_MAP[treeId] = TreeViewCache.DataType.LIST
//    TREE_NODE_LIST_PROP_MAP[treeId] = treeNodeTreeProp
//    TREE_DATA_CALL_MAP[treeId] = initDataCall
//
//    TREE_CHECKBOX_MAP[treeId] = checkBox
//
//    imgCall?.let {
//        TREE_IMG_CALL_MAP[treeId] = it as (TreeItem<*>) -> Unit
//    }
//    if (init) {
//        asyncTask {
//            RefreshTree<V, B>()
//
//            EventBus.subscribe("${TreeBusId.REFRESH_NODE_LIST}_$treeId", DefaultEvent::class) {
//                refreshNodeList(this)
//            }
//            EventBus.publish(DefaultEvent("${TreeBusId.REFRESH_NODE_LIST}_${treeId}"))
//        }
//    }
//}
//
///**
// * 通过树类型嵌套的数据源，注册当前TreeView视图,id和children属性、获取数据的函数
// * @param idProp id属性引用
// * @param childrenProp children属性引用
// * @param init 是否注册后立即初始化
// * @param checkBox 构建勾选框的树
// * @param imgCall 获取树元素的图标
// * imgCall = {
// *                 if ("GROUP" == it.value.type) {
// *                     it.graphic = FontIcon(Feather.FOLDER)
// *                 }
// *             }
// * @param getInitDataCall 获取初始化数据的回调函数
// * listOf(
// *                     TreeTreeVO(
// *                         "1", "0", "1", arrayListOf(
// *                             TreeTreeVO(
// *                                 "2", "1", "2", arrayListOf(
// *                                     TreeTreeVO("4", "2", "4", null)
// *                                 )
// *                             ),
// *                             TreeTreeVO("3", "1", "3", null)
// *                         )
// *                     )
// *                 )
// */
////@JvmName("RegisterByTree")
////inline fun <reified V : Any, reified B : Any> TreeView<V>.Register(
////    idProp: KProperty1<V, B>,
////    childrenProp: KProperty1<V, List<V>?>,
////    init: Boolean = false,
////    checkBox: Boolean = false,
////    noinline imgCall: ((TreeItem<V>) -> Unit)? = null,
////    noinline getInitDataCall: () -> List<V>
////) {
////    DATA_TYPE = TreeViewCache.DataType.TREE
////    TREE_HELP_MAP[treeId] = Pair(idProp, childrenProp)
////    TREE_DATA_CALL_MAP[treeId] = getInitDataCall
////    TREE_CHECKBOX_MAP[treeId] = checkBox
////    imgCall?.let {
////        TREE_IMG_CALL_MAP[treeId] = it as (TreeItem<*>) -> Unit
////    }
////    if (init) {
////        RefreshTree<V, B>()
////    }
////}
//
//@JvmName("RegisterByTree")
//inline fun <reified V : Any, reified B : Any> TreeView<V>.RegisterByTree(
//    treeNodeTreeProp: TreeNodeTreeProp<V, B>,
//    checkBox: Boolean = false,
//    init: Boolean = false,
//    noinline imgCall: ((TreeItem<V>) -> Unit)? = null,
//    noinline initDataCall: () -> List<V>
//) {
//    //判断
//    if (TREE_DATE_TYPE_MAP.containsKey(treeId)) {
//        throw Exception("TreeView已经注册过,请勿重复注册")
//    }
//    //添加到缓存
//    this.root.treeViewId = treeId
//
//    TREE_DATE_TYPE_MAP[treeId] = TreeViewCache.DataType.TREE
//    TREE_NODE_TREE_PROP_MAP[treeId] = treeNodeTreeProp
//    TREE_DATA_CALL_MAP[treeId] = initDataCall
//
//    TREE_CHECKBOX_MAP[treeId] = checkBox
//    imgCall?.let {
//        TREE_IMG_CALL_MAP[treeId] = it as (TreeItem<*>) -> Unit
//    }
//
//    if (init) {
//        asyncTask {
//            RefreshTree<V, B>()
//
//            EventBus.subscribe("${TreeBusId.REFRESH_NODE_LIST}_$treeId", DefaultEvent::class) {
//                refreshNodeList(this)
//            }
//            EventBus.publish(DefaultEvent("${TreeBusId.REFRESH_NODE_LIST}_${treeId}"))
//        }
//    }
//}
//
///**
// * 重新加载树,相当于新建树节点,数据将被全部清理！！,可重新传入初始化数据的函数
// */
//inline fun <reified V : Any, reified B : Any> TreeView<V>.ResetInitDataCall(
//    noinline getInitDataCall: () -> List<V>
//) {
//    TREE_DATA_CALL_MAP[treeId] = getInitDataCall
//
//}
///**
// * 根据注册的[获取数据的函数],获取基础数据
// */
//inline fun <reified V : Any> TreeView<V>.initDatas(): List<V> {
//    return TREE_DATA_CALL_MAP[treeId]!!.invoke() as List<V>
//}
//
///**
// * 重新加载树,相当于新建树节点,数据将被全部清理！！,可重新传入初始化数据的函数
// */
//inline fun <reified V : Any, reified B : Any> TreeView<V>.RefreshTree(
//    noinline filterFunc: ((V) -> Boolean)? = null
//) {
//    //println("RefreshTree")
//
//    //根据初始化数据函数获取数据
//    val datas = initDatas()
//    //清除旧item的数据和缓存
//    this.root.children.clear()
//    clearTreeItemMap(treeId)
//    TREE_TO_BUSI_TO_MAP[this.treeId] = arrayListOf()
//
//    this.root.treeViewId = treeId
//    val imgCall = TREE_IMG_CALL_MAP[treeId]
//    //重载
//    if (TREE_DATE_TYPE_MAP[treeId] == TreeViewCache.DataType.LIST) {
//        val prop = listProps<V, B>()
//        initList(this.root, prop, datas, filterFunc, imgCall)
//    } else {
//        val props = treeProps<V, B>()
//        val childtren = datas.map {
//            val item = newTreeItem(treeId, it)
//            item.treeViewId = treeId
//            initTree(item, props.first, props.second, filterFunc, imgCall)
//            item
//        }.filter {
//            if (filterFunc == null || filterFunc(it.value)) {
//                return@filter true
//            }
//            it.children.isNotEmpty()
//        }
//        //添加子节点
//        if (childtren.isNotEmpty()) {
//            this.root.children.addAll(childtren)
//        }
//    }
//}
//
///**
// * 过滤树,临时处理,节点数据不会被清理
// */
//inline fun <reified V : Any, reified B : Any> TreeView<V>.FilterTree(
//    noinline filterFunc: ((V) -> Boolean)? = null
//) {
//    println("FilterTree")
//    //根据缓存数据函数获取数据
//    val tempRootDataNode = TREE_DATA_NODE_LIST_MAP[treeId]
//    //清除旧item的数据和缓存
//    this.root.children.clear()
//    clearTreeItemMap(treeId)
//    TREE_TO_BUSI_TO_MAP[this.treeId] = arrayListOf()
//
//    this.root.treeViewId = treeId
//    val imgCall = TREE_IMG_CALL_MAP[treeId]
//
//    //组织树数据
//    val idProp = when (TREE_DATE_TYPE_MAP[treeId]) {
//        TreeViewCache.DataType.LIST -> {
//            listProps<V, B>().first
//        }
//
//        else -> {
//            treeProps<V, B>().first
//        }
//    }
//    val childtren = tempRootDataNode!!.children?.map {
//        val item = newTreeItem(treeId, it.entity as V)
//        item.treeViewId = treeId
//        initFilterTree(item, idProp, it.children, filterFunc, imgCall)
//        item
//    }?.filter {
//        if (filterFunc == null || filterFunc(it.value)) {
//            return@filter true
//        }
//        it.children.isNotEmpty()
//    }
//    //添加子节点
//    if (childtren.isNullOrEmpty()) {
//        return
//    }
//    this.root.children.addAll(childtren)
//}
//
//inline fun <reified V, B> TreeView<V>.listProps(): Pair<KProperty1<V, B>, KProperty1<V, B>> {
//    val props = TREE_NODE_LIST_PROP_MAP[treeId]!!
//    return Pair(props.idProp, props.parentIdProp) as Pair<KProperty1<V, B>, KProperty1<V, B>>
//}
//
///**
// * 将子元素列表添加到指定item下（递归）
// */
//fun <V : Any, B : Any> TreeView<V>.initList(
//    panTreeItem: TreeItem<V>,
//    prop: Pair<KProperty1<V, B>, KProperty1<V, B>>,
//    datas: List<V>,
//    filterFunc: ((V) -> Boolean)? = null,
//    imgCall: ((TreeItem<*>) -> Unit)?
//) {
//    //获取子节点
//    val childtren = datas
//        .filter { prop.first.get(panTreeItem.value!!) == prop.second.get(it) }
//        .map {
//            val item = newTreeItem(treeId, it)
//            item.treeViewId = treeId
//            if (imgCall != null) {
//                imgCall(item)
//            }
//            initList(item, prop, datas, filterFunc, imgCall)
//            item.CacheBusiIdMap(prop.first)
//            item
//        }.filter {
//            if (filterFunc == null || filterFunc(it.value)) {
//                return@filter true
//            }
//            it.children.isNotEmpty()
//        }
//    //添加子节点
//    if (childtren.isNotEmpty()) {
//        panTreeItem.children.addAll(childtren)
////        //添加到关键字缓存
////        panTreeItem.keywordStr =
////            panTreeItem.value.toString() + "[" + childtren.map { it.value.toString() }.joinToString(",") + "]"
//    } else {
//        //添加到关键字缓存
//        // panTreeItem.keywordStr = panTreeItem.value.toString()
//    }
//}
//
//inline fun <reified V, reified B> TreeView<V>.treeProps(): Pair<KProperty1<V, B>, KProperty1<V, List<V>?>> {
//    val props = TREE_NODE_TREE_PROP_MAP[treeId]!!
//    return Pair(props.idProp, props.childrenProp) as Pair<KProperty1<V, B>, KProperty1<V, List<V>?>>
//}
//
///**
// * 将子元素列表添加到指定item下（递归）
// */
//fun <V : Any, B : Any> TreeView<V>.initTree(
//    panTreeItem: TreeItem<V>,
//    idProp: KProperty1<V, B>,
//    childrenProp: KProperty1<V, List<V>?>,
//    filterFunc: ((V) -> Boolean)? = null,
//    imgCall: ((TreeItem<*>) -> Unit)?
//) {
//    //获取子节点
//    val datas = childrenProp.get(panTreeItem.value)
//    if (datas != null) {
//        val childtren = datas.map {
//            val item = newTreeItem(treeId, it)
//            item.treeViewId = panTreeItem.treeViewId
//            if (imgCall != null) {
//                imgCall(item)
//            }
//            initTree(item, idProp, childrenProp, filterFunc, imgCall)
//            item.CacheBusiIdMap(idProp)
//            item
//        }.filter {
//            if (filterFunc == null || filterFunc(it.value)) {
//                return@filter true
//            }
//            it.children.isNotEmpty()
//        }
//        //添加子节点
//        if (childtren.isNotEmpty()) {
//            panTreeItem.children.addAll(childtren)
//        }
//    }
//
//}
//
///**
// * 将子元素列表添加到指定item下（递归）
// */
//fun <V : Any, B : Any> TreeView<V>.initFilterTree(
//    panTreeItem: TreeItem<V>,
//    idProp: KProperty1<V, B>,
//    tempDataNodeChildren: Collection<TreeViewCache.TempDataNode<out Any>>?,
//    filterFunc: ((V) -> Boolean)? = null,
//    imgCall: ((TreeItem<*>) -> Unit)?
//) {
//    //获取子节点
//    if (tempDataNodeChildren != null) {
//        val newChildtren = tempDataNodeChildren.map { empDataNode ->
//            val item = newTreeItem(treeId, empDataNode.entity as V)
//            item.treeViewId = panTreeItem.treeViewId
//            if (imgCall != null) {
//                imgCall(item)
//            }
//            empDataNode.children?.let {
//                initFilterTree(item, idProp, it, filterFunc, imgCall)
//            }
//            item.CacheBusiIdMap(idProp)
//            item
//        }.filter {
//            if (filterFunc == null || filterFunc(it.value)) {
//                return@filter true
//            }
//            it.children.isNotEmpty()
//        }
//        //添加子节点
//        if (newChildtren.isNotEmpty()) {
//            panTreeItem.children.addAll(newChildtren)
//        }
//    }
//
//}
//
///**
// * 获取TreeItem实例，根据CheckBox标识返回,否则勾选框会失去联动效果
// */
//fun <V : Any> newTreeItem(treeId: String, it: V): TreeItem<V> {
//    val isCheckBox = TREE_CHECKBOX_MAP[treeId]
//    if (isCheckBox == true) {
//        return CheckBoxTreeItem(it)
//    } else {
//        return TreeItem(it)
//    }
//}
////
/////**
//// * 判断当前节点是否可以进行加载子节点操作
//// * true 未指定leaf属性，或者leaf=false,并且children为空
//// * false 指定了leaf并且leaf=true
//// */
////fun <V : Any> TreeItem<V>.CanLoadChildren(): Boolean {
////    if (children.isNotEmpty()) {
////        return false
////    }
////    val leafProp = when (TREE_DATE_TYPE_MAP[this.treeViewId]) {
////        TreeViewCache.DataType.LIST -> TREE_NODE_LIST_PROP_MAP[this.treeViewId]!!.leafProp
////        TreeViewCache.DataType.TREE -> TREE_NODE_TREE_PROP_MAP[this.treeViewId]!!.leafProp
////        null -> TREE_NODE_LIST_PROP_MAP[this.treeViewId]!!.leafProp
////    }
////    if (leafProp != null) {
////        val isLeaf = (leafProp as KProperty1<V, Boolean>).get(this.value)
////        if (!isLeaf) {
////            return true
////        }
////        return false
////    } else {
////        return true
////    }
////}
////
/////**
//// * 清理TreeView的缓存数据，窗口关闭的时候回调此方法
//// */
////fun <V> TreeView<V>.ClearCache() {
////    println("ClearCache")
////    clearTreeViewMap(treeId)
////    clearTreeItemMap(treeId)
////}
////
////private fun clearTreeViewMap(treeId: String) {
////    println("clearTreeViewMap")
////    TREE_NODE_LIST_PROP_MAP[treeId].let {
////        TREE_NODE_LIST_PROP_MAP.remove(treeId)
////    }
////    TREE_NODE_TREE_PROP_MAP[treeId].let {
////        TREE_NODE_TREE_PROP_MAP.remove(treeId)
////    }
////    TREE_DATE_TYPE_MAP.remove(treeId)
////
////    TREE_DATA_CALL_MAP.remove(treeId)
////    TREE_IMG_CALL_MAP.remove(treeId)
////    TREE_CHECKBOX_MAP.remove(treeId)
////
////    TREE_TO_ITEM_MAP.remove(treeId)
////    TREE_DATA_NODE_LIST_MAP.remove(treeId)
////}
////fun clearTreeItemMap(treeId: String) {
////    println("clearTreeItemMap")
////    TREE_TO_ITEM_MAP[treeId]?.forEach {
////        ITEM_TO_TREE_MAP.remove(it)
////    }
////    TREE_TO_ITEM_MAP.remove(treeId)
////
////    TREE_TO_BUSI_TO_MAP[treeId]?.apply {
////        this.forEach {
////            ITEM_BUSI_TO_TREEITEM_MAP.remove(it)
////        }
////        TREE_TO_BUSI_TO_MAP.remove(treeId)
////    }
////}
////
/////**
//// * 更新item下的子元素
//// */
////fun <V : Any> TreeItem<V>.UpdateChild(
////    data: V
////) {
////    if (TREE_DATE_TYPE_MAP[this.treeViewId] == TreeViewCache.DataType.LIST) {
////        val idProp = TREE_NODE_LIST_PROP_MAP[this.treeViewId]!!.idProp as KProperty1<V, *>
////        for (child in children) {
////            if (idProp.get(child.value) == idProp.get(data)) {
////                child.value = data
////                child.CacheBusiIdMap()
////                break
////            }
////        }
////    } else {
////        val idProp = TREE_NODE_TREE_PROP_MAP[this.treeViewId]!!.idProp as KProperty1<V, *>
////        for (child in children) {
////            if (idProp.get(child.value) == idProp.get(data)) {
////                child.value = data
////                child.CacheBusiIdMap()
////                break
////            }
////        }
////    }
////    this.isExpanded = true
////
////    EventBus.publish(DefaultEvent("${TreeBusId.REFRESH_NODE_LIST}_${this.treeViewId}"))
////
////}
////
/////**
//// * 为item添加子元素
//// */
////fun <V> TreeItem<V>.AddChildren(
////    vararg datas: V
////) {
////    val imgCall = TREE_IMG_CALL_MAP[this.treeViewId]
////    for (data in datas) {
////        //获取子节点
////        val child = TreeItem(data)
////        imgCall?.let { it(child) }
////        child.treeViewId = this.treeViewId
////        child.CacheBusiIdMap()
////        //添加子节点
////        children.add(child)
////    }
////    this.isExpanded = true
////
////    EventBus.publish(DefaultEvent("${TreeBusId.REFRESH_NODE_LIST}_${this.treeViewId}"))
////}
////
/////**
//// * item添加子元素
//// */
////fun <V> TreeItem<V>.AddChildrenList(
////    datas: List<V>
////) {
////    val imgCall = TREE_IMG_CALL_MAP[this.treeViewId]
////    for (data in datas) {
////        //获取子节点
////        val child = TreeItem(data)
////        imgCall?.let { it(child) }
////        child.treeViewId = this.treeViewId
////        child.CacheBusiIdMap()
////        //添加子节点
////        children.add(child)
////    }
////    this.isExpanded = true
////
////    EventBus.publish(DefaultEvent("${TreeBusId.REFRESH_NODE_LIST}_${this.treeViewId}"))
////}
////
/////**
//// * 更新当前item元素
//// */
////fun <V> TreeItem<V>.UpdateItem(
////    data: V
////) {
////    this.value = data
////    val imgCall = TREE_IMG_CALL_MAP[this.treeViewId]
////    imgCall?.let { it(this) }
////    CacheBusiIdMap()
////
////    EventBus.publish(DefaultEvent("${TreeBusId.REFRESH_NODE_LIST}_${this.treeViewId}"))
////}
////
////
////fun <V> TreeView<V>.GetItemByBusiId(id: String): TreeItem<V>? {
////    val item = ITEM_BUSI_TO_TREEITEM_MAP[id] ?: return null
////    return item as? TreeItem<V>
////}
////
////fun <V> TreeItem<V>.CacheBusiIdMap(idPropParam: KProperty1<V, Any>? = null) {
////    if (TREE_DATE_TYPE_MAP[this.treeViewId] == TreeViewCache.DataType.LIST) {
////        val idProp = if (idPropParam != null) idPropParam else TREE_NODE_LIST_PROP_MAP[this.treeViewId]!!.idProp as KProperty1<V, Any>
////        val busiId = idProp.get(value).toString()
////        ITEM_BUSI_TO_TREEITEM_MAP[busiId] = this
////
////        TREE_TO_BUSI_TO_MAP[this.treeViewId]!!.add(busiId)
////    } else {
////        val idProp = if (idPropParam != null) idPropParam else TREE_NODE_TREE_PROP_MAP[this.treeViewId]!!.idProp as KProperty1<V, Any>
////        val busiId = idProp.get(value).toString()
////        ITEM_BUSI_TO_TREEITEM_MAP[busiId] = this
////        TREE_TO_BUSI_TO_MAP[this.treeViewId]!!.add(busiId)
////    }
////}
////
/////**
//// * 删除当前item元素
//// */
////fun <V> TreeItem<V>.DeleteThis() {
////    this.parent?.children?.remove(this)
////
////    EventBus.publish(DefaultEvent("${TreeBusId.REFRESH_NODE_LIST}_${this.treeViewId}"))
////}
////
////
/////**
//// * 根据过滤条件删除当前item下的子元素
//// */
////fun <V> TreeItem<V>.DeleteChildItem(
////    block: (V) -> Boolean
////) {
////    this.children?.let {
////        it.removeIf {
////            block(it.value)
////        }
////        EventBus.publish(DefaultEvent("${TreeBusId.REFRESH_NODE_LIST}_${this.treeViewId}"))
////    }
////}
////
////fun <V> TreeItem<V>.GetTreePath(): String {
////    val treePath = StringBuffer()
////    getParentPath(this, treePath)
////    treePath.append("/${this.value.toString()}")
////    return treePath.toString()
////}
////
////fun <V> getParentPath(treeItem: TreeItem<V>, treePath: StringBuffer) {
////    treeItem.parent?.let {
////        getParentPath(it, treePath)
////        treePath.append("/${it.value.toString()}")
////    }
////}
////
/////*
////    刷新节点数据
//// */
////fun <V: Any> refreshNodeList(treeView: TreeView<V>){
////    asyncTask {
////        println("正在刷新节点数据集:${treeView.id}")
////        val root = treeView.root
////        val rootDataNode = TreeViewCache.TempDataNode(root.value)
////        rootDataNode.children = root.children.map {
////            treeItemToNode(it)
////        }.toList()
////        TREE_DATA_NODE_LIST_MAP[treeView.treeId] = rootDataNode
////
////        println(rootDataNode)
////    }
////}
////
////private fun <V : Any> treeItemToNode(treeItem: TreeItem<V>):TreeViewCache.TempDataNode<V>{
////    val dataNode = TreeViewCache.TempDataNode(treeItem.value)
////    if (treeItem.children.isNotEmpty()) {
////        dataNode.children = treeItem.children.map {
////            treeItemToNode(it)
////        }.toList()
////    }
////    return dataNode
////}