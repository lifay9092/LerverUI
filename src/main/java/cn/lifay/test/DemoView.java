package cn.lifay.test;

import javafx.event.ActionEvent;

/**
 *@ClassName DemoView
 *@Description TODO
 *@Author 李方宇
 *@Date 2023/1/9 16:14
 **/
public class DemoView {
    public void formTest(ActionEvent actionEvent) {
        System.out.println("ddd");
        // UserForm userForm = new UserForm("测试",User.class);
        UserForm userForm = new UserForm("测试", new User(1, "111111"));
        userForm.show();
    }

}