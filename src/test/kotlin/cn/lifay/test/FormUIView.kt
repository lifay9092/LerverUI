package cn.lifay.test

import javafx.event.ActionEvent


/**
 * FormUIView TODO
 * @author lifay
 * @date 2023/2/24 17:22
 **/
class FormUIView {
    fun formTest(actionEvent: ActionEvent) {
        val userForm = UserForm("测试", UserData(1, "111111", SelectTypeEnum.C, true, "男"))
        userForm.show()
    }

    fun formTest2(actionEvent: ActionEvent) {
        val personForm = PersonForm(Person(1, "111111", true))
        personForm.show()
    }

    fun formMsg(actionEvent: ActionEvent) {

    }


}