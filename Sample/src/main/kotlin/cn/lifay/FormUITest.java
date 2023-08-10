package cn.lifay;

import atlantafx.base.theme.PrimerLight;
import cn.lifay.global.GlobalResource;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * FormUITest TODO
 *
 * @author lifay
 * @date 2023/2/24 17:19
 **/
public class FormUITest extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        GlobalResource.loadTheme(new PrimerLight());
        FXMLLoader fxmlLoader = new FXMLLoader(FormUITest.class.getResource("formTest.fxml"));
        Parent load = fxmlLoader.load();
        Scene scene = new Scene(load);

        primaryStage.setTitle("Hello World");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}