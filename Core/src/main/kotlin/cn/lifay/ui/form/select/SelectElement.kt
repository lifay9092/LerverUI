package cn.lifay.ui.form.select

import cn.lifay.ui.DelegateProp
import cn.lifay.ui.form.FormElement
import javafx.scene.Node
import javafx.scene.control.ChoiceBox
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1

/**
 *@ClassName SelectElement
 *@Description 选择下拉框-元素
 *@Author lifay
 *@Date 2023/2/5 13:39
 **/
class SelectElement<T : Any, R : Any> constructor(
    t: KClass<T>,
    r: Class<R>,
    label: String,
    property2: KMutableProperty1<T, R>?,
    property: KMutableProperty1<T, R?>?,
    customProp: DelegateProp<T, R>?,
    items: Collection<R?>,
    required: Boolean = false,
    initValue: R? = null,
    val nodeBuild: (ChoiceBox<R>.() -> Unit)?
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
        inline operator fun <reified T : Any, reified R : Any> invoke(
            label: String,
            property: KMutableProperty1<T, R>,
            items: Collection<R?>,
            initValue: R? = null,
            noinline nodeBuild: (ChoiceBox<R>.() -> Unit)? = null
        ) = SelectElement(T::class, R::class.java, label, property, null, null, items, false, initValue, nodeBuild)

        /*注入 property 返回值不为空 对应var ? */
        inline operator fun <reified T : Any, reified R : Any> invoke(
            label: String,
            property: KMutableProperty1<T, R?>,
            items: Collection<R?>,
            required: Boolean = false,
            initValue: R? = null,
            noinline nodeBuild: (ChoiceBox<R>.() -> Unit)? = null
        ) = SelectElement(T::class, R::class.java, label, null, property, null, items, required, initValue, nodeBuild)

        /*注入 customProp javabean */
        inline operator fun <reified T : Any, reified R : Any> invoke(
            label: String,
            customProp: DelegateProp<T, R>,
            items: Collection<R>,
            required: Boolean = false,
            initValue: R? = null,
            noinline nodeBuild: (ChoiceBox<R>.() -> Unit)? = null
        ) = SelectElement(T::class, R::class.java, label, null, null, customProp, items, required, initValue, nodeBuild)

    }

//    protected var control: ChoiceBox<R> = ChoiceBox<R>()


    override fun registerGraphic(): Node {
        return ChoiceBox<R>().apply {
            nodeBuild?.let { it(this) }
        }
    }

    override fun graphic(): ChoiceBox<R?> {
        return node as ChoiceBox<R?>
    }

    override fun getElementValue(): R? {
        return graphic().value as R?
    }

    override fun setElementValue(v: R?) {
        graphic().value = v
    }

    override fun clear() {
        graphic().value = defaultValue()
    }

    override fun verify(): Boolean {
        return graphic().value == null
    }

    override fun defaultValue(): R? {
        return initValue
    }

    fun loadItems(items: Collection<R?>) {
        val choiceBox = node as ChoiceBox<R>
        choiceBox.items.clear()
        choiceBox.items.addAll(items)
    }
}