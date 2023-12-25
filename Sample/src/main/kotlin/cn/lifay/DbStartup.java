package cn.lifay;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DbStartup {

    public static void main(String[] args) {


        GlobeStartUp.launch(() -> {
            VBox pane = new VBox(22D);
            pane.getChildren().add(new Button("dasdsadasd"));

            Stage stage = new Stage();
            stage.setTitle("首页");
            stage.centerOnScreen();
            stage.setScene(new Scene(pane));
            return stage;
        });
    }
}
