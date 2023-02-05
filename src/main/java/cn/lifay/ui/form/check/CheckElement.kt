package cn.lifay.ui.form.check

import cn.lifay.ui.form.FormElement
import javafx.scene.control.CheckBox
import javafx.scene.control.Control
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1

/**
 *@ClassName CheckElement
 *@Description TODO
 *@Author lifay
 *@Date 2023/2/5 13:53
 **/
class CheckElement<T, R : Any>(
    r: KClass<R>,
    label: String,
    property: KMutableProperty1<T, R>
) :
    FormElement<T, R>(r, label, property) {

    protected var control: CheckBox = CheckBox()

    init {
        init()
    }

    override fun control(): Control {
        return control
    }
/*
    fun getValue(): R? {
        return control.getValue()
    }

    fun setValue(r: Any) {
        control.setValue(r as R)
    }*/

}