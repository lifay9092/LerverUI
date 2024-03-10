package cn.lifay.ui.tree

import cn.lifay.extension.asyncTask
import cn.lifay.mq.EventBus
import cn.lifay.mq.event.BodyEvent
import cn.lifay.ui.tree.TreeViewCache.ITEM_TO_TREE_MAP
import javafx.scene.control.CheckBoxTreeItem
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import kotlin.reflect.KProperty1

/**
 * T = 实体类的类型
 * P = 实体类中ID、PARENT_ID属性的类型
 */
class LerverTreeView<T : Any, P : Any> : TreeView<T> {
    constructor()
    constructor(treeItem: TreeItem<T>?) : super(treeItem)

    val treeId = this.hashCode().toString()

    var DATA_TYPE = LerverTreeDataType.LIST
    var TREE_NODE_LIST_PROP: LerverTreeNodeListProp<T, P>? = null
    var TREE_NODE_TREE_PROP: LerverTreeNodeTreeProp<T, P>? = null

    var TREE_DATA_CALL: (() -> List<T>)? = null
    var TREE_IMG_CALL: ((TreeItem<T>) -> Unit)? = null

    /* treeViewId=CheckBox标识 */
    var TREE_CHECKBOX: Boolean = false

    // TreeViewId=treeItem的hascode数组
    val ITEM_CODE_LIST = ArrayList<Int>()

    // TreeItem的hashcode=treeItem 辅助提供ID直接查询
    val ITEM_CODE_TO_TREEITEM_MAP = HashMap<Int, TreeItem<T>>()

    // value业务ID=treeItem 辅助提供ID直接查询
    val ITEM_BUSI_TO_TREEITEM_MAP = HashMap<String, TreeItem<T>>()

    //业务ID列表
    val BUSI_ID_LIST = ArrayList<String>()

    // TreeViewId=数据节点,用来缓存TreeItem数据和筛选后迅速还原,TreeView数据变更后进行刷新
    var TREE_ROOT_NODE_DATA: LerverTreeTempNode<T>? = null

    private var IS_REGISTER_EVENT = false

    /**
     * 通过集合列表类型的数据源，注册当前TreeView视图,id和父id属性、获取数据的函数
     * @param idProp id属性引用
     * @param parentProp parentId属性引用
     * @param init 是否注册后立即初始化
     * @param checkBox 构建勾选框的树
     * @param imgCall 获取树元素的图标
     * imgCall = {
     *                 if ("GROUP" == it.value.type) {
     *                     it.graphic = FontIcon(Feather.FOLDER)
     *                 }
     *             }
     * @param getInitDataCall 获取初始化数据的回调函数
     *        val test1 = TreeListVO("3", "2", "4", SimpleStringProperty("3"))
     *        val test2 = TreeListVO("2", "1", "2", SimpleStringProperty("2"))
     *        val test3 = TreeListVO("4", "1", "4", SimpleStringProperty("4"))
     *        listOf(test1,test2,test3)
     */
    fun RegisterByList(
        treeNodeTreeProp: LerverTreeNodeListProp<T, P>,
        checkBox: Boolean = false,
        init: Boolean = false,
        imgCall: ((TreeItem<T>) -> Unit)? = null,
        initDataCall: () -> List<T>
    ) {
        //判断
        if (TREE_DATA_CALL != null) {
            throw Exception("TreeView已经注册过,请勿重复注册")
        }

        TREE_NODE_LIST_PROP = treeNodeTreeProp
        TREE_DATA_CALL = initDataCall

        TREE_CHECKBOX = checkBox


        imgCall?.let {
            TREE_IMG_CALL = it
        }
        if (init) {
            RefreshTree()
        }
        //添加到缓存
        this.root.treeViewId = treeId
        this.root.treeView = this
        initTreeItem(this.root)

    }

    /**
     * 通过树类型嵌套的数据源，注册当前TreeView视图,id和children属性、获取数据的函数
     * @param idProp id属性引用
     * @param childrenProp children属性引用
     * @param init 是否注册后立即初始化
     * @param checkBox 构建勾选框的树
     * @param imgCall 获取树元素的图标
     * imgCall = {
     *                 if ("GROUP" == it.value.type) {
     *                     it.graphic = FontIcon(Feather.FOLDER)
     *                 }
     *             }
     * @param getInitDataCall 获取初始化数据的回调函数
     * listOf(
     *                     TreeTreeVO(
     *                         "1", "0", "1", arrayListOf(
     *                             TreeTreeVO(
     *                                 "2", "1", "2", arrayListOf(
     *                                     TreeTreeVO("4", "2", "4", null)
     *                                 )
     *                             ),
     *                             TreeTreeVO("3", "1", "3", null)
     *                         )
     *                     )
     *                 )
     */
    fun RegisterByTree(
        treeNodeTreeProp: LerverTreeNodeTreeProp<T, P>,
        checkBox: Boolean = false,
        init: Boolean = false,
        imgCall: ((TreeItem<T>) -> Unit)? = null,
        initDataCall: () -> List<T>
    ) {
        //判断
        if (TREE_DATA_CALL != null) {
            throw Exception("TreeView已经注册过,请勿重复注册")
        }

        DATA_TYPE = LerverTreeDataType.TREE
        TREE_NODE_TREE_PROP = treeNodeTreeProp
        TREE_DATA_CALL = initDataCall

        TREE_CHECKBOX = checkBox

        imgCall?.let {
            TREE_IMG_CALL = it
        }
        if (init) {
            RefreshTree()
        }
        //添加到缓存
        this.root.treeViewId = treeId
        this.root.treeView = this
        initTreeItem(this.root)
    }

    /**
     * 根据注册的[获取数据的函数],获取基础数据
     */
    private fun initDatas(): List<T> {
        return TREE_DATA_CALL!!.invoke()
    }

    /**
     * 重新传入初始化数据的函数
     */
    fun ResetInitDataCall(
         getInitDataCall: () -> List<T>
    ) {
        TREE_DATA_CALL = getInitDataCall
    }
    /**
     * 重新加载树,相当于新建树节点,数据将被全部清理！！,可重新传入初始化数据的函数
     */
    fun RefreshTree(
        filterFunc: ((T) -> Boolean)? = null
    ) {
            //println("RefreshTree")

            //根据初始化数据函数获取数据
            val datas = initDatas()

            //清除item缓存
            clearItemCache()
            //清除旧item的数据
            this.root.apply {
                children.clear()
                //添加到缓存
                treeViewId = treeId
                treeView = this@LerverTreeView
                initTreeItem(this)
            }

            //重载
            if (DATA_TYPE == LerverTreeDataType.LIST) {
                initList(this.root, datas, filterFunc)
            } else {
                val childtren = datas.map {
                    val item = createTreeItem(it)
                    initTree(item, filterFunc)
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
            refreshNodeList()

            //注册item操作事件
            if (!IS_REGISTER_EVENT) {
                EventBus.subscribe(
                    "${LerverTreeBusId.ITEM_ADD_LIST}_${treeId}",
                    BodyEvent::class
                ) {
                    it.body?.let {
                        val itemEventListBody = it as LerverTreeItemEventListBody<T>
                        addChildren(itemEventListBody.code, itemEventListBody.list)
                        refreshNodeList()
                    }
                }
                EventBus.subscribe(
                    "${LerverTreeBusId.ITEM_UPT}_${treeId}",
                    BodyEvent::class
                ) {
                    it.body?.let {
                        val itemEventValueBody = it as LerverTreeItemEventValueBody<T>
                        updateItem(itemEventValueBody.code, itemEventValueBody.value)
                        refreshNodeList()
                    }
                }
                EventBus.subscribe(
                    "${LerverTreeBusId.ITEM_UPT_CHILD}_${treeId}",
                    BodyEvent::class
                ) {
                    it.body?.let {
                        val itemEventValueBody = it as LerverTreeItemEventValueBody<T>
                        updateChild(itemEventValueBody.code, itemEventValueBody.value)
                        refreshNodeList()
                    }
                }
                EventBus.subscribe(
                    "${LerverTreeBusId.ITEM_DEL}_${treeId}",
                    BodyEvent::class
                ) {
                    it.body?.let {
                        val itemEventCodeBody = it as LerverTreeItemEventCodeBody
                        delete(itemEventCodeBody.codes)
                        refreshNodeList()
                    }
                }
                IS_REGISTER_EVENT = true
            }

    }


    /**
     * 过滤树,临时处理,节点数据不会被清理
     */
    fun FilterTree(
        filterFunc: ((T) -> Boolean)? = null
    ) {
//        println("FilterTree")
        //根据缓存数据函数获取数据
        //清除旧item的数据和缓存
        this.root.children.clear()
        this.root.treeViewId = treeId

        //组织树数据
        val idProp = when (DATA_TYPE) {
            LerverTreeDataType.LIST -> {
                TREE_NODE_LIST_PROP!!.idProp
            }

            else -> {
                TREE_NODE_TREE_PROP!!.idProp
            }
        }
        val childtren = TREE_ROOT_NODE_DATA!!.children?.map {
            val item = createTreeItem(it.entity)
            initFilterTree(item, idProp, it.children, filterFunc)
            item
        }?.filter {
            if (filterFunc == null || filterFunc(it.value)) {
                return@filter true
            }
            it.children.isNotEmpty()
        }
        //添加子节点
        if (childtren.isNullOrEmpty()) {
            return
        }
        this.root.children.addAll(childtren)
    }

    /**
     * 将子元素列表添加到指定item下（递归）
     */
    private fun initFilterTree(
        panTreeItem: TreeItem<T>,
        idProp: KProperty1<T, P>,
        lerverTreeTempNodeChildren: Collection<LerverTreeTempNode<T>>?,
        filterFunc: ((T) -> Boolean)? = null
    ) {
        //获取子节点
        if (lerverTreeTempNodeChildren != null) {
            val newChildtren = lerverTreeTempNodeChildren.map { empDataNode ->
                val item = createTreeItem(empDataNode.entity)
                empDataNode.children?.let {
                    initFilterTree(item, idProp, it, filterFunc)
                }
                item
            }.filter {
                if (filterFunc == null || filterFunc(it.value)) {
                    return@filter true
                }
                it.children.isNotEmpty()
            }
            //添加子节点
            if (newChildtren.isNotEmpty()) {
                panTreeItem.children.addAll(newChildtren)
            }
        }
    }

    /**
     * 将子元素列表添加到指定item下（递归）
     */
    private fun initList(
        panTreeItem: TreeItem<T>,
        datas: List<T>,
        filterFunc: ((T) -> Boolean)? = null
    ) {
        //获取子节点
        val childtren = datas
            .filter {
                TREE_NODE_LIST_PROP!!.idProp.get(panTreeItem.value!!) == TREE_NODE_LIST_PROP!!.parentIdProp.get(it)
            }.map {
                val item = createTreeItem(it)
                TREE_IMG_CALL?.let { it1 -> it1(item) }

                initList(item, datas, filterFunc)
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

    /**
     * 将子元素列表添加到指定item下（递归）
     */
    private fun initTree(
        panTreeItem: TreeItem<T>,
        filterFunc: ((T) -> Boolean)? = null
    ) {
        //获取子节点
        val datas = TREE_NODE_TREE_PROP!!.childrenProp.get(panTreeItem.value)
        if (datas != null) {
            val childtren = datas.map {
                val item = createTreeItem(it)
                TREE_IMG_CALL?.let { it1 -> it1(item) }

                initTree(item, filterFunc)
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

    }

    /**
     * 获取TreeItem实例，根据CheckBox标识返回,否则勾选框会失去联动效果
     */
    private fun createTreeItem(it: T): TreeItem<T> {
        return if (TREE_CHECKBOX) {
            CheckBoxTreeItem(it).apply {
                this.treeViewId = treeId
                this.treeView = this@LerverTreeView
                initTreeItem(this)
            }
        } else {
            TreeItem(it).apply {
                this.treeViewId = treeId
                this.treeView = this@LerverTreeView
                initTreeItem(this)
            }
        }
    }

    /**
     * TreeItem实例更新value值后进行相应的更新
     */
    private fun initTreeItem(treeItem: TreeItem<T>) {
        val code = treeItem.hashCode()
//        println("initTreeItem code:$code ${treeItem}")
        ITEM_CODE_TO_TREEITEM_MAP[code] = treeItem
        ITEM_CODE_LIST.add(code)

        val idProp =
            if (DATA_TYPE == LerverTreeDataType.LIST) TREE_NODE_LIST_PROP!!.idProp else TREE_NODE_TREE_PROP!!.idProp
        val busyId = idProp.get(treeItem.value).toString()
        ITEM_BUSI_TO_TREEITEM_MAP[busyId] = treeItem
        BUSI_ID_LIST.add(busyId)

        TREE_IMG_CALL?.let { it(treeItem) }
    }

    /**
     * 清除TreeItem相应的缓存
     */
    private fun clearItemCache(treeItem: TreeItem<T>? = null) {
        val idProp =
            if (DATA_TYPE == LerverTreeDataType.LIST) TREE_NODE_LIST_PROP!!.idProp else TREE_NODE_TREE_PROP!!.idProp

        if (treeItem != null) {
            val code = treeItem.hashCode()
            val busyId = idProp.get(treeItem.value).toString()

            ITEM_CODE_LIST.remove(code)
            ITEM_CODE_TO_TREEITEM_MAP.remove(code)
            ITEM_TO_TREE_MAP.remove(code)

            ITEM_BUSI_TO_TREEITEM_MAP.remove(busyId)
            BUSI_ID_LIST.remove(busyId)
        } else {
            ITEM_CODE_LIST.let {
                it.forEach {
                    ITEM_TO_TREE_MAP.remove(it)
                }
                it.clear()
            }
            ITEM_CODE_TO_TREEITEM_MAP.clear()

            ITEM_BUSI_TO_TREEITEM_MAP.clear()
            BUSI_ID_LIST.clear()
        }
    }

    //
    fun GetItemByBusiId(id: String): TreeItem<T>? {
        return ITEM_BUSI_TO_TREEITEM_MAP[id] ?: return null
    }

    /*
        刷新缓存节点数据
     */
    @Synchronized
    private fun refreshNodeList() {
        asyncTask {
            // println("正在刷新节点数据集:${treeId}")
//            val rootDataNode = LerverTreeTempNode(root.value)
//            val newChildren = root.children.map {
//                treeItemToNode(it)
//            }.toList()
//            rootDataNode.children = newChildren
//
            val rootDataNode = copyDataToDestination(root)
            TREE_ROOT_NODE_DATA = rootDataNode

//            println(rootDataNode)
        }
    }

    /*
        treeItem转换成缓存节点数据
     */
    private fun treeItemToNode(treeItem: TreeItem<T>): LerverTreeTempNode<T> {
        val dataNode = LerverTreeTempNode(treeItem.value)
        if (treeItem.children.isNotEmpty()) {
            val newChildren = treeItem.children.map {
                treeItemToNode(it)
            }.toList()
            dataNode.children = newChildren
        }
        return dataNode
    }

    fun copyDataToDestination(source: TreeItem<T>): LerverTreeTempNode<T> {
        val destinationChildren = source.children.map { copyDataToDestination(it) }
        return LerverTreeTempNode(source.value, destinationChildren)
    }

//
//    private fun refreshNodeList() {
//        asyncTask {
//            val rootDataNode = createRootDataNode(root.value)
//            TREE_ROOT_NODE_DATA = rootDataNode
//        }
//    }
//
//    private fun createRootDataNode(value: T): LerverTreeTempNode<T> {
//        val rootDataNode = LerverTreeTempNode(value)
//        rootDataNode.children = createChildNodes(rootDataNode.children).toList()
//        return rootDataNode
//    }
//
//    private fun createChildNodes(children: List<TreeItem<T>>): List<LerverTreeTempNode<T>> {
//        return children.mapNotNull { child ->
//            if (child.children.isNotEmpty()) {
//                val newNode = treeItemToNode(child)
//                newNode.children = createChildNodes(child.children)
//                newNode
//            } else {
//                null
//            }
//        }
//    }

//    private fun treeItemToNode(treeItem: TreeItem<T>): LerverTreeTempNode<T> {
//        return LerverTreeTempNode(treeItem.value)
//    }
    /**
     * 为item添加子元素
     */
    private fun addChildren(
        code: Int,
        datas: List<T>
    ) {
//        println("addChildren code:$code")
        val treeItem = ITEM_CODE_TO_TREEITEM_MAP[code]!!
//        for (data in datas) {
//            //获取子节点
//            val child = createTreeItem(data)
//            //添加子节点
//            treeItem.children.add(child)
//        }
        //重载
        if (DATA_TYPE == LerverTreeDataType.LIST) {
            initList(treeItem, datas)
        } else {
            val childtren = datas.map {
                val item = createTreeItem(it)
                initTree(item)
                item
            }
            //添加子节点
            if (childtren.isNotEmpty()) {
                treeItem.children.addAll(childtren)
            }
        }
        treeItem.isExpanded = true
    }

    /**
     * 更新当前item元素
     */
    private fun updateItem(
        code: Int,
        data: T
    ) {
        val treeItem = ITEM_CODE_TO_TREEITEM_MAP[code]!!
        treeItem.value = data
        initTreeItem(treeItem)
    }

    /**
     * 更新item下的子元素
     */
    private fun updateChild(
        code: Int,
        data: T
    ) {
        val treeItem = ITEM_CODE_TO_TREEITEM_MAP[code]!!
        if (DATA_TYPE == LerverTreeDataType.LIST) {
            val idProp = this.TREE_NODE_LIST_PROP!!.idProp as KProperty1<T, *>
            for (child in treeItem.children) {
                if (idProp.get(child.value) == idProp.get(data)) {
                    child.value = data
                    initTreeItem(child)
                    break
                }
            }
        } else {
            val idProp = this.TREE_NODE_TREE_PROP!!.idProp as KProperty1<T, *>
            for (child in treeItem.children) {
                if (idProp.get(child.value) == idProp.get(data)) {
                    child.value = data
                    initTreeItem(child)
                    break
                }
            }
        }
        treeItem.isExpanded = true
    }

    /**
     * 删除item元素
     */
    private fun delete(
        codes: List<Int>,
    ) {
        val idProp =if (DATA_TYPE == LerverTreeDataType.LIST) TREE_NODE_LIST_PROP!!.idProp else TREE_NODE_TREE_PROP!!.idProp
        val list = ArrayList<TreeItem<T>>()
        for (code in codes) {
            ITEM_CODE_TO_TREEITEM_MAP[code]?.let { item ->
                //递归获取所有code和业务ID
                getDeepChildItems(item,list)
                list.add(item)

            }
        }

        list.forEach { item ->
            val code = item.hashCode()
            val busyId = idProp.get(item.value).toString()

            ITEM_CODE_LIST.remove(code)
            ITEM_CODE_TO_TREEITEM_MAP.remove(code)
            ITEM_TO_TREE_MAP.remove(code)

            ITEM_BUSI_TO_TREEITEM_MAP.remove(busyId)
            BUSI_ID_LIST.remove(busyId)

            item.parent?.children?.remove(item)
//            println("删除了:$item")
        }
    }


    private fun getDeepChildItems(treeItem: TreeItem<T>,list : ArrayList<TreeItem<T>>) {
        treeItem.children?.let { children ->
            for (child in children) {
                getDeepChildItems(child,list)
                list.add(child)
            }
        }
    }
}
