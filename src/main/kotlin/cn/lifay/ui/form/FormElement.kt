package cn.lifay.ui.form

import cn.lifay.extension.platformRun
import cn.lifay.extension.textFillColor
import cn.lifay.ui.DelegateProp
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableValue
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.layout.HBox
import java.util.function.Predicate
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1

/**
 * @ClassName FormElement
 * @Description TODO
 * @Author lifay
 * @Date 2023/1/8 10:09
 */
abstract class FormElement<T : Any, R : Any>(
    val r: Class<R>,
    val label: String,
    var primary: Boolean = false,
    var required: Boolean = false,
    val initValue: R? = null
) : HBox() {

    protected lateinit var node: Node

    private val predicate: Predicate<R>? = null
    var property: KMutableProperty1<T, R?>? = null
    var property2: KMutableProperty1<T, R>? = null
    var customProp: DelegateProp<T, R>? = null
    var tc: KClass<T>? = null

    fun init() {
        //  println("$label FormElement init")
        alignment = Pos.CENTER_LEFT
        val l = Label(label).textFillColor("#606266")
        l.padding = Insets(5.0, 10.0, 5.0, 10.0)
        children.add(l)
        padding = Insets(5.0, 10.0, 5.0, 10.0)
        node = registerGraphic()
        children.add(node)
    }

    /**
     *  注册输入值控件实例
     */
    abstract fun registerGraphic(): Node

    /**
     *  获取控件实例
     */
    abstract fun graphic(): Node

    /**
     *  获取控件值
     */
    abstract fun getElementValue(): R?

    /**
     *  设置控件值
     */
    abstract fun setElementValue(v: R?)

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
     *  如果有初始值，则设置
     */
    fun initValue() {
        if (initValue != null) {
            setElementValue(initValue)
        }
    }

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
        return when (node) {
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
        platformRun { node!!.isDisable = true }
    }

    /**
     *  获取对象属性名称
     */
    fun getPropName(): String {
        return if (customProp == null) {
            if (property2 != null) {
                property2!!.name
            } else {
                property!!.name
            }
        } else {
            customProp!!.fieldName
        }
    }

    /*
        */
    /**
     *  获取对象属性值
     *//*

    fun getPropValue(t: T):R? {
        return if (customProp == null) {
            property!!.get(t)
        } else {
            customProp!!.getValue(t)
        }
    }
*/

    /**
     *  对象属性值 转到 元素值
     */
    fun propToEle(t: T) {
        platformRun {
            val elementValue = if (customProp == null) {
                if (property2 != null) {
                    property2!!.get(t)
                } else {
                    property!!.get(t)
                }
            } else {
                customProp!!.getValue(t)
            }
            setElementValue(elementValue)
        }
    }

    /**
     *  元素值 转到 对象属性值
     */
    fun eleToProp(t: T) {
        val elementValue = getElementValue()

        if (customProp == null) {
            if (property2 != null) {
                if (elementValue != null) {
                    //如果控制有值 直接赋予控件值
                    property2!!.set(t, elementValue)
                } else {
                    //如果控制无值,实体属性有值 则赋予控件默认值
                    property2!!.set(t, defaultValue()!!)
                }
            } else {
                if (elementValue != null) {
                    //如果控制有值 直接赋予控件值
                    property!!.set(t, elementValue)
                } else if (property!!.get(t) != null) {
                    //如果控制无值,实体属性有值 则赋予控件默认值
                    property!!.set(t, defaultValue()!!)
                }
            }
        } else {
            if (elementValue != null) {
                //如果控制有值 直接赋予控件值
                customProp!!.setValue(t, elementValue)
            } else if (customProp!!.getValue(t) != null) {
                //如果控制无值,实体属性有值 则赋予控件默认值
                customProp!!.setValue(t, defaultValue())
            }
        }
    }

    /**
     *  获取元素在表格的表头
     */
    open fun getTableHead(): TableColumn<T, R> {
        val col = TableColumn<T, R>(label.replace(":", "").replace("：", ""))

        when (r) {
            java.lang.Boolean::class.java -> {
                col.setCellValueFactory { p: TableColumn.CellDataFeatures<T, R> ->
                    val b = getElementValue() as Boolean
                    val v = if (b) "是" else "否"
                    val property = SimpleStringProperty(v) as ObservableValue<R>
                    property
                }
            }

            else -> {
                col.cellValueFactory = PropertyValueFactory(getPropName())
            }
        }
        return col
    }

}