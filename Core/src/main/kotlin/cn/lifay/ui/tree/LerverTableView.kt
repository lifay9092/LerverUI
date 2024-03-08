package cn.lifay.ui.tree

import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import kotlin.reflect.KProperty1


/**
 * T = 实体类的类型
 * P = 实体类中ID、PARENT_ID属性的类型
 */
class LerverTableView<T : Any, P : Any> : TableView<T> {
    constructor()
    constructor(list: ObservableList<T>) : super(list)

    private var IS_REGISTER_EVENT = false

//
//    // TreeViewId=treeItem的hascode数组
//    val ITEM_CODE_LIST = ArrayList<Int>()
//
//    // TreeItem的hashcode=treeItem 辅助提供ID直接查询
//    val ITEM_CODE_TO_TABLEITEM_MAP = HashMap<Int, TreeItem<T>>()

    // value业务ID=treeItem 辅助提供ID直接查询
    val ITEM_BUSI_TO_TABLEITEM_MAP = HashMap<String, T>()

    //业务ID列表
    val BUSI_ID_LIST = ArrayList<String>()

    /**
     * 通过集合列表类型的数据源，注册当前TreeView视图,id和父id属性、获取数据的函数
     * @param idProp id属性引用
     * @param init 是否注册后立即初始化
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
    fun Register(
        idProp: KProperty1<T, P>,
        heads: List<TableColumn<T, P>>,
    ) {
        //判断
        if (IS_REGISTER_EVENT) {
            throw Exception("TableView已经注册过,请勿重复注册")
        }
        columns.apply {
            clear()
            addAll(heads)
        }
        this.items
        val list = itemsProperty().get()
        list.addListener(ListChangeListener<T> { c ->
            println("onChanged : $c")
            while (c.next()) {
                if (c.wasAdded()) {
                    list.subList(c.from, c.to).forEach {
                        ITEM_BUSI_TO_TABLEITEM_MAP[idProp.get(it) as String] = it
                    }
                }
            }
        })


    }

}