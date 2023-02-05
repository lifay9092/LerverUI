/*
package cn.lifay.ui.form.text;

import javafx.scene.control.TextInputControl;

*/
/**
 *@ClassName IntegerTextElement
 *@Description TODO
 *@Author 李方宇
 *@Date 2023/1/11 9:22
 **//*

public class IntegerTextElement<T> extends TextElement<T, Integer> {
    public IntegerTextElement(String label, String fieldName) {
        super(label, fieldName);
    }
    public IntegerTextElement(String label, String fieldName,boolean primary) {
        super(label, fieldName, primary);
    }
    public IntegerTextElement(String label, String fieldName,boolean primary,boolean isTextArea) {
        super(label, fieldName, primary,isTextArea);
    }

    @Override
    public Integer getValue() {
        TextInputControl inputControl = (TextInputControl) control();
        return inputControl.getText().isBlank() ? null : Integer.valueOf(inputControl.getText());
    }

    @Override
    public void setValue(Object value) {
        TextInputControl inputControl = (TextInputControl) control();
        inputControl.setText(value.toString());
    }
}*/
