package cn.lifay.ui.form.select

import cn.lifay.ui.form.FormElement
import javafx.scene.control.ChoiceBox
import javafx.scene.control.Control
import kotlin.reflect.KMutableProperty1

/**
 *@ClassName SelectElement
 *@Description TODO
 *@Author lifay
 *@Date 2023/2/5 13:39
 **/
class SelectElement<T, R : Any>(
    r: Class<R>,
    label: String,
    property: KMutableProperty1<T, R>,
    items: Collection<R?>
) :
    FormElement<T, R>(r, label, property) {

//    protected var control: ChoiceBox<R> = ChoiceBox<R>()

    init {
        init()
        loadItems(items)
    }

    override fun control(): Control {
        return ChoiceBox<R>()
    }
/*
    fun getValue(): R? {
        return control.getValue()
    }

    fun setValue(r: Any) {
        control.setValue(r as R)
    }*/

    fun loadItems(items: Collection<R?>) {
        (control as ChoiceBox<R>).getItems().clear()
        control.getItems().addAll(items)
    }
}