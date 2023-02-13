package cn.lifay.test

import cn.lifay.ui.message.ErrorMessage
import cn.lifay.ui.message.InfoMessage
import cn.lifay.ui.message.WarnMessage
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.layout.AnchorPane

/**
 * @ClassName DemoView
 * @Description TODO
 * @Author 李方宇
 * @Date 2023/1/9 16:14
 */
class DemoView {
    @FXML
    lateinit var rootPane : AnchorPane

    fun formTest(actionEvent: ActionEvent?) {
        // UserForm userForm = new UserForm("测试",User.class);
//        UserNewForm userForm = new UserNewForm("测试", new UserData(1, "111111"));
        val userForm = UserForm("测试", UserData(1, "111111", SelectTypeEnum.C, true, "男"))
        userForm.show()
    }

    fun info(actionEvent: ActionEvent?) {
        val infoMessage = InfoMessage(rootPane.scene.window)
        infoMessage.show("信息。。。。。。。")
    }

    fun warn(actionEvent: ActionEvent?) {
        val warnMessage = WarnMessage(rootPane.scene.window)
        warnMessage.show("警告。。。。。大萨达多撒多撒多撒大所多所大多大大声的。。")
    }

    fun error(actionEvent: ActionEvent?) {
        val errorMessage = ErrorMessage(rootPane.scene.window)
        errorMessage.show("错误。。。。。。。")
    }
}