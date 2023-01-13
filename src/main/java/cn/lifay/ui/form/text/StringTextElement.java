package cn.lifay.ui.form.text;

import javafx.scene.control.Control;
import javafx.scene.control.TextInputControl;

/**
 *@ClassName StringTextElement
 *@Description TODO
 *@Author 李方宇
 *@Date 2023/1/11 9:20
 **/
public class StringTextElement<T> extends TextElement<T, String> {
    public StringTextElement(String label, String fieldName) {
        super(label, fieldName);
    }
    public StringTextElement(String label, String fieldName,boolean primary) {
        super(label, fieldName, primary);
    }
    public StringTextElement(String label, String fieldName,boolean primary,boolean isTextArea) {
        super(label, fieldName, primary,isTextArea);
    }

    @Override
    public String getValue() {
        TextInputControl inputControl = (TextInputControl) control();
        return inputControl.getText();
    }

    @Override
    public void setValue(Object value) {

        TextInputControl inputControl = (TextInputControl) control();
        inputControl.setText(value.toString());
        if (!verify()) {

        }
    }
}