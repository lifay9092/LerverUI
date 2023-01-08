package cn.lifay.ui.form;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.Collection;

/**
 *@ClassName FormElement
 *@Description TODO
 *@Author lifay
 *@Date 2023/1/8 10:09
 **/
public class FormElement<T, R> extends Group {

    //    private Function<T, R> getValueFunc;
    private SetFunc<T, R> setValueFunc;

    private TextField textField = new TextField();

    private FormElement() {
    }

    private FormElement(Node... nodes) {
        super(nodes);
    }

    private FormElement(Collection<Node> collection) {
        super(collection);
    }

    //
//    private FormElement(String label, Function<T, R> getValueFunc, SetFunc<T, R> setValueFunc) {
//        super(new Label(label));
//        //TextField textField = new TextField();
//        textField.textProperty().addListener((observableValue, oldValue, newValue) -> {
//
//        });
//        getChildren().add(textField);
//        setAutoSizeChildren(true);
//        this.getValueFunc = getValueFunc;
//        this.setValueFunc = setValueFunc;
//    }
    private FormElement(String label, SetFunc<T, R> setValueFunc) {
        new FormElement<>(label, setValueFunc, false);
    }

    private FormElement(String label, SetFunc<T, R> setValueFunc, boolean disable) {
        super(new Label(label));
        //TextField textField = new TextField();
        textField.setDisable(disable);
//        textField.textProperty().addListener((observableValue, oldValue, newValue) -> {
//
//        });
        getChildren().add(textField);
        setAutoSizeChildren(true);
        this.setValueFunc = setValueFunc;
    }

    public R value() {

        return (R) textField.getText();
    }

    public String valueStr() {
        return textField.getText().trim();
    }

    public void setValueFunc(T t) {
        setValueFunc.set(t, value());
    }

    public static <T, R> FormElement<T, R> text(String label, SetFunc<T, R> setValueFunc) {

        return new FormElement<>(label, setValueFunc);
    }

}
