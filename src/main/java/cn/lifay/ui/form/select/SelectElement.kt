package cn.lifay.ui.form.select

import cn.lifay.ui.form.FormElement
import javafx.scene.Node
import javafx.scene.control.ChoiceBox
import java.awt.SystemColor.text
import kotlin.reflect.KMutableProperty1

/**
 *@ClassName SelectElement
 *@Description 选择下拉框-元素
 *@Author lifay
 *@Date 2023/2/5 13:39
 **/
class SelectElement<T, R : Any>(
    r: Class<R>,
    label: String,
    property: KMutableProperty1<T, R?>,
    required: Boolean = false,
    items: Collection<R?>
) :
    FormElement<T, R>(r, label, property,required = required) {

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