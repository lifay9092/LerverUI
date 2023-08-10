package cn.lifay.ui.form.select

import cn.lifay.ui.DelegateProp
import cn.lifay.ui.form.FormElement
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableValue
import javafx.scene.Node
import javafx.scene.control.ChoiceBox
import javafx.scene.control.TableColumn
import javafx.scene.control.cell.PropertyValueFactory
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1

/**
 *@ClassName SelectFkElement
 *@Description 选择下拉框-元素
 *@Author lifay
 *@Date 2023/2/5 13:39
 **/
class SelectFkElement<T : Any, R : Any, F : Any> constructor(
    t: KClass<T>,
    r: Class<R>,
    val f: Class<F>,
    label: String,
    property2: KMutableProperty1<T, R>?,
    private val eleProperty2: KMutableProperty1<F, R>?,
    property: KMutableProperty1<T, R?>?,
    private val eleProperty: KMutableProperty1<F, R?>?,
    customProp: DelegateProp<T, R>?,
    private val eleCustomProp: DelegateProp<F, R>?,
    items: Collection<F>,
    required: Boolean = false,
    initValue: R? = null,
    private val getForeignFunc: (R) -> F
) :
    FormElement<T, R>(r, label, required = required, initValue = initValue) {

    init {
        super.property2 = property2
        super.property = property
        super.customProp = customProp
        super.tc = t

        init()
        loadItems(items)
    }

    companion object {
        /*注入 property 返回值不为空 对应var 没有? */
        inline operator fun <reified T : Any, reified R : Any, reified F : Any> invoke(
            label: String,
            property: KMutableProperty1<T, R>,
            eleProperty: KMutableProperty1<F, R>,
            items: Collection<F>,
            initValue: R? = null,
            noinline getForeignFunc: (R) -> F
        ) = SelectFkElement(
            T::class,
            R::class.java,
            F::class.java,
            label,
            property,
            eleProperty,
            null,
            null,
            null,
            null,
            items,
            false,
            initValue,
            getForeignFunc
        )

        /*注入 property 返回值不为空 对应var ? */
        inline operator fun <reified T : Any, reified R : Any, reified F : Any> invoke(
            label: String,
            property: KMutableProperty1<T, R?>,
            eleProperty: KMutableProperty1<F, R?>,
            items: Collection<F>,
            required: Boolean = false,
            initValue: R? = null,
            noinline getForeignFunc: (R) -> F
        ) = SelectFkElement(
            T::class,
            R::class.java,
            F::class.java,
            label,
            null,
            null,
            property,
            eleProperty,
            null,
            null,
            items,
            required,
            initValue,
            getForeignFunc
        )

        /*注入 customProp javabean */
        inline operator fun <reified T : Any, reified R : Any, reified F : Any> invoke(
            label: String,
            customProp: DelegateProp<T, R>,
            eleCustomProp: DelegateProp<F, R>,
            items: Collection<F>,
            required: Boolean = false,
            initValue: R? = null,
            noinline getForeignFunc: (R) -> F
        ) = SelectFkElement(
            T::class,
            R::class.java,
            F::class.java,
            label,
            null,
            null,
            null,
            null,
            customProp,
            eleCustomProp,
            items,
            required,
            initValue,
            getForeignFunc
        )

    }

//    protected var control: ChoiceBox<R> = ChoiceBox<R>()


    override fun registerGraphic(): Node {
        return ChoiceBox<F>()
    }

    override fun graphic(): ChoiceBox<F> {
        return node as ChoiceBox<F>
    }

    override fun getElementValue(): R? {
        if (eleProperty2 != null) {
            return eleProperty2.get(graphic().value)
        } else if (eleProperty != null) {
            return eleProperty.get(graphic().value)
        }
        return eleCustomProp!!.getValue(graphic().value)
    }

    override fun setElementValue(v: R?) {
        graphic().value = getForeignFunc(v!!)
    }

    override fun clear() {
        graphic().value = null
    }

    override fun verify(): Boolean {
        return graphic().value == null
    }

    override fun defaultValue(): R? {
        return when (r) {
            java.lang.String::class.java -> {
                ""
            }

            java.lang.Integer::class.java, java.lang.Long::class.java -> {
                0
            }

            java.lang.Double::class.java, java.lang.Float::class.java -> {
                0.0
            }

            else -> {}
        } as R
    }

    fun loadItems(items: Collection<F>) {
        val choiceBox = node as ChoiceBox<F>
        choiceBox.items.clear()
        choiceBox.items.addAll(items)
    }

    override fun getTableHead(): TableColumn<T, R> {
        val col = TableColumn<T, R>(label.replace(":", "").replace("：", ""))

        if (r == f) {
            //同类型
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
        col.setCellValueFactory { p: TableColumn.CellDataFeatures<T, R> ->
            val robj = if (property2 != null) {
                property2!!.get(p.value)
            } else if (property != null) {
                property!!.get(p.value)
            } else {
                customProp!!.getValue(p.value)
            }
            val fobj = getForeignFunc(robj!!)
            val property = SimpleStringProperty(fobj.toString()) as ObservableValue<R>
            property
        }
        return col
    }
}