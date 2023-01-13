package cn.lifay.ui.form.text;

import cn.lifay.ui.form.FormElement;
import javafx.scene.control.Control;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 *@ClassName TextElement
 *@Description TODO
 *@Author 李方宇
 *@Date 2023/1/10 17:30
 **/
public abstract class TextElement<T, R> extends FormElement<T, R> {

    private TextField textField;
    private TextArea textArea;
    private final boolean isTextArea;

    public TextElement(String label, String fieldName) {
        this(label, fieldName, false);
    }

    public TextElement(String label, String fieldName, boolean primary) {
        this(label, fieldName, primary,false);
    }

    public TextElement(String label, String fieldName, boolean primary,boolean isTextArea) {
        super(label, fieldName, primary);
        this.isTextArea = isTextArea;
        init();
    }
    @Override
    public Control control() {
        if (isTextArea) {
            if (textArea == null) {
                textArea = new TextArea();
                textArea.setWrapText(false);
            }
            return textArea;
        } else {
            if (textField == null) {
                textField = new TextField();
            }
            return textField;
        }
    }

}