package cn.lifay.ui.form.btn

import cn.lifay.ui.form.FormUI
import javafx.scene.control.Button

/**
 *@ClassName CustomButton
 *@Description TODO
 *@Author lifay
 *@Date 2023/4/5 20:11
 **/
class CustomButton<T : FormUI<*>>(val btn: Button, val actionFunc: (T) -> Unit) {


}