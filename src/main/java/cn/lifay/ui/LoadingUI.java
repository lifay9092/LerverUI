package cn.lifay.ui;

import cn.lifay.StaticUtil;
import cn.lifay.ui.form.FormElement;
import cn.lifay.ui.form.User;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *@ClassName LoadingUI
 *@Description 等待组件
 *@Author lifay
 *@Date 2022/10/9 20:26
 **/
public class LoadingUI {

    protected Stage stage;
    protected StackPane root;
    protected Label messageLb;
    public void test() {
       // FormElement<User,String> f = FormElement.text("测试",new TextField(),User::getName);
    }
    public LoadingUI(Stage owner) {
        ImageView loadingView = new ImageView(
                new Image(StaticUtil.loading()));// 可替换

        messageLb = new Label("请耐心等待...");
        messageLb.setFont(Font.font(20));

        root = new StackPane();
        root.setMouseTransparent(true);
        root.setPrefSize(owner.getWidth(), owner.getHeight());
        root.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0, 0.3), null, null)));
        root.getChildren().addAll(loadingView, messageLb);

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);

        stage = new Stage();
        stage.setScene(scene);
        stage.setResizable(false);
        stage.initOwner(owner);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.getIcons().addAll(owner.getIcons());
        stage.setX(owner.getX());
        stage.setY(owner.getY());
        stage.setHeight(owner.getHeight());
        stage.setWidth(owner.getWidth());
    }

    /**
     * 显示指定信息
     * @param message 1
     * @author lifay
     * @return void
     */
    public void showMessage(String message) {
        Platform.runLater(() -> messageLb.setText(message));
    }

    // 显示
    public void show() {
        Platform.runLater(() -> stage.show());
    }

    // 关闭
    public void closeStage() {
        Platform.runLater(() -> stage.close());
    }

}
