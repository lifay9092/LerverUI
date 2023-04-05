package cn.lifay.ui.form.btn

import javafx.scene.control.Button

/**
 *@ClassName CustomButton
 *@Description TODO
 *@Author lifay
 *@Date 2023/4/5 20:11
 **/
class CustomButton<T : Any>(val btn: Button, val actionFunc: (T) -> Unit) {


}