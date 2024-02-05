package cn.lifay.test

import cn.lifay.db.UserData
import cn.lifay.extension.*
import cn.lifay.mq.EventBus
import cn.lifay.mq.event.TextEvent
import cn.lifay.ui.BaseView
import cn.lifay.ui.table.TableEditCell
import cn.lifay.ui.tree.*
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.control.cell.CheckBoxTreeCell
import javafx.scene.control.cell.ProgressBarTableCell
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.control.cell.TextFieldTableCell
import javafx.scene.input.MouseButton
import javafx.scene.layout.AnchorPane
import javafx.scene.paint.Color
import org.kordamp.ikonli.feather.Feather
import org.kordamp.ikonli.javafx.FontIcon
import java.net.URL
import java.util.*

/**
 * @ClassName DemoView
 * @Description TODO
 * @Author 李方宇
 * @Date 2023/1/9 16:14
 */
class CommonDemoView : BaseView<AnchorPane>() {

    @FXML
    var splitMenuBtn = SplitMenuButton()

    @FXML
    var keywordText = TextField()

    @FXML
    var rootPane = AnchorPane()

    @FXML
    var treeView = TreeView<TreeListVO>()

    @FXML
    var checkTreeView = TreeView<TreeTreeVO>()
//    val rootItemProperties = SimpleObjectProperty<TreeItem<TreeTestVO>>()
    //  val testItemProperties = SimpleObjectProperty<TreeItem<TreeTestVO>>()

    lateinit var rootTreeItem: TreeItem<TreeListVO>
    lateinit var rootCheckTreeItem: CheckBoxTreeItem<TreeTreeVO>

    @FXML
    var tableView = TableView<TableTestVO>()

    @FXML
    var sendText = TextArea()

    @FXML
    lateinit var sendBtn: Button

    @FXML
    var user1 = TextArea()

    @FXML
    var user2 = TextArea()

    @FXML
    lateinit var copyBtn: Button

    @FXML
    lateinit var testTbBtn: Button

    override fun rootPane(): AnchorPane {
        return rootPane
    }

    override fun initialize(p0: URL?, p1: ResourceBundle?) {
        val observableArrayList = FXCollections.observableArrayList<TreeListVO>()
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
        val treeItem1 = TreeItem(TreeListVO("3", "2", "4", SimpleStringProperty("3")))
        val treeItem2 = TreeItem(TreeListVO("2", "1", "2", SimpleStringProperty("2")))
        val treeItem3 = TreeItem(TreeListVO("4", "1", "4", SimpleStringProperty("4")))
        treeItem2.children.add(treeItem3)

        val test1 = TreeListVO("3", "2", "4", SimpleStringProperty("3"))
        val test2 = TreeListVO("2", "1", "2", SimpleStringProperty("2"))
        val test3 = TreeListVO("4", "1", "4", SimpleStringProperty("4"))


        rootTreeItem = TreeItem<TreeListVO>().apply {
            value = TreeListVO("1", "", "4", SimpleStringProperty("1"))
            this.isExpanded = true
            addEventHandler(
                TreeItem.valueChangedEvent(),
                EventHandler<TreeItem.TreeModificationEvent<TreeListVO>> {
                    println(it.newValue)
                }
            )


        }

        // rootItemProperties.value = root
        // testItemProperties.value = treeItem2
        treeView.apply {
            root = rootTreeItem
            isShowRoot = true
            Register(TreeListVO::id, TreeListVO::parentId, true) {
                listOf(test1, test2, test3)
            }
            setOnMouseClicked {
                if (it.button == MouseButton.SECONDARY) {
                    //右键
                    contextMenu = ContextMenu().apply {
                        items.add(MenuItem("根节点下添加").apply {
                            setOnAction {
                                val selectedItem = treeView.selectionModel.selectedItem
                                selectedItem.AddChildren(
                                    TreeListVO(
                                        "根节点下节点1",
                                        "6",
                                        "根节点下节点1",
                                        SimpleStringProperty("根节点下节点1")
                                    )
                                )
                                selectedItem.AddChildrenList(
                                    listOf(
                                        TreeListVO(
                                            "根节点下节点2",
                                            "6",
                                            "根节点下节点2",
                                            SimpleStringProperty("根节点下节点2")
                                        )
                                    )
                                )
                            }
                        })
                    }
                }

            }
        }

        rootCheckTreeItem = CheckBoxTreeItem<TreeTreeVO>().apply {
            value = TreeTreeVO("0", "", "0", null)
            this.isExpanded = true
            addEventHandler(
                TreeItem.valueChangedEvent(),
                EventHandler<TreeItem.TreeModificationEvent<TreeTreeVO>> {
                    println(it.newValue)
                }
            )


        }

        checkTreeView.apply {
            root = rootCheckTreeItem
            isShowRoot = true
            cellFactory = CheckBoxTreeCell.forTreeView()
            Register(TreeTreeVO::id, TreeTreeVO::children, true, true) {
                listOf(
                    TreeTreeVO(
                        "1", "0", "1", arrayListOf(
                            TreeTreeVO(
                                "2", "1", "2", arrayListOf(
                                    TreeTreeVO("4", "2", "4", null)
                                )
                            ),
                            TreeTreeVO("3", "1", "3", null)
                        )
                    )
                )
            }

        }

        keywordText.textProperty().addListener { observableValue, s, s2 ->
            if (s2.isNullOrBlank()) {
                return@addListener
            }
            treeView.RefreshTree<TreeListVO, String>(filterFunc = {
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

        testTbBtn.graphic = FontIcon("mdal-adb")
            .customStyle(16, Color.RED)
        copyBtn.graphic = FontIcon(Feather.COPY)
            .customStyle(18, Color.LIGHTSKYBLUE)
        sendBtn.graphic = FontIcon().apply {
            style =
                "-fx-font-family: 'Material Icons';-fx-icon-code: mdal-5g;-fx-icon-size: 16px;-fx-icon-color: #0014ea;"
        }

        EventBus.subscribe(DemoId.CHAT, TextEvent::class) {
            platformRun {
                user1.appendText("${it.text}\n")
            }
        }
        EventBus.subscribe(DemoId.CHAT, TextEvent::class) {
            platformRun {
                user2.appendText("${it.text}\n")
            }
        }
    }

    fun treeTest(actionEvent: ActionEvent) {
        println("测试树")
        rootTreeItem.value.name = "测试修改"
        println(rootTreeItem.children[0].treeViewId)
        rootTreeItem.children[0].value = TreeListVO("5", "1", "5", SimpleStringProperty("5"))
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
        val userForm = UserDataForm(UserData(1, "111111", SelectTypeEnum.C, true, "男"))
        userForm.show()
    }

    fun info(actionEvent: ActionEvent?) {
        alertInfo("信息打印")
    }

    fun warn(actionEvent: ActionEvent?) {
        alertWarn("警告打印")
    }

    fun detail(actionEvent: ActionEvent?) {
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

    fun error(actionEvent: ActionEvent?) {
        alertError(
            "错误打印", "头部信息", "异常详细信息fun tableText(actionEvent: ActionEvent) {\n" +
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
                    "        rootTreeItem.children[0].DeleteThis()"
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
        rootTreeItem.children[0].AddChildren(TreeListVO("add1", "5", "add1", SimpleStringProperty("add1")))
    }

    fun treeTestAdd2(actionEvent: ActionEvent) {
        rootTreeItem.children[0].AddChildrenList(listOf(TreeListVO("add2", "6", "add2", SimpleStringProperty("add2"))))
    }


    fun treeTestUpt(actionEvent: ActionEvent) {
        rootTreeItem.children[0].UpdateItem(TreeListVO("修改测试", "5", "修改测试", SimpleStringProperty("修改测试")))
        val treeItem = treeView.GetItemByBusiId("add1")
        treeItem?.UpdateItem(TreeListVO("修改测试222", "5", "修改测试222", SimpleStringProperty("修改测试22")))
        println(treeItem?.value)
    }

    fun treeTestDel1(actionEvent: ActionEvent) {
        rootTreeItem.children[0].DeleteThis()
    }

    fun treeTestDel2(actionEvent: ActionEvent) {
        rootTreeItem.DeleteChildItem { it.id == "4" }

    }

    fun chat(actionEvent: ActionEvent) {
        EventBus.publish(TextEvent(DemoId.CHAT, sendText.text))
    }

    fun taskAction() {
        //  showNotification("111")
        asyncTaskLoading(rootPane.scene.window, "出错", true, success = {
            println("ddddddddd")
            showNotification("222")
        }) {
            //  showNotification("333")
            Thread.sleep(3333)
        }
    }

    fun copyText(actionEvent: ActionEvent) {
        copyToClipboard("测试复制的文字")
    }

    fun curdTest(actionEvent: ActionEvent) {
        val userManage = UserManage()
        userManage.show()
    }


}