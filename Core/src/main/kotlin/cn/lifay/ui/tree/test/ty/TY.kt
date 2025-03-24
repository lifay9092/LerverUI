package cn.lifay.ui.tree.test.ty

import cn.lifay.extension.asyncTask
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView

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
 * hashcode -> TreeItem
 * hashcode -> TreeNode
 * hashcode -> parent.hashcode
 *
 * add
 * upt
 * del
 * filter
 * restore
 */
// 树配置类（使用密封类实现配置模式）
sealed class TreeConfig<T : Any, P : Any> {
    abstract val idProperty: (T) -> P?
    abstract val parentIdProperty: (T) -> P?
    abstract val childrenProperty: (T) -> List<T>?
    abstract var loadChildren: (T) -> List<T>?

    // ID-PARENT_ID配置
    class IdParentIdConfig<T : Any, P : Any>(
        override val idProperty: (T) -> P?,
        override val parentIdProperty: (T) -> P? = { null },
    ) : TreeConfig<T, P>() {
        override val childrenProperty: (T) -> List<T>? = { emptyList() }
        override var loadChildren: (T) -> List<T>? = { null }
    }

    // CHILDREN属性配置
    class ChildrenPropertyConfig<T : Any, P : Any>(
        override val childrenProperty: (T) -> List<T>?,
    ) : TreeConfig<T, P>() {
        override val idProperty: (T) -> P? = { null }
        override val parentIdProperty: (T) -> P? = { null }
        override var loadChildren: (T) -> List<T>? = { null }
    }

    // 懒加载配置
    class LazyLoadingConfig<T : Any, P : Any>(
    ) : TreeConfig<T, P>() {
        override val idProperty: (T) -> P? = { null }
        override val parentIdProperty: (T) -> P? = { null }
        override val childrenProperty: (T) -> List<T>? = { emptyList() }
        override var loadChildren: (T) -> List<T>? = { null }
    }
}

class SmartTreeItem<T : Any, P : Any>(
    value: T
) : TreeItem<T>(value) {
    var treeViewddd: SmartTreeView<T, P>? = null

    constructor(
        value: T,
        treeViewddd: SmartTreeView<T, P>
    ) : this(value) {
        this.treeViewddd = treeViewddd
    }
}


inline fun <reified T : Any, P : Any> SmartTreeItem<T, P>.addNode(value: T) {
    this.treeViewddd!!.printFunc { this.treeViewddd!!.addNode(this, value) }
}

inline fun <reified T : Any, P : Any> SmartTreeItem<T, P>.removeNode(value: T) {
    this.treeViewddd!!.printFunc { this.treeViewddd!!.removeNode(this) }
}

/*
    数据节点，用来缓存TreeItem数据和筛选后迅速还原
 */
class SmartTreeTempNode<T : Any>(
    val entity: T,
    var children: List<SmartTreeTempNode<T>>?
) {
    constructor(entity: T) : this(entity, null) {
    }

    override fun toString(): String {
        return "TempDataNode(entity=$entity"
    }

}

// 封装的智能树组件
class SmartTreeView<T : Any, P : Any>(
    private val config: TreeConfig<T, P>
) : TreeView<T>() {

    //root下的节点 原始树结构缓存
    private var originalTreeStructure: List<SmartTreeTempNode<T>>? = null //

    //过滤状态
    private var isFiltered = false

    // T实例.hashcode -> 原始节点数据实例
    private val codeToValueNode = HashMap<Int, SmartTreeTempNode<T>>()

    // T实例.hashcode -> T实例所在TreeItem实例
    private val codeToTreeItem = HashMap<Int, SmartTreeItem<T, P>>()

    // T实例.hashcode -> T实例所在TreeItem实例展开状态
    private val codeToExpandedStatus = HashMap<Int, Boolean>()

    /**/
    // 根据配置加载数据
    fun loadData(dataSource: (T) -> List<T>?) {
        codeToValueNode.clear()
        codeToTreeItem.clear()
        codeToExpandedStatus.clear()
        config.loadChildren = dataSource
        (root as SmartTreeItem<T, P>).apply {
            treeViewddd = this@SmartTreeView
            children.clear()
            val hashCode = value.hashCode()
            codeToTreeItem[hashCode] = this
            codeToValueNode[hashCode] = SmartTreeTempNode(entity = value)
        }

        when (config) {
            is TreeConfig.IdParentIdConfig<T, P> -> buildTreeByIdParentId()
            is TreeConfig.ChildrenPropertyConfig<T, P> -> buildTreeByChildren()
            is TreeConfig.LazyLoadingConfig<T, P> -> buildLazyTree()
        }
        // 保存初始结构
        asyncTask {
            originalTreeStructure = root.children.map {
                itemToNode(it as SmartTreeItem<T, P>)
            }
        }
        println()
    }

    /**
     * 树项节点转成缓存节点
     */
    private fun itemToNode(treeItem: SmartTreeItem<T, P>): SmartTreeTempNode<T> {

        val hashCode = treeItem.value.hashCode()
        codeToValueNode[hashCode] = SmartTreeTempNode(treeItem.value)
        codeToTreeItem[hashCode] = treeItem
        if (config is TreeConfig.ChildrenPropertyConfig<T, P>) {
            println("code:$hashCode value:${treeItem}")
        }
        val tempNode =
            SmartTreeTempNode(treeItem.value, treeItem.children?.map { itemToNode(it as SmartTreeItem<T, P>) })
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

    private fun createTreeItemByIdParentId(data: T, list: List<T>?): SmartTreeItem<T, P> {
        val treeItem = SmartTreeItem<T, P>(data, this@SmartTreeView)
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

    private fun createTreeItemByChildren(data: T): SmartTreeItem<T, P> {
        val treeItem = SmartTreeItem<T, P>(data, this@SmartTreeView)
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
                val childchildrenItem = childrenValue?.map { createTreeItemByLazyLoad(it) }
                childchildrenItem?.let {
                    if (it.isNotEmpty()) {
                        item.children.setAll(it)
                        item.isExpanded = true
                    }
                }
            }
        }
        root.isExpanded = true
        root.children.setAll(config.loadChildren(root.value)?.map { createTreeItemByLazyLoad(it) } ?: emptyList())
    }

    private fun createTreeItemByLazyLoad(data: T): SmartTreeItem<T, P> {
        val item = SmartTreeItem<T, P>(data, this@SmartTreeView)
        return item
    }

    // 查找节点
    fun findNodeById(id: Any): SmartTreeItem<T, P>? {
        return findNode(root as SmartTreeItem<T, P>, id)
    }

    private fun findNode(item: SmartTreeItem<T, P>, targetId: Any): SmartTreeItem<T, P>? {
        if (config.idProperty(item.value) == targetId) return item
        item.children.forEach {
            val found = findNode(it as SmartTreeItem<T, P>, targetId)
            if (found != null) return found
        }
        return null
    }

    // 添加节点
    fun addNode(parentItem: SmartTreeItem<T, P>, data: T) {
        val smartTreeItem = SmartTreeItem<T, P>(data, this@SmartTreeView)
        val smartTreeTempNode = SmartTreeTempNode(data)
        //父节点中添加节点
        parentItem.children.add(smartTreeItem)
        codeToValueNode[parentItem.value.hashCode()]!!.children.let {
            (it as MutableList<SmartTreeTempNode<T>>?)?.add(smartTreeTempNode)
        }
        //节点缓存
        codeToTreeItem[data.hashCode()] = smartTreeItem
        codeToValueNode[data.hashCode()] = smartTreeTempNode
    }

    // 删除节点
    fun removeNode(item: SmartTreeItem<T, P>) {
        val hashCode = item.value.hashCode()
        //父节点中删除节点
        item.parent?.children?.remove(item)
        codeToValueNode[item.parent!!.value.hashCode()]!!.children.let {
            (it as MutableList<SmartTreeTempNode<T>>?)?.remove(codeToValueNode[hashCode])
        }
        //删除节点缓存
        if (codeToTreeItem.containsKey(hashCode)) {
            codeToTreeItem.remove(hashCode)
            codeToValueNode.remove(hashCode)
        }
    }

    fun printFunc(func: () -> Unit) {
        printf("前")
        func()
        printf("后")

    }

    fun printf(msg: String) {
        println(msg)
        println("codeToTreeItem")
        codeToTreeItem.forEach {
            println("key:${it.key} value:${it.value}")
        }
        println("codeToValueNode")
        codeToValueNode.forEach {
            println("key:${it.key} value:${it.value}")
        }
        println(msg)
    }

    fun updateNode(item: SmartTreeItem<T, P>, newData: T) {
        item.value = newData
    }

    fun replaceChildren(item: SmartTreeItem<T, P>, newChildren: List<T>) {
        item.children.setAll(newChildren.map { SmartTreeItem<T, P>(it, this@SmartTreeView) })
    }

    // 刷新树
    fun refreshTree() {
        loadData(config.loadChildren)
    }

    // 过滤树 节点或子节点满足条件都显示
    fun filter(predicate: (SmartTreeItem<T, P>) -> Boolean) {
        println("filter")
        isFiltered = true
        val iterator = root.children.iterator()
        while (iterator.hasNext()) {
            val child = iterator.next() as SmartTreeItem<T, P>
            codeToExpandedStatus[child.hashCode()] = child.isExpanded
            if (!pruneTree(child, predicate)) {
                println("removed1: $child")
                iterator.remove()  // 使用迭代器的remove方法
            }
        }
        codeToTreeItem?.forEach { hashCode, treeItem ->
            println("code:$hashCode value:${treeItem}")

        }

    }

    // 递归修剪树结构的核心方法
    private fun pruneTree(node: SmartTreeItem<T, P>, predicate: (SmartTreeItem<T, P>) -> Boolean): Boolean {
        val children = node.children.toList() // 避免并发修改异常
        var childPruneResult = false
        for (child in children) {
            codeToExpandedStatus[child.hashCode()] = child.isExpanded
            if (!pruneTree(child as SmartTreeItem<T, P>, predicate)) {
                println("removed2: $child")
                node.children.remove(child)
                //  child.booleanProperty.set(false)
            } else {
                childPruneResult = true
            }
        }
        // 当前节点是否需要保留？
        val b = predicate(node)
        val keep = childPruneResult || b
        return keep
    }

    /**
     * 缓存节点转成树项节点
     */
    private fun nodeToItem(item: SmartTreeTempNode<T>): SmartTreeItem<T, P> {
        val hashCode = item.entity.hashCode()
        println(codeToTreeItem.containsKey(hashCode))
        val tempNode = codeToTreeItem[hashCode] ?: SmartTreeItem<T, P>(item.entity, this@SmartTreeView)
        tempNode.children.setAll(item.children?.map { nodeToItem(it) })
        if (codeToExpandedStatus.containsKey(tempNode.hashCode())) {
            tempNode.isExpanded = codeToExpandedStatus[tempNode.hashCode()]!!
        } else {
            tempNode.isExpanded = false
        }
        return tempNode
    }

    // 还原树
    fun restore() {
        if (isFiltered) {
            try {
                root.children.clear()
                originalTreeStructure?.let {
                    it.forEach {
                        root.children.add(nodeToItem(it))
                    }
                }
            } finally {
                // 重置过滤状态
                isFiltered = false
            }
        }

//        root.children.forEach {
//            it as TreeItem<T>
//            it.booleanProperty.set(true)
//        }
    }


}
