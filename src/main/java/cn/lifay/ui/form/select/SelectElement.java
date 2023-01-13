package cn.lifay.ui.form.select;

import cn.lifay.ui.form.FormElement;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Control;

import java.util.List;

/**
 *@ClassName SelectElement
 *@Description 选择下拉框 T-表单实体 R-ChoiceBox实体 注意：具体显示名称由R的toString()决定
 *@Author 李方宇
 *@Date 2023/1/11 15:02
 **/
public abstract class SelectElement<T,R> extends FormElement<T, R> {

    protected ChoiceBox<R> control = new ChoiceBox<>();
    protected SelectElement(String label, String fieldName) {
        super(label, fieldName);
        init();
    }
    protected SelectElement(String label, String fieldName,List<R> items) {
        super(label, fieldName);
        init();
        control.getItems().addAll(items);
    }

    @Override
    public Control control() {
        return control;
    }

    @Override
    public R getValue() {
        return control.getValue();
    }

    @Override
    public void setValue(Object r) {
        control.setValue((R) r);
    }
    public void loadItems(List<R> items){
        control.getItems().clear();
        control.getItems().addAll(items);
    }
}