package cn.lifay.ui.form;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.util.function.Predicate;

/**
 *@ClassName FormElement
 *@Description TODO
 *@Author lifay
 *@Date 2023/1/8 10:09
 **/
public abstract class FormElement<T, R> extends HBox {


    private Control control;

    private String fieldName;
    private boolean primary;

    private Predicate<R> predicate;

    private FormElement() {
        super();
    }

    protected FormElement(String label, String fieldName) {
        this(label, fieldName, false);
    }

    protected FormElement(String label, String fieldName, boolean primary) {
        this.setAlignment(Pos.CENTER_LEFT);
        Label l = new Label(label);
        l.setPadding(new Insets(5, 10, 5, 10));
        getChildren().add(l);
        this.setPadding(new Insets(5, 10, 5, 10));
        this.fieldName = fieldName;
        this.primary = primary;
    }

    public void init() {
        this.control = control();
        getChildren().add(control);
    }

    public boolean isPrimary() {
        return primary;
    }

    public abstract Control control();

    public abstract R getValue();


    public abstract void setValue(Object r);

    protected boolean verify(){
        if (predicate == null) {
            return true;
        }
        return predicate.test(getValue());
    }

/*
    public static <T, R> FormElement<T, R> text(String label,String fieldName) {
        FormElement<T, R> element = new FormElement<>(label, fieldName) {
        };
        return element;
    }
*/

  /*  private Class<? extends Type> getType() {
        TypeVariable<? extends Class<? extends FormElement>>[] typeParameters = this.getClass().getTypeParameters();
        Type type = this.getClass().getGenericSuperclass();
        if (type.getTypeName().equals("javafx.scene.layout.HBox")){
            return null;
        }
        return type.getClass();
    }
*/

    public String getFieldName() {
        return fieldName;
    }
    public void disable(){
        Platform.runLater(() -> {control.setDisable(true);});
    }
    public void clear(){
        Platform.runLater(() -> {
            if (control instanceof TextField) {
                ((TextField) control).clear();
                if (primary){
                    control.setDisable(false);
                    //System.out.println(control.isDisable());
                }
            }else if (control instanceof ChoiceBox) {
                ((ChoiceBox) control).setValue(null);
            }
        });
    }
}
