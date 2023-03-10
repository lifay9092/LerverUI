package cn.lifay.test

import cn.lifay.ui.table.TableCell2
import cn.lifay.ui.table.TableEditCell
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleStringProperty
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.TableCell
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.ProgressBarTableCell
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.control.cell.TextFieldTableCell
import javafx.scene.layout.AnchorPane
import java.net.URL
import java.util.*
import java.util.concurrent.Callable

/**
 * @ClassName DemoView
 * @Description TODO
 * @Author 李方宇
 * @Date 2023/1/9 16:14
 */
class DemoView : Initializable {
    @FXML
    var tableView = TableView<TableTestVO>()

    @FXML
    lateinit var rootPane: AnchorPane
    override fun initialize(p0: URL?, p1: ResourceBundle?) {
        tableView.apply {
            isEditable = true
            columns.addAll(
                TableColumn<TableTestVO, String>("普通").apply {
                    this.cellValueFactory = PropertyValueFactory("text")
                    setCellFactory { v ->
                        val tableCell = object : TableEditCell<TableTestVO, String>() {
                        }
                        tableCell
                    }
                    prefWidth = 150.0
                },
                TableColumn<TableTestVO, String>("自定义Property").apply {
                    this.setCellValueFactory { p: TableColumn.CellDataFeatures<TableTestVO, String> ->
                        SimpleStringProperty(p.value.info)
                    }
                },
                TableColumn<TableTestVO, String>("字段Property").apply {
                    this.setCellValueFactory { p: TableColumn.CellDataFeatures<TableTestVO, String> ->
                        p.value.msg
                    }
                    isEditable = true
                    cellFactory = TextFieldTableCell.forTableColumn()
                    prefWidth = 150.0
                },
                TableColumn<TableTestVO, Double>("进度条").apply {
                    this.setCellValueFactory { p: TableColumn.CellDataFeatures<TableTestVO, Double> ->
                        Bindings.createObjectBinding({ p.value.processBar.value }, p.value.processBar)

                    }
                    this.cellFactory = ProgressBarTableCell.forTableColumn()
                }
            )

            items.apply {
                clear()
                addAll(
                    TableTestVO("111", "1111", SimpleStringProperty("111"), SimpleDoubleProperty(0.0)),
                    TableTestVO("222", "2222", SimpleStringProperty("222"), SimpleDoubleProperty(0.0))
                )
            }
        }
    }

    fun formTest(actionEvent: ActionEvent?) {
        // UserForm userForm = new UserForm("测试",User.class);
//        UserNewForm userForm = new UserNewForm("测试", new UserData(1, "111111"));
        val userForm = UserForm(UserData(1, "111111", SelectTypeEnum.C, true, "男"))
        userForm.show()
    }

    fun info(actionEvent: ActionEvent?) {

    }

    fun warn(actionEvent: ActionEvent?) {
    }

    fun error(actionEvent: ActionEvent?) {
    }

    fun tableText(actionEvent: ActionEvent) {
        tableView.items[0].text = "33333"
        tableView.items[0].info = "777777777"
        tableView.items[0].msg.value = "33333"
        tableView.items[0].processBar.value = 0.5
        tableView.items[1].msg.value = "4444444444"
    }


}