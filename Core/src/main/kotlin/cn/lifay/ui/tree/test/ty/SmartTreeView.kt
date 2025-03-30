package cn.lifay.ui.tree.test.ty

import cn.lifay.exception.LerverUIException
import cn.lifay.extension.asyncTask
import cn.lifay.mq.EventBus
import cn.lifay.mq.event.BodyEvent
import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.control.CheckBoxTreeItem
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import kotlinx.coroutines.Dispatchers

/**
 *
 *
 *
 * 用kotlin开发javafx界面库，对TreeView组件进行封装，可以扩展TreeView、TreeItem功能：
 * 1支持树层级数据可以通过多种方式进行加载：指定类的ID和PARENT_ID属性，指定类的CHILDREN属性，指定懒加载获取下级数据函数
 * 2增加方法：添加树节点、删除树节点、修改树节点、修改树节点的子节点；
 * 3增加方法：刷新树节点、动态过滤树节点、还原树节点；
 * 4增加方法：根据业务ID查找树节点；
 *
 * TreeView：
 *     filter: func 过滤和删减节点，不修改原始数据，处理过滤中修改的节点要同步到原始数据
 *     restore：还原回filter前（原始数据）
 *
 * TreeItem
 *
 * code -> TreeItem
 * code -> TreeNode
 * code -> parent.code
 *
 * add
 * upt
 * del
 * filter
 * restore
 */
// 树配置类（使用密封类实现配置模式）
sealed class TreeConfig<T : Any, P : Any> {
    abstract val idProperty: (T) -> P
    abstract val parentIdProperty: (T) -> P?
    abstract val childrenProperty: (T) -> List<T>?
    abstract var loadChildren: (T) -> List<T>?
    abstract var isCheckBox: Boolean
    abstract var imgCall: ((TreeItem<T>) -> Unit)?

    // ID-PARENT_ID配置
    class IdParentIdConfig<T : Any, P : Any>(
        override val idProperty: (T) -> P,
        override val parentIdProperty: (T) -> P? = { null },
        override var isCheckBox: Boolean = false,
        override var imgCall: ((TreeItem<T>) -> Unit)? = null,
    ) : TreeConfig<T, P>() {
        override val childrenProperty: (T) -> List<T>? = { emptyList() }
        override var loadChildren: (T) -> List<T>? = { null }
    }

    // CHILDREN属性配置
    class ChildrenPropertyConfig<T : Any, P : Any>(
        override val idProperty: (T) -> P,
        override val childrenProperty: (T) -> List<T>?,
        override var isCheckBox: Boolean = false,
        override var imgCall: ((TreeItem<T>) -> Unit)? = null,
    ) : TreeConfig<T, P>() {
        override val parentIdProperty: (T) -> P? = { null }
        override var loadChildren: (T) -> List<T>? = { null }
    }

    // 懒加载配置
    class LazyLoadingConfig<T : Any, P : Any>(
        override val idProperty: (T) -> P,
        override var isCheckBox: Boolean = false,
        override var imgCall: ((TreeItem<T>) -> Unit)? = null,
    ) : TreeConfig<T, P>() {
        override val parentIdProperty: (T) -> P? = { null }
        override val childrenProperty: (T) -> List<T>? = { emptyList() }
        override var loadChildren: (T) -> List<T>? = { null }
    }
}

/*
    数据节点，用来缓存TreeItem数据和筛选后迅速还原
 */
class SmartTreeTempNode<T : Any>(
    var nodeValue: T,
    var nodeChildren: List<SmartTreeTempNode<T>>?,
    val expanded: SimpleBooleanProperty = SimpleBooleanProperty(false),
) {
    constructor(entity: T) : this(entity, null) {
    }

    override fun toString(): String {
        return "SmartTreeTempNode(entity=$nodeValue expanded=${expanded.value})"
    }

}

// 封装的智能树组件
class SmartTreeView<T : Any, P : Any>(
    private val config: TreeConfig<T, P>
) : TreeView<T>() {
    val treeId = this.hashCode().toString()

    //root下的节点 原始树结构缓存
    private var ORIGINAL_ROOT_NODE: SmartTreeTempNode<T>? = null

    //过滤状态
    private var IS_FILTERED = false

    // T实例.code -> 原始节点数据实例
    private val ID_TO_VALUE_NODE = HashMap<String, SmartTreeTempNode<T>>()

    // T实例.code -> T实例所在TreeItem实例
    private val ID_TO_TREE_ITEM = HashMap<String, TreeItem<T>>()

    // T实例.code -> T实例所在TreeItem实例展开状态
//    private val codeToExpandedStatus = HashMap<Int, Boolean>()

    // TreeItem的hashcode=treeItem 辅助提供ID直接查询
    private val CODE_TO_TREEITEM_MAP = HashMap<Int, TreeItem<T>>()

    //事件注册标识
    private var IS_REGISTER_EVENT = false

    /**/
    // 根据配置加载数据
    @Synchronized
    fun loadData(dataSource: (T) -> List<T>?) {
        ID_TO_VALUE_NODE.clear()
        ID_TO_TREE_ITEM.clear()
        CODE_TO_TREEITEM_MAP.clear()
        config.loadChildren = dataSource

        val rootId = getIdByValue(root.value)
        root.apply {
            treeViewddd = this@SmartTreeView
            children.clear()
            ID_TO_TREE_ITEM[rootId] = this
            CODE_TO_TREEITEM_MAP[hashCode()] = this
        }
//        println("root.code():rootHashCode")
        when (config) {
            is TreeConfig.IdParentIdConfig<T, P> -> buildTreeByIdParentId()
            is TreeConfig.ChildrenPropertyConfig<T, P> -> buildTreeByChildren()
            is TreeConfig.LazyLoadingConfig<T, P> -> buildLazyTree()
        }
        asyncTask {
            // 保存初始结构
            ORIGINAL_ROOT_NODE = SmartTreeTempNode(
                root.value,
                root.children?.map { itemToNode(it) })
            ID_TO_VALUE_NODE[rootId] = ORIGINAL_ROOT_NODE!!
            ORIGINAL_ROOT_NODE!!.apply {
                expanded.unbind()
                expanded.bind(root.expandedProperty())
            }
            /*注册item操作事件*/
            if (!IS_REGISTER_EVENT) {
                registryItemEvent()
            }
        }
    }

    /**
     * 注册item操作事件
     */
    private fun registryItemEvent() {
        EventBus.subscribe<BodyEvent<SmartTreeItemEventListBody<T>>>(
            "${SmartTreeBusId.ITEM_ADD_LIST}_${treeId}"
        ) {
            it.body?.let {
                val itemEventListBody = it as SmartTreeItemEventListBody<T>
                val treeItem = CODE_TO_TREEITEM_MAP[itemEventListBody.code]!!
                asyncTask(Dispatchers.Main) {
                    itemEventListBody.list.forEach {
                        addNode(treeItem, it)
                    }
                }
            }
        }
        EventBus.subscribe<BodyEvent<SmartTreeItemEventValueBody<T>>>(
            "${SmartTreeBusId.ITEM_UPT}_${treeId}"
        ) {
            it.body?.let {
                val itemEventValueBody = it
                val treeItem = CODE_TO_TREEITEM_MAP[itemEventValueBody.code]!!
                asyncTask(Dispatchers.Main) { uptNode(treeItem, itemEventValueBody.value) }
            }
        }

        EventBus.subscribe<BodyEvent<SmartTreeItemEventCodeBody>>(
            "${SmartTreeBusId.ITEM_DEL}_${treeId}",
        ) {
            it.body?.let {
                val itemEventCodeBody = it
                asyncTask(Dispatchers.Main) {
                    itemEventCodeBody.codes.forEach {
                        removeNode(it)
                    }
                }
            }
        }
        IS_REGISTER_EVENT = true
    }

    /**
     * 树项节点转成缓存节点
     */
    private fun itemToNode(treeItem: TreeItem<T>): SmartTreeTempNode<T> {
//        treeItem.treeViewddd = this@SmartTreeView

        val tempNode =
            SmartTreeTempNode(treeItem.value, treeItem.children?.map { itemToNode(it) })
        val id = getIdByValue(treeItem.value)
        tempNode.apply {
            expanded.unbind()
            expanded.bind(treeItem.expandedProperty())
        }
        ID_TO_VALUE_NODE[id] = tempNode
        ID_TO_TREE_ITEM[id] = treeItem
        CODE_TO_TREEITEM_MAP[treeItem.hashCode()] = treeItem
        return tempNode
    }

    // 根据ID-PARENT_ID构建树
    private fun buildTreeByIdParentId() {
        if (root == null) {
            return
        }
        val rootId = config.idProperty(root.value)
        val childrenList = config.loadChildren(root.value)
        childrenList?.filter {
            rootId == config.parentIdProperty(it)
        }?.forEach { data ->
            val item = createTreeItemByIdParentId(data, childrenList)
            root.children.add(item)
        }
    }

    // 根据ID-PARENT_ID构建树
    private fun createTreeItemByIdParentId(data: T, list: List<T>?): TreeItem<T> {
        val treeItem = createTreeItemBase(data)
        val itemId = config.idProperty(data)
        list?.filter {
            itemId == config.parentIdProperty(it)
        }?.forEach { child ->
            val item = createTreeItemByIdParentId(child, list)
            treeItem.children.add(item)
        }
        return treeItem
    }

    // 根据children属性构建树
    private fun buildTreeByChildren() {
        if (root == null) {
            return
        }
        val childrenList = config.loadChildren(root.value)
        root.children.setAll(childrenList?.map { createTreeItemByChildren(it) } ?: emptyList())
    }

    // 根据children属性构建树
    private fun createTreeItemByChildren(data: T): TreeItem<T> {
        val treeItem = createTreeItemBase(data)
        config.childrenProperty(data)?.let { children ->
            treeItem.children.setAll(children.map { createTreeItemByChildren(it) })
        }
        return treeItem
    }

    // 构建懒加载树
    private fun buildLazyTree() {
        this.setOnMouseClicked {
            //懒加载
            val item = this.selectionModel.selectedItem
            if (item != null && it.clickCount == 2 && !item.isExpanded && item.children.isEmpty()) {
                val childrenValue = config.loadChildren(item.value)
                val childchildrenItem = childrenValue?.map { createTreeItemBase(it) }
                childchildrenItem?.let {
                    if (it.isNotEmpty()) {
                        item.children.setAll(it)
                        item.isExpanded = true
                    }
                }
            }
        }
        root.isExpanded = true
        root.children.setAll(config.loadChildren(root.value)?.map { createTreeItemBase(it) } ?: emptyList())
    }

    private fun createTreeItemBase(data: T): TreeItem<T> {
        val treeItem = if (config.isCheckBox) CheckBoxTreeItem<T>(data) else TreeItem<T>(data)
        treeItem.treeViewddd = this@SmartTreeView
        return treeItem
    }

    // 查找节点
    fun findNodeById(id: P): TreeItem<T>? {
        return ID_TO_TREE_ITEM[id.toString()]
    }


    // 添加节点
    @Synchronized
    private fun addNode(parentItem: TreeItem<T>, data: T) {
        val id = getIdByValue(data)
        if (ID_TO_TREE_ITEM.containsKey(id)) {
            throw LerverUIException("id已存在:$id")
        }
        val treeItem = createTreeItemBase(data)
        val smartTreeTempNode = SmartTreeTempNode(data)
        smartTreeTempNode.expanded.apply {
            unbind()
            bind(treeItem.expandedProperty())
        }

        //添加到父tree节点中
        parentItem.children.add(treeItem)
        //添加到父节点缓存中
        ID_TO_VALUE_NODE[getIdByValue(parentItem.value)]!!.nodeChildren.let {
            (it as MutableList<SmartTreeTempNode<T>>?)?.add(smartTreeTempNode)
        }
        //节点缓存
        ID_TO_TREE_ITEM[id] = treeItem
        ID_TO_VALUE_NODE[id] = smartTreeTempNode
        CODE_TO_TREEITEM_MAP[treeItem.hashCode()] = treeItem
    }

    // 修改节点 注意id变化的情况
    @Synchronized
    private fun uptNode(treeItem: TreeItem<T>, data: T) {
        val oldId = getIdByValue(treeItem.value)
        val isExist = (ID_TO_TREE_ITEM.containsKey(oldId)) && ID_TO_VALUE_NODE.containsKey(oldId)
        if (!isExist) {
            throw LerverUIException("id不存在:$oldId")
        }
        val newId = getIdByValue(data)
        val smartTreeTempNode = ID_TO_VALUE_NODE[oldId]
        smartTreeTempNode!!.expanded.apply {
            unbind()
            bind(treeItem.expandedProperty())
        }
        //更新tree节点中
        treeItem.value = null
        treeItem.value = data
        //添加到父节点临时节点中
        smartTreeTempNode.nodeValue = data

        /*更新缓存*/
        if (newId != oldId) {
            ID_TO_TREE_ITEM.remove(oldId)
            ID_TO_VALUE_NODE.remove(oldId)
            CODE_TO_TREEITEM_MAP.remove(treeItem.hashCode())

            ID_TO_TREE_ITEM[newId] = treeItem
            ID_TO_VALUE_NODE[newId] = smartTreeTempNode
            CODE_TO_TREEITEM_MAP[treeItem.hashCode()] = treeItem
        }
        //hashcode缓存
        //   val code = treeItem.hashCode()
        //code不存在，则清理旧的hashcode
        //  if (!CODE_TO_TREEITEM_MAP.containsKey(code)) {
        //      throw LerverUIException("code不存在:$code")

//            CODE_TO_TREEITEM_MAP[code] = treeItem
//
//            val treeItemIdList = collectDeepId(root as TreeItem<T>)
//            val treeItemIdToCodes = treeItemIdList.associateBy(keySelector = {
//                ID_TO_TREE_ITEM[it].hashCode()
//            }, valueTransform = { it })
//            val codeToTreeItemIterator = CODE_TO_TREEITEM_MAP.iterator()
//            while (codeToTreeItemIterator.hasNext()) {
//                val codeToNodeEntry = codeToTreeItemIterator.next()
//                val id = treeItemIdToCodes[codeToNodeEntry.key]
//                if (!treeItemIdList.contains(id)) {
//                    println("清理了:${codeToNodeEntry.key} id:$id")
//                    codeToTreeItemIterator.remove()
//                }
//            }
//
//            val idToTreeItemIterator = ID_TO_TREE_ITEM.iterator()
//            while (idToTreeItemIterator.hasNext()) {
//                val idToTreeItemEntry = idToTreeItemIterator.next()
//                if (!treeItemIdList.contains(idToTreeItemEntry.key)) {
//                    println("清理了:${idToTreeItemEntry.key}")
//                    idToTreeItemIterator.remove()
//                }
//            }
//            val codeToNodeIterator = ID_TO_VALUE_NODE.iterator()
//            while (codeToNodeIterator.hasNext()) {
//                val codeToNodeEntry = codeToNodeIterator.next()
//                if (!treeItemIdList.contains(codeToNodeEntry.key)) {
//                    println("清理了:${codeToNodeEntry.key}")
//                    codeToNodeIterator.remove()
//                }
//            }

        //  }
    }

    /**
     * 递归获取子节点值的id
     */
    private fun collectDeepId(item: TreeItem<T>): List<String> {
        tailrec fun collect(
            remaining: List<TreeItem<T>>,  // 待处理的节点列表
            acc: List<String> = emptyList() // 累积的哈希值结果
        ): List<String> {
            return if (remaining.isEmpty()) {
                acc // 终止条件：无剩余节点时返回结果
            } else {
                val current = remaining.first()
                val newRemaining = remaining.drop(1) + current.children // 深度优先：子节点追加到末尾
                collect(newRemaining as List<TreeItem<T>>, acc + getIdByValue(current.value))
                //          collect(newRemaining as List<SmartTreeItem<T, P>>, acc + current.hashCode().toString())
            }
        }
        return collect(listOf(item)) // 从当前节点开始遍历
    }

    // 删除节点
    @Synchronized
    private fun removeNode(code: Int) {
        val treeItem = CODE_TO_TREEITEM_MAP[code]!!
        /*删除当前节点*/
        val parentItem = treeItem.parent
        val id = getIdByValue(treeItem.value)
        //父节点中删除节点
        treeItem.expandedProperty().unbind()
        parentItem?.children?.remove(treeItem)
        //父节点缓存中删除节点
        ID_TO_VALUE_NODE[getIdByValue(parentItem.value)]?.nodeChildren?.let {
            val iterator = (it as MutableList).iterator()
            while (iterator.hasNext()) {
                val next = iterator.next()
                if (getIdByValue(next.nodeValue) == id) {
                    iterator.remove()
                }
            }
//            (it as MutableList<SmartTreeTempNode<T>>?)?.remove(codeToValueNode[code])
        }
        //删除节点缓存
        ID_TO_VALUE_NODE[id]?.expanded?.unbind()
        ID_TO_TREE_ITEM.remove(id)
        ID_TO_VALUE_NODE.remove(id)
        CODE_TO_TREEITEM_MAP.remove(code)
        /*递归获取子节点的hashcode*/
        val childrenHashCodes = collectDeepId(treeItem)
        childrenHashCodes.forEach {
            val childTreeItem = ID_TO_TREE_ITEM[it]
            CODE_TO_TREEITEM_MAP.remove(childTreeItem.hashCode())
            ID_TO_TREE_ITEM.remove(it)
            ID_TO_VALUE_NODE[it]?.expanded?.unbind()
            ID_TO_VALUE_NODE.remove(it)
        }
    }

    fun printFunc(func: () -> Unit) {
        println()
        printf(">>>>>>>>>>>>>>>>>>>>前>>>>>>>>>>>>>>>>>>>>")
        func()
        printf("<<<<<<<<<<<<<<<<<<<<后<<<<<<<<<<<<<<<<<<<<]")
        println()

    }

    fun printf(msg: String) {
        println(msg)
        collectDeepId(this.root as TreeItem<T>).forEach { println(it) }
//        println("codeToTreeItem")
//        codeToTreeItem.forEach {
//            println("key:${it.key} value:${it.value}")
//        }
//        println("codeToValueNode")
//        codeToValueNode.forEach {
//            println("key:${it.key} value:${it.value}")
//        }
        println(msg)
    }

    // 刷新树
    @Synchronized
    fun refreshTree() {
        loadData(config.loadChildren)
    }

    // 过滤树 节点或子节点满足条件都显示
    @Synchronized
    fun filter(predicate: (T) -> Boolean) {
        IS_FILTERED = true
        restore(predicate)
    }

    // 递归修剪树结构的核心方法
    private fun pruneTree(node: SmartTreeTempNode<T>, predicate: ((T) -> Boolean)?): Boolean {
        var childPruneResult = false

        val nodeTreeItem = try {
            ID_TO_TREE_ITEM[getIdByValue(node.nodeValue)]!!
        } catch (e: Exception) {
            TODO("Not yet implemented")
        }
        nodeTreeItem.children!!.clear()
        node.nodeChildren?.let { nodeChildren ->
            for (child in nodeChildren) {
                if (pruneTree(child, predicate) || predicate == null) {
                    nodeTreeItem.children.add(nodeToItem(child))
                    childPruneResult = true
                }
            }
        }
        // 当前节点是否需要保留？
        val keep = childPruneResult || predicate?.let { it(node.nodeValue) } == true
        return keep
    }

    // 还原树
    @Synchronized
    fun restore(predicate: ((T) -> Boolean)? = null) {
        if (IS_FILTERED || predicate == null) {
            try {
                root.children.clear()
                ORIGINAL_ROOT_NODE?.let {
                    for (nodeChild in ORIGINAL_ROOT_NODE!!.nodeChildren!!) {
                        if (pruneTree(nodeChild, predicate) || predicate == null) {
                            root.children.add(nodeToItem(nodeChild))
                        }
                    }
                }
            } finally {
                // 重置过滤状态
                IS_FILTERED = false
            }
        }
//        println()
    }

    /**
     * 缓存节点转成树项节点
     */
    private fun nodeToItem(item: SmartTreeTempNode<T>): TreeItem<T> {

        val code = getIdByValue(item.nodeValue)
        val smartTreeItem = ID_TO_TREE_ITEM[code]!!
        smartTreeItem.isExpanded = item.expanded.value
        return smartTreeItem
    }

    // 获取节点的id
    private fun getIdByValue(item: T): String {
        val code = config.idProperty.invoke(item).toString()
        return code
    }

}
