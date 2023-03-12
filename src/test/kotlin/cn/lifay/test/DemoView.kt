package cn.lifay.test

import cn.lifay.ui.table.TableEditCell
import cn.lifay.ui.tree.RegisterByList
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.scene.control.cell.ProgressBarTableCell
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.control.cell.TextFieldTableCell
import javafx.scene.layout.AnchorPane
import java.net.URL
import java.util.*

/**
 * @ClassName DemoView
 * @Description TODO
 * @Author 李方宇
 * @Date 2023/1/9 16:14
 */
class DemoView : Initializable {
    @FXML
    lateinit var rootPane: AnchorPane

    @FXML
    var treeView = TreeView<TreeTestVO>()
//    val rootItemProperties = SimpleObjectProperty<TreeItem<TreeTestVO>>()
    //  val testItemProperties = SimpleObjectProperty<TreeItem<TreeTestVO>>()

    lateinit var rootTreeItem: TreeItem<TreeTestVO>

    @FXML
    var tableView = TableView<TableTestVO>()

    override fun initialize(p0: URL?, p1: ResourceBundle?) {
        val observableArrayList = FXCollections.observableArrayList<TreeTestVO>()


        val treeItem1 = TreeItem(TreeTestVO("3", "2", "4", SimpleStringProperty("3")))
        val treeItem2 = TreeItem(TreeTestVO("2", "1", "2", SimpleStringProperty("2")))
        val treeItem3 = TreeItem(TreeTestVO("4", "1", "4", SimpleStringProperty("4")))
        treeItem2.children.add(treeItem3)

        val test1 = TreeTestVO("3", "2", "4", SimpleStringProperty("3"))
        val test2 = TreeTestVO("2", "1", "2", SimpleStringProperty("2"))
        val test3 = TreeTestVO("4", "1", "4", SimpleStringProperty("4"))


        rootTreeItem = TreeItem<TreeTestVO>().apply {
            value = TreeTestVO("1", "", "4", SimpleStringProperty("1"))
            this.isExpanded = true
            addEventHandler(
                TreeItem.valueChangedEvent(),
                EventHandler<TreeItem.TreeModificationEvent<TreeTestVO>> {
                    println(it.newValue)
                }
            )

        }

        // rootItemProperties.value = root
        // testItemProperties.value = treeItem2
        treeView.apply {
            root = rootTreeItem
            isShowRoot = true
            RegisterByList(TreeTestVO::id, TreeTestVO::parentId, listOf(test1, test2, test3))
//            rootProperty().bind(rootItemProperties)
        }
        tableView.apply {
            isEditable = true
            columns.addAll(
                TableColumn<TableTestVO, String>("普通").apply {
                    this.cellValueFactory = PropertyValueFactory("text")
                    setCellFactory {
                        object : TableEditCell<TableTestVO, String>() {}
                    }
                    setOnEditCommit { t: TableColumn.CellEditEvent<TableTestVO, String> ->
                        (t.tableView.items[t.tablePosition.row] as TableTestVO).text = t.newValue
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

    fun treeTest(actionEvent: ActionEvent) {
        println("测试树")
        rootTreeItem.value.name = "测试修改"
        rootTreeItem.children[0].value = TreeTestVO("5", "1", "5", SimpleStringProperty("5"))
        // rootItemProperties.value.apply {
        //      value.name = "测试"
        //      children[0].value.name = "修改了"
//            children.add(
//                TreeItem(TreeTestVO("5", "1","5", SimpleStringProperty("5")))
//            )
        //  }
        // testItemProperties.value.apply {
        //      value.name = "fffffffffff"
        //    }
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