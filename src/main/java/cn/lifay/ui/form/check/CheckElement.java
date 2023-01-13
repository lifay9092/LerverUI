package cn.lifay.ui.form.check;

import cn.lifay.ui.form.FormElement;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Control;

/**
 *@ClassName CheckElement
 *@Description TODO
 *@Author 李方宇
 *@Date 2023/1/11 18:45
 **/
public class CheckElement<T> extends FormElement<T,Boolean> {

    public CheckBox choiceBox = new CheckBox();

    public CheckElement(String label, String fieldName) {
        super(label, fieldName);
        init();
    }

    @Override
    public Control control() {
        return choiceBox;
    }

    @Override
    public Boolean getValue() {
        return choiceBox.isSelected();
    }

    @Override
    public void setValue(Object r) {
        choiceBox.setSelected((Boolean) r);
    }
}