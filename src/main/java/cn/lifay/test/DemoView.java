package cn.lifay.test;

import cn.lifay.ui.message.InfoMessage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

/**
 *@ClassName DemoView
 *@Description TODO
 *@Author 李方宇
 *@Date 2023/1/9 16:14
 **/
public class DemoView {
    @FXML
    private AnchorPane rootPane;

    public void formTest(ActionEvent actionEvent) {
        // UserForm userForm = new UserForm("测试",User.class);
//        UserNewForm userForm = new UserNewForm("测试", new UserData(1, "111111"));
        UserForm userForm = new UserForm("测试", new UserData(1, "111111", SelectTypeEnum.C, true, "男"));
        userForm.show();
    }

    public void info(ActionEvent actionEvent) {

        InfoMessage infoMessage = new InfoMessage(rootPane.getScene().getWindow());
        infoMessage.show("信息。。。。。。。");
    }
}