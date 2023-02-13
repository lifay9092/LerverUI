package cn.lifay.test;

import cn.lifay.ui.message.ErrorMessage;
import cn.lifay.ui.message.InfoMessage;
import cn.lifay.ui.message.WarnMessage;
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

    public void warn(ActionEvent actionEvent) {
        WarnMessage warnMessage = new WarnMessage(rootPane.getScene().getWindow());
        warnMessage.show("警告。。。。。大萨达多撒多撒多撒大所多所大多大大声的。。");
    }

    public void error(ActionEvent actionEvent) {
        ErrorMessage errorMessage = new ErrorMessage(rootPane.getScene().getWindow());
        errorMessage.show("错误。。。。。。。");
    }

}