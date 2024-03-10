package cn.lifay;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DbStartup {

    public static void main(String[] args) {


        GlobeStartUp.launch(false, () -> {
            var pane = new VBox(42D);
            pane.getChildren().add(new Button("dasdsadasd"));

            var stage = new Stage();
            stage.setTitle("首页");
            stage.centerOnScreen();
            stage.setScene(new Scene(pane));
            return stage;
        });
    }
}
