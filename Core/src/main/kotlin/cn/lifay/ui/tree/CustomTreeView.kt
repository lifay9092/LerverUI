package cn.lifay.ui.tree

import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import kotlin.reflect.KProperty1

class CustomTreeView<N> : TreeView<N>() {

    val treeId = this.hashCode().toString()

    var DATA_TYPE = TreeViewCache.DataType.LIST

    /* treeViewId=获取数据的函数 */
    lateinit var TREE_DATA_CALL : () -> List<*>

    /* treeViewId=获取图标的函数 */
    lateinit var TREE_IMG_CALL : (TreeItem<*>) -> Unit

    /*k=TreeView的hascode, v=id和父id属性委托 适合List数据*/
    lateinit var LIST_HELP :  Pair<KProperty1<*, *>, KProperty1<*, *>>

    /*k=TreeView的hascode, v=id和children属性委托  适合Tree数据*/
    lateinit var TREE_HELP : Pair<KProperty1<*, *>, KProperty1<*, List<*>?>>

    /* treeViewId=CheckBox标识 */
    var TREE_CHECKBOX : Boolean = false

    /*item的id集合*/
    val TREE_TO_ITEM = ArrayList<Int>()

    val lc = """
        通过注册方法注入必要参数
        1.初始化数据：通过注入函数，可随时加载，用于展示和刷新树 -- 缓存函数，随时调用
        2.数据过滤：注入过滤函数，对当前的树进行过滤 -- 需要对原数据提供备份和还原功能
        3.
    """.trimIndent()
}