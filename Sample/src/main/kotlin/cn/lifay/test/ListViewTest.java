package cn.lifay.test;

import atlantafx.base.theme.PrimerLight;
import cn.lifay.global.LerverResource;
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
public class ListViewTest extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        LerverResource.loadTheme(new PrimerLight());
        FXMLLoader fxmlLoader = new FXMLLoader(ListViewTest.class.getResource("list.fxml"));
        Parent load = fxmlLoader.load();
        Scene scene = new Scene(load);

        primaryStage.setTitle("Hello World");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}