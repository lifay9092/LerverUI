package cn.lifay.ui.tree

import cn.lifay.extension.asyncTask
import cn.lifay.mq.EventBus
import cn.lifay.mq.event.DefaultEvent
import javafx.scene.control.CheckBoxTreeItem
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import kotlin.reflect.KProperty1

class TestTreeViewKT<T : Any, P : Any> : TreeView<T> {
    constructor()
    constructor(treeItem: TreeItem<T>?) : super(treeItem)

    val treeId = this.hashCode().toString()

    var DATA_TYPE = DataType.LIST
    var TREE_NODE_LIST_PROP: TreeNodeListProp<T, P>? = null
    var TREE_NODE_TREE_PROP: TreeNodeTreeProp<T, P>? = null

    var TREE_DATA_CALL: (() -> List<T>)? = null
    var TREE_IMG_CALL: ((TreeItem<T>) -> Unit)? = null

    /* treeViewId=CheckBox标识 */
    var TREE_CHECKBOX: Boolean = false

    // TreeViewId=treeItem的hascode数组
    val ITEM_ID_LIST = ArrayList<Int>()

    // value业务ID=treeItem 辅助提供ID直接查询
    val ITEM_BUSI_TO_TREEITEM_MAP = HashMap<String, TreeItem<T>>()
    val BUSI_ID_LIST = ArrayList<String>()

    // TreeViewId=数据节点,用来缓存TreeItem数据和筛选后迅速还原,TreeView数据变更后进行刷新
    lateinit var TREE_ROOT_NODE_DATA: TempDataNode<T>

    init {
        TREE_ROOT_NODE_DATA = TempDataNode(root.value)
    }

    val lc = """
        通过注册方法注入必要参数
        1.初始化数据：通过注入函数，可随时加载，用于展示和刷新树 -- 缓存函数，随时调用
        2.数据过滤：注入过滤函数，对当前的树进行过滤 -- 需要对原数据提供备份和还原功能
        3.
    """.trimIndent()

    fun RegisterByList(
        treeNodeTreeProp: TreeNodeListProp<T, P>,
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
            asyncTask {
                RefreshTree()

                EventBus.subscribe("${TreeBusId.REFRESH_NODE_LIST}_$treeId", DefaultEvent::class) {
                    refreshNodeList(this)
                }
                EventBus.publish(DefaultEvent("${TreeBusId.REFRESH_NODE_LIST}_${treeId}"))
            }
        }
    }

    /**
     * 根据注册的[获取数据的函数],获取基础数据
     */
    fun initDatas(): List<T> {
        return TREE_DATA_CALL!!.invoke()
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
        //清除旧item的数据和缓存
        this.root.children.clear()

        this.root.treeViewId = treeId
        //重载
        if (DATA_TYPE == DataType.LIST) {
            initList(this.root as TreeItem, datas, filterFunc)
        } else {
            val childtren = datas.map {
                val item = newTreeItem( it)
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
    }


    /**
     * 过滤树,临时处理,节点数据不会被清理
     */
    fun FilterTree(
         filterFunc: ((T) -> Boolean)? = null
    ) {
        println("FilterTree")
        //根据缓存数据函数获取数据
        //清除旧item的数据和缓存
        this.root.children.clear()
        TreeViewCache.TREE_TO_BUSI_TO_MAP[this.treeId] = arrayListOf()

        this.root.treeViewId = treeId

        //组织树数据
        val idProp = when (DATA_TYPE) {
            DataType.LIST -> {
                TREE_NODE_LIST_PROP!!.idProp
            }
            else -> {
                TREE_NODE_TREE_PROP!!.idProp
            }
        }
        val childtren = TREE_ROOT_NODE_DATA.children?.map {
            val item = newTreeItem(it.entity)
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
fun initFilterTree(
    panTreeItem: TreeItem<T>,
    idProp: KProperty1<T, P>,
    tempDataNodeChildren: Collection<TempDataNode<T>>?,
    filterFunc: ((T) -> Boolean)? = null
) {
    //获取子节点
    if (tempDataNodeChildren != null) {
        val newChildtren = tempDataNodeChildren.map { empDataNode ->
            val item = newTreeItem(empDataNode.entity)
            loadImg(item)
            empDataNode.children?.let {
                initFilterTree(item, idProp, it, filterFunc)
            }
            item.CacheBusiIdMap(idProp)
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
    fun initList(
        panTreeItem: TreeItem<T>,
        datas: List<T>,
        filterFunc: ((T) -> Boolean)? = null
    ) {
        //获取子节点
        val childtren = datas
            .filter {
                TREE_NODE_LIST_PROP!!.idProp.get(panTreeItem.value!!) == TREE_NODE_LIST_PROP!!.parentIdProp.get(it)
            }.map {
                val item = newTreeItem(it)
                TREE_IMG_CALL?.let { it1 -> it1(item) }

                initList(item, datas, filterFunc)
                item.CacheBusiIdMap(TREE_NODE_LIST_PROP!!.idProp)
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
    fun loadImg(treeItem: TreeItem<*>){
        TREE_IMG_CALL?.let { it(treeItem as TreeItem<T>) }
    }

    /**
     * 将子元素列表添加到指定item下（递归）
     */
    fun initTree(
        panTreeItem: TreeItem<T>,
        filterFunc: ((T) -> Boolean)? = null
    ) {
        //获取子节点
        val datas = TREE_NODE_TREE_PROP!!.childrenProp.get(panTreeItem.value)
        if (datas != null) {
            val childtren = datas.map {
                val item = newTreeItem(it)
                TREE_IMG_CALL?.let { it1 -> it1(item) }

                initTree(item, filterFunc)
                item.CacheBusiIdMap()
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
    fun newTreeItem(it: T): TreeItem<T> {
        return if (TREE_CHECKBOX == true) {
            CheckBoxTreeItem(it).apply {
                this.treeViewId = treeId
                this.treeView = this@TestTreeViewKT
            }
        } else {
            TreeItem(it).apply {
                this.treeViewId = treeId
                this.treeView = this@TestTreeViewKT
            }
        }
    }

    //
    fun GetItemByBusiId(id: String): TreeItem<T>? {
        val item = ITEM_BUSI_TO_TREEITEM_MAP[id] ?: return null
        return item as? TreeItem<T>
    }

    /*
        刷新节点数据
     */
    fun refreshNodeList(treeView: TreeView<T>) {
        asyncTask {
            println("正在刷新节点数据集:${treeView.id}")
            val root = treeView.root
            val rootDataNode = TempDataNode(root.value)
            rootDataNode.children = root.children.map {
                treeItemToNode(it)
            }.toList()
            TREE_ROOT_NODE_DATA = rootDataNode

            println(rootDataNode)
        }
    }

    private fun treeItemToNode(treeItem: TreeItem<T>): TempDataNode<T> {
        val dataNode = TempDataNode(treeItem.value)
        if (treeItem.children.isNotEmpty()) {
            dataNode.children = treeItem.children.map {
                treeItemToNode(it)
            }.toList()
        }
        return dataNode
    }


}
