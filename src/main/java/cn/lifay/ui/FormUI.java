package cn.lifay.ui;

import cn.lifay.ui.form.FormElement;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 *@ClassName FormUI
 *@Description form表单：动态添加元素（文本框、选择），基本操作（保存、编辑、删除、清除）
 *@Author lifay
 *@Date 2023/1/8 10:05
 **/
public abstract class FormUI<T> extends Stage {

    private T t;
    private StackPane pane = new StackPane();
    protected List<FormElement<T,?>> elements = FXCollections.emptyObservableList();
    protected Button saveBtn = new Button("保存");
    protected Button editBtn = new Button("修改");
    protected Button delBtn = new Button("删除");

    public FormUI(String title,Class<T> clazz) {
        this.setTitle(title);
        this.setScene(new Scene(pane));
        pane.getChildren().addAll(elements);
        pane.getChildren().addAll(saveBtn,editBtn,delBtn);

        try {
            t = clazz.getDeclaredConstructor().newInstance();

            saveBtn.setOnMouseClicked(mouseEvent -> {
                if (mouseEvent.getClickCount() == 1) {
                    try {
                        saveBtn.setDisable(true);
                        if (saveData(t)) {
                            Platform.runLater(() ->{
                                new Alert(Alert.AlertType.INFORMATION, "保存成功").show();
                            });
                        }else {
                            Platform.runLater(() ->{
                                new Alert(Alert.AlertType.ERROR, "保存失败").show();
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    } finally {
                        saveBtn.setDisable(false);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public abstract void addElements();
    public abstract boolean saveData(T t);

}
