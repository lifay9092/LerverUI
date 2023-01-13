package cn.lifay;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *@ClassName Demo
 *@Description TODO
 *@Author 李方宇
 *@Date 2023/1/9 16:07
 **/
public class Demo extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
//        MyTypeToken<User> myTypeToken = new MyTypeToken<User>();
//        Type type = myTypeToken.getType();
//        System.out.println("泛型T name:" + type.getTypeName());

        FXMLLoader fxmlLoader = new FXMLLoader(Demo.class.getResource("demo.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}