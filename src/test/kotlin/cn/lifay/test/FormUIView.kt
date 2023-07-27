package cn.lifay.test

import cn.lifay.extension.alertConfirmation
import cn.lifay.extension.alertWarn
import cn.lifay.ui.message.Message.Companion.show
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.ButtonBar.ButtonData
import javafx.scene.control.ButtonType
import javafx.scene.layout.AnchorPane


/**
 * FormUIView TODO
 * @author lifay
 * @date 2023/2/24 17:22
 **/
class FormUIView {
    @FXML
    var rootPane = AnchorPane()

    fun formTest(actionEvent: ActionEvent) {
        val userForm = UserForm()
//        val userForm = UserForm("测试", UserData(1, "111111", SelectTypeEnum.C, true, "男"))
        userForm.show()
    }

    fun formTest2(actionEvent: ActionEvent) {
        val personForm = PersonForm(Person(1, "111111", true))
        personForm.show()
    }

    fun formMsg(actionEvent: ActionEvent) {

    }

    fun confirmationOld(actionEvent: ActionEvent) {
        alertConfirmation("将要执行删除操作,是否继续?")
    }
    fun confirmation(actionEvent: ActionEvent) {
        val alert = Alert(AlertType.CONFIRMATION)
       // alert.title = "Confirmation Dialog"
        //alert.headerText = "header"
        alert.contentText = "text"
//
//        val yesBtn = ButtonType("Yes", ButtonData.YES)
//        val noBtn = ButtonType("No", ButtonData.NO)
//        val cancelBtn = ButtonType(
//            "Cancel", ButtonData.CANCEL_CLOSE
//        )
//
//        alert.buttonTypes.setAll(yesBtn, noBtn, cancelBtn)
//        alert.initOwner(rootPane.scene.window)
        alert.showAndWait()
    }


}