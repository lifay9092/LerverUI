package cn.lifay;

import cn.lifay.test.EasyView;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import xyz.yuelai.FXApplication;
import xyz.yuelai.View;

/**
 * Demo 测试类
 * @author lifay
 * @date 2023/1/9 16:07
 **/
public class Demo extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
//        MyTypeToken<User> myTypeToken = new MyTypeToken<User>();
//        Type type = myTypeToken.getType();
//        System.out.println("泛型T name:" + type.getTypeName());
/*
        FXMLLoader fxmlLoader = new FXMLLoader(Demo.class.getResource("demo.fxml"));
        Parent load = fxmlLoader.load();
        Scene scene = new Scene(load);
        primaryStage.setScene(scene);
        primaryStage.show();*/
        FXApplication.setElementStyleEnable(true);
        EasyView view = View.createView(EasyView.class);
        Scene scene = new Scene(view.getRoot());
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}