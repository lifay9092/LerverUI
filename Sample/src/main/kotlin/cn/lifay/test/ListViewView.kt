package cn.lifay.test

import javafx.collections.FXCollections
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.ListView
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.layout.AnchorPane
import java.net.URL
import java.util.*

/**
 * ListViewView TODO
 * @author lifay
 * @date 2023/8/31 9:14
 */
class ListViewView : Initializable {
    @FXML
    val rootPane: AnchorPane? = null

    @FXML
    var dataList: ListView<String>? = null

    @FXML
    lateinit var tableList: TableView<Person>
    override fun initialize(url: URL?, resourceBundle: ResourceBundle?) {
//        dataList = new ListView<>(FXCollections.observableArrayList("1","2","3"));
        dataList!!.items.addAll(FXCollections.observableArrayList("1", "2", "3"))

        tableList!!.columns.addAll(
            TableColumn<Person, Int>("ID").apply {
                this.cellValueFactory = PropertyValueFactory("id")
                prefWidth = 200.0
            },
            TableColumn<Person, String>("名称").apply {
                this.cellValueFactory = PropertyValueFactory("name")
                prefWidth = 200.0
            }
        )
        tableList!!.items.addAll(
            Person(1, "1", false),
            Person(2, "2", true),
            Person(3, "3", true),
        )
    }

    fun addTest(actionEvent: ActionEvent?) {
        tableList.items.addAll(
            Person(4, "4", false),
            Person(5, "5", true),
        )
    }

    fun uptTest(actionEvent: ActionEvent?) {
        tableList.items[1] = Person(4, UUID.randomUUID().toString(), false)
    }

    fun delTest(actionEvent: ActionEvent?) {
        tableList.items.remove(Person(4, "4", false))
        tableList.items.removeFirst()

    }
}