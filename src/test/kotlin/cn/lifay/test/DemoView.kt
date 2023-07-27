package cn.lifay.test

import cn.lifay.db.DbManage
import cn.lifay.extension.*
import cn.lifay.ui.table.TableEditCell
import cn.lifay.ui.tree.*
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.*
import javafx.scene.control.cell.ProgressBarTableCell
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.control.cell.TextFieldTableCell
import javafx.scene.input.MouseButton
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
    var splitMenuBtn = SplitMenuButton()

    @FXML
    var keywordText = TextField()

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
        splitMenuBtn.items.addAll(
            MenuItem("1111").apply {
                setOnAction {
                println("11111")
            }
            },
            MenuItem("2222").apply {
                setOnAction {
                    println("22222")
                }
            }
        )
        splitMenuBtn.text = "ddddddddd"
        splitMenuBtn.setOnAction {
            println("点击了")
        }
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
            Register(TreeTestVO::id, TreeTestVO::parentId,true){
                listOf(test1, test2, test3)
            }
            setOnMouseClicked {
                if (it.button == MouseButton.SECONDARY) {
                    //右键
                    contextMenu = ContextMenu().apply {
                        items.add(MenuItem("根节点下添加").apply {
                            setOnAction {
                                val selectedItem = treeView.selectionModel.selectedItem
                                selectedItem.AddChildren(TreeTestVO("根节点下节点1", "6", "根节点下节点1", SimpleStringProperty("根节点下节点1")))
                                selectedItem.AddChildrenList(listOf(TreeTestVO("根节点下节点2", "6", "根节点下节点2", SimpleStringProperty("根节点下节点2"))))
                            }
                        })
                    }
                }

            }
//            rootProperty().bind(rootItemProperties)
        }
        keywordText.textProperty().addListener { observableValue, s, s2 ->
            if (s2.isNullOrBlank()) {
                return@addListener
            }
            treeView.RefreshTree<TreeTestVO, String>(filterFunc = {
                it.name.contains(s2)
            })
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
        println(rootTreeItem.children[0].treeViewId)
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
        alertInfo("信息打印")
    }

    fun warn(actionEvent: ActionEvent?) {
        alertWarn("警告打印")
    }

    fun error(actionEvent: ActionEvent?) {
        alertDetail(
            "错误打印", "异常详细信息fun tableText(actionEvent: ActionEvent) {\n" +
                    "        tableView.items[0].text = \"33333\"\n" +
                    "        tableView.items[0].info = \"777777777\"\n" +
                    "        tableView.items[0].msg.value = \"33333\"\n" +
                    "        tableView.items[0].processBar.value = 0.5\n" +
                    "        tableView.items[1].msg.value = \"4444444444\"\n" +
                    "    }\n" +
                    "\n" +
                    "    fun treeTestAdd1(actionEvent: ActionEvent) {\n" +
                    "        rootTreeItem.children[0].AddChildren(TreeTestVO(\"add1\", \"5\", \"add1\", SimpleStringProperty(\"add1\")))\n" +
                    "    }\n" +
                    "\n" +
                    "    fun treeTestAdd2(actionEvent: ActionEvent) {\n" +
                    "        rootTreeItem.children[0].AddChildrenList(listOf(TreeTestVO(\"add2\", \"6\", \"add2\", SimpleStringProperty(\"add2\"))))\n" +
                    "    }\n" +
                    "\n" +
                    "    fun treeTestUpt(actionEvent: ActionEvent) {\n" +
                    "        rootTreeItem.children[0].UpdateItem(TreeTestVO(\"修改测试\", \"5\", \"修改测试\", SimpleStringProperty(\"修改测试\")))\n" +
                    "    }\n" +
                    "\n" +
                    "    fun treeTestDel1(actionEvent: ActionEvent) {\n" +
                    "        rootTreeItem.children[0].DeleteThis()", Alert.AlertType.ERROR, isExpanded = true
        )
    }

    fun tableText(actionEvent: ActionEvent) {
        tableView.items[0].text = "33333"
        tableView.items[0].info = "777777777"
        tableView.items[0].msg.value = "33333"
        tableView.items[0].processBar.value = 0.5
        tableView.items[1].msg.value = "4444444444"
    }

    fun treeTestAdd1(actionEvent: ActionEvent) {
        rootTreeItem.children[0].AddChildren(TreeTestVO("add1", "5", "add1", SimpleStringProperty("add1")))
    }

    fun treeTestAdd2(actionEvent: ActionEvent) {
        rootTreeItem.children[0].AddChildrenList(listOf(TreeTestVO("add2", "6", "add2", SimpleStringProperty("add2"))))
    }


    fun treeTestUpt(actionEvent: ActionEvent) {
        rootTreeItem.children[0].UpdateItem(TreeTestVO("修改测试", "5", "修改测试", SimpleStringProperty("修改测试")))
    }

    fun treeTestDel1(actionEvent: ActionEvent) {
        rootTreeItem.children[0].DeleteThis()
    }

    fun treeTestDel2(actionEvent: ActionEvent) {
        rootTreeItem.DeleteChildItem { it.id == "4" }

    }


}