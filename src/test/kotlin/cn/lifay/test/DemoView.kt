package cn.lifay.test

import cn.lifay.ui.message.ErrorMessage
import cn.lifay.ui.message.InfoMessage
import cn.lifay.ui.message.WarnMessage
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleStringProperty
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.ProgressBarTableCell
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.control.cell.TextFieldTableCell
import javafx.scene.layout.AnchorPane
import javafx.util.Callback
import java.net.URL
import java.util.*
import java.util.Collections.addAll
import java.util.concurrent.Callable

/**
 * @ClassName DemoView
 * @Description TODO
 * @Author 李方宇
 * @Date 2023/1/9 16:14
 */
class DemoView : Initializable{
    @FXML
    var tableView =  TableView<TableTestVO>()

    @FXML
    lateinit var rootPane : AnchorPane
    override fun initialize(p0: URL?, p1: ResourceBundle?)
    {
        tableView.apply {
            columns.addAll(
                TableColumn<TableTestVO,String>("文本").apply {
                    this.cellValueFactory = PropertyValueFactory("text")
                },
                TableColumn<TableTestVO,String>("文本2").apply {
                    this.setCellValueFactory { p: TableColumn.CellDataFeatures<TableTestVO, String> ->
                        SimpleStringProperty(p.value.info)
                    }
                },
                TableColumn<TableTestVO,String>("消息").apply {
                    this.setCellValueFactory { p: TableColumn.CellDataFeatures<TableTestVO, String> ->
                        p.value.msg
                    }
                }
                ,
                TableColumn<TableTestVO,Double>("进度条").apply {
                    this.setCellValueFactory { p: TableColumn.CellDataFeatures<TableTestVO, Double> ->
                        Bindings.createObjectBinding(object : Callable<Double>{
                            override fun call(): Double {
                                return p.value.processBar.value
                            }
                        },p.value.processBar)

                    }
                    this.cellFactory = ProgressBarTableCell.forTableColumn()
                }
            )

            items.apply {
                clear()
                addAll(
                    TableTestVO("111","1111", SimpleStringProperty("111"), SimpleDoubleProperty(0.0)),
                    TableTestVO("222","2222", SimpleStringProperty("222"), SimpleDoubleProperty(0.0))
                )
            }
        }
    }
    fun formTest(actionEvent: ActionEvent?) {
        // UserForm userForm = new UserForm("测试",User.class);
//        UserNewForm userForm = new UserNewForm("测试", new UserData(1, "111111"));
        val userForm = UserForm("测试", UserData(1, "111111", SelectTypeEnum.C, true, "男"))
        userForm.show()
    }

    fun info(actionEvent: ActionEvent?) {
        val infoMessage = InfoMessage(rootPane.scene.window)
        infoMessage.show("信息。。。。。。。")
    }

    fun warn(actionEvent: ActionEvent?) {
        val warnMessage = WarnMessage(rootPane.scene.window)
        warnMessage.show("警告。。。。。大萨达多撒多撒多撒大所多所大多大大声的。。")
    }

    fun error(actionEvent: ActionEvent?) {
        val errorMessage = ErrorMessage(rootPane.scene.window)
        errorMessage.show("错误。。。。。。。")
    }

    fun tableText(actionEvent: ActionEvent) {
        tableView.items[0].text = "33333"
        tableView.items[0].info = "777777777"
        tableView.items[0].msg.value = "33333"
        tableView.items[0].processBar.value = 0.5
        tableView.items[1].msg.value = "4444444444"
    }


}