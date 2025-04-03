package cn.lifay.ui.form.btn

import cn.lifay.ui.form.BaseFormUI
import cn.lifay.ui.form.CurdUI
import cn.lifay.ui.form.DataFormUI
import javafx.scene.control.Button

/**
 *@ClassName CustomButton
 *@Description TODO
 *@Author lifay
 *@Date 2023/4/5 20:11
 **/
class CustomButton<T : DataFormUI<*>>(val btn: Button, val actionFunc: (T) -> Unit) {

}

class BaseButton<T : BaseFormUI<*>>(val btn: Button, val actionFunc: (T) -> Unit) {

    fun disable() {
        btn.isDisable = true
    }

    fun enable() {
        btn.isDisable = false
    }
}


class CurdButton<T : CurdUI<*>>(val btn: Button, val actionFunc: (T) -> Unit) {

    fun disable() {
        btn.isDisable = true
    }

    fun enable() {
        btn.isDisable = false
    }
}


