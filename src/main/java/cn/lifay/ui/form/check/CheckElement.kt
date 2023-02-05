package cn.lifay.ui.form.check

import cn.lifay.ui.form.FormElement
import javafx.scene.control.CheckBox
import javafx.scene.control.Control
import kotlin.reflect.KMutableProperty1

/**
 *@ClassName CheckElement
 *@Description TODO
 *@Author lifay
 *@Date 2023/2/5 13:53
 **/
class CheckElement<T, R : Any>(
    r: Class<R>,
    label: String,
    property: KMutableProperty1<T, R>
) :
    FormElement<T, R>(r, label, property) {


    init {
        init()
    }

    override fun control(): Control {
        return CheckBox()
    }
/*
    fun getValue(): R? {
        return control.getValue()
    }

    fun setValue(r: Any) {
        control.setValue(r as R)
    }*/

}