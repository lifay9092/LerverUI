package cn.lifay.ui.form

import cn.lifay.extension.platformRun
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableValue
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.layout.HBox
import java.util.function.Predicate
import kotlin.reflect.KMutableProperty1

/**
 * @ClassName FormElement
 * @Description TODO
 * @Author lifay
 * @Date 2023/1/8 10:09
 */
abstract class FormElement<T, R : Any>(
    val r: Class<R>,
    val label: String,
    val property: KMutableProperty1<T, R?>,
    var primary: Boolean = false,
    var required: Boolean = false,
    val defaultValue: R? = null
) :
    HBox() {

    protected val graphic: Node? = registerGraphic()
    /*
        var property: KMutableProperty1<T, R>? = null

        var isPrimary = false*/

    private val predicate: Predicate<R>? = null


    /*    protected constructor(label: String, property: KMutableProperty1<T, R>, primary: Boolean = false) {
            alignment = Pos.CENTER_LEFT
            val l = Label(label)
            l.padding = Insets(5.0, 10.0, 5.0, 10.0)
            children.add(l)
            padding = Insets(5.0, 10.0, 5.0, 10.0)
            this.property = property
            isPrimary = primary
        }*/

    fun init() {
        alignment = Pos.CENTER_LEFT
        val l = Label(label)
        l.padding = Insets(5.0, 10.0, 5.0, 10.0)
        children.add(l)
        padding = Insets(5.0, 10.0, 5.0, 10.0)
//        this.elementValue = defaultValue()
        children.add(graphic)
    }

    var elementValue: R?
        get() {
            val v = get()
            if (v == null && defaultValue != null) {
                return defaultValue
            }
            return v
        }
        set(value) {
            set(value)
        }

    /**
     *  注册输入值控件实例
     */
    abstract fun registerGraphic(): Node?

    /**
     *  获取控件实例
     */
    abstract fun graphic(): Node

    /**
     *  获取控件值
     */
    abstract fun get(): R?

    /**
     *  设置控件值
     */
    abstract fun set(v: R?)

    /**
     *  置空
     */
    abstract fun clear()

    /**
     * 校验
     */
    abstract fun verify(): Boolean

    /**
     *  获取默认值
     */
    abstract fun defaultValue(): R?

    /**
     *  检查必传
     */
    fun checkRequired(): Boolean {
        if (required && !verify()) {
            return false
        }
        return true
    }

    /**
     *  获取控件值
     */
    private fun initControlValue(): Any? {
        return when (graphic) {
            is TextField, is TextArea -> {
                ""
            }

            is CheckBox -> {
                false
            }

            else -> {
                null
            }
        }
    }

    /**
     *  控件不可编辑
     */
    fun disable() {
        platformRun { graphic!!.isDisable = true }
    }

    /**
     *  对象属性值 转到 元素值
     */
    fun setEle(t: T) {
        val v = property.get(t)
        this.elementValue = v

    }

    /**
     *  元素值 转到 对象属性值
     */
    fun setProp(t: T) {
        property.set(t, elementValue)
    }

    /**
     *  获取表格的表头
     */
    fun getTableHead(): TableColumn<T, R> {
        val col = TableColumn<T, R>(label.replace(":", "").replace("：", ""))
        col.style = "-fx-alignment: CENTER;"
//        col.prefWidth = 10.0
        when (r) {
            java.lang.Boolean::class.java -> {
                col.setCellValueFactory { p: TableColumn.CellDataFeatures<T, R> ->
                    val b = property.get(p.value) as Boolean
                    val v = if (b) "是" else "否"
                    val property = SimpleStringProperty(v) as ObservableValue<R>
                    property
                }
            }

            else -> {
                col.cellValueFactory = PropertyValueFactory(property.name)
            }
        }
        return col
    }

//    private fun getWidth(v:String,head:String):Double{
//
//    }
}