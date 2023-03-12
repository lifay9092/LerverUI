package cn.lifay.test;

import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @ClassName BindView
 * @Description TODO
 * @Author lifay
 * @Date 2023/3/11 20:48
 **/
public class BindView implements Initializable {

    @FXML
    private TextField name;

    private SimpleObjectProperty<Person> person = new SimpleObjectProperty<>(new Person(111, "111", false));
    private SimpleStringProperty n = new SimpleStringProperty();
    private StringBinding stringBinding;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        /* stringBinding = Bindings.createStringBinding(() -> {
             person.setName(name.textProperty().get());
             return person.getName();
         }, name.textProperty());*/
        name.textProperty().bindBidirectional(n);
    }

    public void test(ActionEvent actionEvent) {
//        name.setText("222222222222");
        //   person.setName("33333333333");
        System.out.println(n.getValue());
//        System.out.println(stringBinding.get());
    }

}
