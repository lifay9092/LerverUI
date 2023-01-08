package cn.lifay.ui.form;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.Collection;
import java.util.function.Function;

/**
 *@ClassName FormElement
 *@Description TODO
 *@Author lifay
 *@Date 2023/1/8 10:09
 **/
public class FormElement<T, R extends SimpleStringProperty> extends Group {

    private Function<T, R> getValueFunc;
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

    private FormElement(String label, Function<T, R> getValueFunc, SetFunc<T, R> setValueFunc) {
        super(new Label(label));
        //TextField textField = new TextField();
        textField.textProperty().addListener((observableValue, oldValue, newValue) -> {

        });
        getChildren().add(textField);
        setAutoSizeChildren(true);
        this.getValueFunc = getValueFunc;
        this.setValueFunc = setValueFunc;
    }

    public R value(T t) {
        return getValueFunc.apply(t);
    }

    public String valueStr(T t) {
        return getValueFunc.apply(t).get();
    }

    public static <T, R extends SimpleStringProperty> FormElement<T, R> text(String label, Function<T, R> getValueFunc, SetFunc<T, R> setValueFunc) {

        return new FormElement<>(label, getValueFunc, setValueFunc);
    }

}
