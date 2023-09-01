package cn.lifay.test;

import cn.lifay.ui.BaseView;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * ListViewView TODO
 * @author lifay
 * @date 2023/8/31 9:14
 **/
public class ListViewView implements Initializable {

    @FXML
    private AnchorPane rootPane;

    @FXML
    private ListView<String> dataList;

    @FXML
    private TableView<String> tableList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        dataList = new ListView<>(FXCollections.observableArrayList("1","2","3"));
        dataList.getItems().addAll(FXCollections.observableArrayList("1", "2", "3"));
        tableList = new TableView<>();
        tableList.getColumns().add(new TableColumn<String,String>("1111"));
        tableList.getItems().addAll(FXCollections.observableArrayList("1", "2", "3"));
    }


}