package cn.lifay.ui.table

import cn.lifay.ui.DelegateProp
import javafx.beans.property.Property
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.scene.control.TableView
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1


/**
 * T = 实体类的类型
 * P = 实体类中ID、PARENT_ID属性的类型
 *
 * 提供快捷TableView类，可以选择两种更新方式：
 * 1.实体类为普通Bean,这种方式在更新属性会替换整个对象，UI会有明显刷新。通过ID查找索引后替换
 * 2.实体类为带有xxxProperty的Bean ，这种方式会局部更新属性。通过ID找到缓存对象，对属性进行更新
 */
class LerverTableView<T : Any, P : Any> : TableView<T> {
    constructor()
    constructor(list: ObservableList<T>) : super(list)

    private var IS_REGISTER_EVENT = false

    // value业务ID=treeItem 辅助提供ID直接查询
    val ITEM_BUSI_TO_TABLEITEM_MAP = ConcurrentHashMap<P, T>()
    val ITEM_BUSI_TO_INDEX_MAP = ConcurrentHashMap<P, Int>()

    //业务ID列表
    val BUSI_ID_LIST = ArrayList<String>()

    private var ID_PROP: KProperty1<T, P>? = null
    private var ID_PROP_DELEGATE: DelegateProp<T, P>? = null

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
        idProp: KProperty1<T, P>
    ) {
        //判断
        if (IS_REGISTER_EVENT) {
            throw Exception("TableView已经注册过,请勿重复注册")
        }
        ID_PROP = idProp

        val list = itemsProperty().get()
        list.addListener(ListChangeListener<T> { c ->
            println("onChanged : $c")
            while (c.next()) {
                println("f:${c.from}, t:${c.to}")
                if (c.wasAdded()) {
//                    println("wasAdded")
                    val entity = list[c.from]
                    ITEM_BUSI_TO_TABLEITEM_MAP[ID_PROP!!.get(entity)] = entity
                } else if (c.wasRemoved()) {
//                    println("wasRemoved")
                    list.filter {
                        !ITEM_BUSI_TO_TABLEITEM_MAP.containsKey(ID_PROP!!.get(it))
                    }.forEach {
                        ITEM_BUSI_TO_TABLEITEM_MAP.remove(ID_PROP!!.get(it))
                    }
                }
            }
        })
    }

    fun RegisterByDelegete(
        idProp: DelegateProp<T, P>
    ) {
        //判断
        if (IS_REGISTER_EVENT) {
            throw Exception("TableView已经注册过,请勿重复注册")
        }
        ID_PROP_DELEGATE = idProp

        val list = itemsProperty().get()
        list.addListener(ListChangeListener<T> { c ->
            println("onChanged : $c")
            while (c.next()) {
                println("f:${c.from}, t:${c.to}")
                if (c.wasAdded()) {
//                    println("wasAdded")
                    val entity = list[c.from]
                    ITEM_BUSI_TO_TABLEITEM_MAP[ID_PROP_DELEGATE!!.getValue(entity)!!] = entity
                } else if (c.wasRemoved()) {
//                    println("wasRemoved")
                    list.filter {
                        !ITEM_BUSI_TO_TABLEITEM_MAP.containsKey(ID_PROP_DELEGATE!!.getValue(it)!!)
                    }.forEach {
                        ITEM_BUSI_TO_TABLEITEM_MAP.remove(ID_PROP_DELEGATE!!.getValue(it)!!)
                    }
                }
            }
        })
    }

//
//    /**
//     * 为主键值为p的对象更新整个实体值
//     * tableView.UpdateItem("111",TableTestVO("111", "33333", SimpleStringProperty("33333"), SimpleDoubleProperty(0.1)))
//     */
//    @Synchronized
//    fun UpdateItem(p:P,t: T){
//        val index = items.indexOfFirst {
//            p == ID_PROP!!.get(it)
//        }
//        if (index != -1) {
//            items[index] = t
//        }
//    }

    /**
     * 为主键值为p的对象更新整个实体值
     * tableView.UpdateItem("111",TableTestVO("111", "33333", SimpleStringProperty("33333"), SimpleDoubleProperty(0.1)))
     */
    @Synchronized
    fun UpdateItem(t: T) {
        val oldEntity = ITEM_BUSI_TO_TABLEITEM_MAP[ID_PROP!!.get(t)]
        oldEntity?.let { o ->
            val index = items.indexOfFirst {
                o == it
            }
            if (index != -1) {
                items[index] = t
            }
        }

    }

    /**
     * 为主键值为p的对象更新某个绑定的属性值
     * tableView.UpdateItem("222",TableTestVO::msg,"444444444")
     * tableView.UpdateItem("222",TableTestVO::processBar,0.5)
     */
    @Synchronized
    fun <V : Any> UpdateItem(p: P, propFunc: KMutableProperty1<T, out Property<V>>, v: V) {
        val oldEntity = ITEM_BUSI_TO_TABLEITEM_MAP[p]
        oldEntity?.let { o ->
            propFunc.get(o).value = v
        }
    }
//
//    /**
//     * 为主键值为p的对象更新某个绑定的属性值
//     * tableView.UpdateItem("222",TableTestVO::msg,"444444444")
//     * tableView.UpdateItem("222",TableTestVO::processBar,0.5)
//     */
//    @Synchronized
//    fun <V:Any> UpdateItem(p:P, propFunc: KMutableProperty1<T,out Property<V>>, v:V){
//        val index = items.indexOfFirst {
//            p == ID_PROP!!.get(it)
//        }
//        if (index != -1) {
//            propFunc.get(items[index]).value = v
//        }
//    }

}