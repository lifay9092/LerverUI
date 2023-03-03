package cn.lifay.ui.form.select

import cn.lifay.ui.DelegateProp
import cn.lifay.ui.form.FormElement
import javafx.scene.Node
import javafx.scene.control.ChoiceBox
import kotlin.reflect.KMutableProperty1

/**
 *@ClassName SelectElement
 *@Description 选择下拉框-元素
 *@Author lifay
 *@Date 2023/2/5 13:39
 **/
class SelectElement<T : Any, R : Any> constructor(
    r: Class<R>,
    label: String,
    property: KMutableProperty1<T, R?>?,
    customProp: DelegateProp<T, R>? = null,
    items: Collection<R?>,
    required: Boolean = false
) :
    FormElement<T, R>(r, label, property, customProp, required = required) {

    companion object {
        inline operator fun <reified T : Any, reified R : Any> invoke(
            label: String,
            property: KMutableProperty1<T, R?>,
            items: Collection<R?>,
            required: Boolean = false
        ) = SelectElement(R::class.java, label, property, null, items, required)

        inline operator fun <reified T : Any, reified R : Any> invoke(
            label: String,
            customProp: DelegateProp<T, R>,
            items: Collection<R?>,
            required: Boolean = false
        ) = SelectElement(R::class.java, label, null, customProp, items, required)

    }

//    protected var control: ChoiceBox<R> = ChoiceBox<R>()

    init {
        init()
        loadItems(items)
    }

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