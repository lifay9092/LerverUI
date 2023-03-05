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
    required: Boolean = false
) :
    FormElement<T, R>(r, label, required = required) {

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
        ) = SelectElement(T::class, R::class.java, label, property, null, null, items, false)

        /*注入 property 返回值不为空 对应var ? */
        inline operator fun <reified T : Any, reified R : Any> invoke(
            label: String,
            property: KMutableProperty1<T, R?>,
            items: Collection<R?>,
            required: Boolean = false
        ) = SelectElement(T::class, R::class.java, label, null, property, null, items, required)

        /*注入 customProp javabean */
        inline operator fun <reified T : Any, reified R : Any> invoke(
            label: String,
            customProp: DelegateProp<T, R>,
            items: Collection<R?>,
            required: Boolean = false
        ) = SelectElement(T::class, R::class.java, label, null, null, customProp, items, required)

    }

//    protected var control: ChoiceBox<R> = ChoiceBox<R>()


    override fun registerGraphic(): Node {
        return ChoiceBox<R>()
    }

    override fun graphic(): ChoiceBox<*> {
        return graphic as ChoiceBox<*>
    }

    override fun get(): R? {
        return graphic().value as R?
    }

    override fun set(v: R?) {
        graphic().value = v
    }

    override fun clear() {
        graphic().value = defaultValue()
    }

    override fun verify(): Boolean {
        return graphic().value == null
    }

    override fun defaultValue(): R? {
        return null
    }

    fun loadItems(items: Collection<R?>) {
        val choiceBox = graphic as ChoiceBox<R>
        choiceBox.items.clear()
        choiceBox.items.addAll(items)
    }
}