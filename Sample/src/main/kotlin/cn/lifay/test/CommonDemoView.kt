package cn.lifay.test

import atlantafx.base.controls.Spacer
import atlantafx.base.theme.Styles
import cn.lifay.SmartTreeViewDemo
import cn.lifay.db.UserData
import cn.lifay.extension.*
import cn.lifay.mq.EventBus
import cn.lifay.mq.event.TextEvent
import cn.lifay.ui.BaseView
import cn.lifay.ui.DelegateProp
import cn.lifay.ui.table.LerverTableView
import cn.lifay.ui.table.TableEditCell
import cn.lifay.ui.tree.*
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.control.cell.CheckBoxTreeCell
import javafx.scene.control.cell.ProgressBarTableCell
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.control.cell.TextFieldTableCell
import javafx.scene.input.MouseButton
import javafx.scene.layout.AnchorPane
import javafx.scene.paint.Color
import javafx.stage.Stage
import kotlinx.coroutines.delay
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
    var centerTreeView = LerverTreeView<TreeListVO, String>()

    @FXML
    var rightCustomTree = LerverTreeView<Person, Int>()

    @FXML
    var leftCheckTreeView = LerverTreeView<TreeTreeVO, String>()
//    val rootItemProperties = SimpleObjectProperty<TreeItem<TreeTestVO>>()
    //  val testItemProperties = SimpleObjectProperty<TreeItem<TreeTestVO>>()

    lateinit var rootTreeItem: TreeItem<TreeListVO>
    lateinit var rootCheckTreeItem: CheckBoxTreeItem<TreeTreeVO>

    @FXML
    var tableView = LerverTableView<TableTestVO, String>()

    @FXML
    var tableViewJava = LerverTableView<Person, Int>()

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


        rootTreeItem = TreeItem<TreeListVO>().apply {
            value = TreeListVO("1", "", "左上1", SimpleStringProperty("1"))
            this.isExpanded = true
            addEventHandler(
                TreeItem.valueChangedEvent(),
                EventHandler<TreeItem.TreeModificationEvent<TreeListVO>> {
                    println(it.newValue)
                }
            )
        }

        val test1 = TreeListVO("3", "2", "左上3", SimpleStringProperty("左上3"))
        val test2 = TreeListVO("2", "1", "左上2", SimpleStringProperty("左上2"))
        val test3 = TreeListVO("4", "1", "左上4", SimpleStringProperty("左上4"))

        // rootItemProperties.value = root
        // testItemProperties.value = treeItem2

        centerTreeView.apply {
            root = rootTreeItem
            isShowRoot = true
            LoadTreeConfig(
                TreeConfig.IdParentIdConfig<TreeListVO, String>(
                    TreeListVO::id, TreeListVO::parentId
                )
            )
            LoadTree {
                listOf(test1, test2, test3)
            }
            setOnMouseClicked {
                if (it.button == MouseButton.SECONDARY) {
                    //右键
                    contextMenu = ContextMenu().apply {
                        items.add(MenuItem("添加子节点").apply {
                            setOnAction {
                                println("添加子节点")
                                val selectedItem = centerTreeView.selectionModel.selectedItem
                                val id1 = UUID.randomUUID().toString()
                                val id2 = UUID.randomUUID().toString()
                                selectedItem.AddChildren(
                                    TreeListVO(
                                        id1,
                                        selectedItem.value.id,
                                        id1,
                                        SimpleStringProperty(id1)
                                    )
                                )
                                selectedItem.AddChildrenList(
                                    listOf(
                                        TreeListVO(
                                            id2,
                                            selectedItem.value.id,
                                            id2,
                                            SimpleStringProperty(id2)
                                        )
                                    )
                                )
                            }
                        })
                        items.add(MenuItem("更新节点").apply {
                            setOnAction {
                                println("更新节点")
                                val selectedItem = centerTreeView.selectionModel.selectedItem
                                val id = UUID.randomUUID().toString()
                                selectedItem.UpdateItem(
                                    TreeListVO(
                                        id,
                                        selectedItem.value.parentId,
                                        id,
                                        SimpleStringProperty(id)
                                    )
                                )
                            }
                        })

                        items.add(MenuItem("删除节点").apply {
                            this.setOnAction {
                                println("删除节点")
                                val selectedItem = centerTreeView.selectionModel.selectedItem
                                selectedItem.DeleteThis()
                            }
                        })
                        items.add(MenuItem("删除子节点").apply {
                            this.setOnAction {
                                println("删除子节点")
                                val selectedItem = centerTreeView.selectionModel.selectedItem
                                if (selectedItem.children.isNotEmpty()) {
                                    selectedItem.DeleteChildItem { true }
                                }
                            }
                        })

                    }
                } else if (it.clickCount == 2) {
                    val selectedItem = this.selectionModel.selectedItem
                    selectedItem?.let {
                        val value = selectedItem.value

                    }

                }

            }
        }

        keywordText.textProperty().addListener { observableValue, old, new ->
            if (old == new) {
                return@addListener
            }
            centerTreeView.FilterTree {
                new.isBlank() || (new.isNotBlank() && it.name.contains(new))
            }
        }

        /*勾选树*/
        rootCheckTreeItem = CheckBoxTreeItem<TreeTreeVO>().apply {
            value = TreeTreeVO("0", "", "0", false, null)
            this.isExpanded = true
            addEventHandler(
                TreeItem.valueChangedEvent(),
                EventHandler<TreeItem.TreeModificationEvent<TreeTreeVO>> {
                    println(it.newValue)
                }
            )

        }
        leftCheckTreeView.apply {
            root = rootCheckTreeItem
            isShowRoot = true
            cellFactory = CheckBoxTreeCell.forTreeView<TreeTreeVO>()
            setCellFactory {

//                LerverCheckBoxTreeCell()
                object : LerverCheckBoxTreeCell<TreeTreeVO>() {
                    //                    customFuncNodes(it)
//                    listOf(Label("name:${it?.value?.name}").apply {
//                        styleClass.addAll(
//                            Styles.TEXT,
//                            Styles.ACCENT
//                        )
//                    })
                    override fun customNodes(treeItem: TreeItem<TreeTreeVO>): List<Node>? {
                        return customFuncNodes(treeItem)
                    }
                }
            }
//            cellFactory = CheckBoxTreeCell.forTreeView()
            LoadTreeConfig(
                TreeConfig.ChildrenPropertyConfig<TreeTreeVO, String>(
                    TreeTreeVO::id,
                    TreeTreeVO::children,
                    isCheckBox = true
                )
            )
            LoadTree {
                listOf(
                    TreeTreeVO(
                        "1", "0", "1", false, arrayListOf(
                            TreeTreeVO(
                                "2", "1", "2", false, arrayListOf(
                                    TreeTreeVO("4", "2", "4", true, null)
                                )
                            ),
                            TreeTreeVO("3", "1", "3", true, null)
                        )
                    )
                )
            }
        }

        tableView.apply {
            isEditable = true
            Register(TableTestVO::id)
            InitTableColumn(
                true,
                TableColumn<TableTestVO, String>("普通").apply {
                    this.cellValueFactory = PropertyValueFactory("id")
                    setCellFactory {
                        object : TableEditCell<TableTestVO, String>() {}
                    }
                    setOnEditCommit { t: TableColumn.CellEditEvent<TableTestVO, String> ->
                        (t.tableView.items[t.tablePosition.row] as TableTestVO).id = t.newValue
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

        tableViewJava.apply {
            isEditable = false
            RegisterByFunction(Person::id)
            columns.addAll(
                TableColumn<Person, Int>("ID").apply {
                    this.cellValueFactory = PropertyValueFactory("id")
                    prefWidth = 150.0
                },
                TableColumn<Person, String>("名称").apply {
                    this.cellValueFactory = PropertyValueFactory("name")
                    prefWidth = 150.0
                }
            )

            items.apply {
                clear()
                addAll(
                    Person(111, "111", false),
                    Person(222, "222", true)
                )
            }
        }

        val customTreeItem = CheckBoxTreeItem(Person(0, "根节点", false)).apply {
            selectedProperty().addListener { observableValue, old, new ->
                println("old:$old new:$new")
            }
        }
        customTreeItem.children.addAll(
            TreeItem(Person(1, "1", false)),
            TreeItem(Person(2, "2", true)),
        )
        rightCustomTree.apply {
            root = customTreeItem
//            setCellFactory {
//                LerverCheckBoxTreeCell()
//            }
            cellFactory = CheckBoxTreeCell.forTreeView()

        }

        /*测试*/
//        list.addListener { observableValue, old, new ->
//            println("old:${old} new:${new}")
//            observableValue.
//        }
        // 添加一个ListChangeListener来监听列表的变化,变化时取出所有变更值，计算父item_id(基于属性引用)，根据索引（item_id）拿到TreeItem进行相应的变更
        // 树型(children)列表兼容：
        list.addListener { c: ListChangeListener.Change<out Person?> ->
            while (c.next()) {
                if (c.wasPermutated()) {
                    println("列表元素顺序已更改")
                } else if (c.wasUpdated()) {
                    println("列表元素已更新")
                } else if (c.wasAdded()) {
                    println("在索引 " + c.from + " 插入了元素: " + c.list[c.from])
                } else if (c.wasRemoved()) {
                    println("在索引 " + c.from + " 删除了元素: " + c.removed)
                } else if (c.wasReplaced()) {
                    println("在索引 " + c.from + " wasReplaced了元素: ")
                }
                println(c.list)
            }
        }
        list.addAll(
            Person(1, "1", false),
            Person(2, "2", false),
            Person(3, "3", false)
        )

        testTbBtn.graphic = FontIcon("mdal-adb")
            .customStyle(16, Color.RED)
        copyBtn.graphic = FontIcon(Feather.COPY)
            .customStyle(18, Color.LIGHTSKYBLUE)
        sendBtn.graphic = FontIcon().apply {
            style =
                "-fx-font-family: 'Material Icons';-fx-icon-code: mdal-5g;-fx-icon-size: 16px;-fx-icon-color: #0014ea;"
        }

        EventBus.subscribe<TextEvent>(DemoId.CHAT) {
            platformRun {
                user1.appendText("${it.text}\n")
            }
            platformRun {
                user2.appendText("${it.text}\n")
            }
        }
    }

    private fun customFuncNodes(treeItem: TreeItem<TreeTreeVO>?): List<Node> {
        if (treeItem == null) {
            return emptyList()
        }
        val treeNode = treeItem.value
        val nodes = ArrayList<Node>()
        nodes.add(
            Label(treeNode.name).apply {
                this.alignment = Pos.CENTER_LEFT
            })
        nodes.add(Spacer())


        nodes.add(Label("taskStatus").apply {
            styleClass.addAll(
                Styles.TEXT,
                Styles.ACCENT
            )
        })
        return nodes
    }
    val list = SimpleListProperty(FXCollections.observableArrayList<Person>())

    fun treeTest(actionEvent: ActionEvent) {


        println("测试树")
        rootTreeItem.value.name = "测试修改"
        println(rootTreeItem.children[0].treeViewId)
        rootTreeItem.children[0].value = TreeListVO("5", "1", "5", SimpleStringProperty("5"))

        val selects = findMatchingTreeItemChildrenByTreeItem(leftCheckTreeView.root) {
            val checkBoxTreeItem = it as CheckBoxTreeItem<TreeTreeVO>
            checkBoxTreeItem.isSelected
        }
        println("selects:${selects.size}")
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

    fun tableTestAdd(actionEvent: ActionEvent) {
        val id = UUID.randomUUID().toString()
        tableView.items.add(
            TableTestVO(id, id, SimpleStringProperty(id), SimpleDoubleProperty(0.3))
        )

    }

    fun tableTestDel(actionEvent: ActionEvent) {
        tableView.items.removeIf {
            it.id == "111"
        }

    }

    fun tableTestClear(actionEvent: ActionEvent) {
        tableView.items.clear()
    }

    fun tableTestUpt(actionEvent: ActionEvent) {
        tableView.UpdateItem(
            TableTestVO("111", "33333", SimpleStringProperty("33333"), SimpleDoubleProperty(0.1))
        )
        tableView.UpdateItem("222", TableTestVO::msg, "444444444")
        tableView.UpdateItem("222", TableTestVO::processBar, 0.5)

    }

    fun tableTestUptJava(actionEvent: ActionEvent) {
        tableViewJava.UpdateItem(
            Person(111, "333", false)
        )
        tableViewJava.UpdateItem(222, DelegateProp("name"), "444444444")

    }

    fun treeTestAdd1(actionEvent: ActionEvent) {
        rootTreeItem.children[0].AddChildren(TreeListVO("add1", "5", "add1", SimpleStringProperty("add1")))
        rootCheckTreeItem.children[0].children[1].children.add(CheckBoxTreeItem(TreeTreeVO("8", "3", "8", true, null)))
    }

    fun treeTestAdd2(actionEvent: ActionEvent) {
        rootTreeItem.children[0].AddChildrenList(listOf(TreeListVO("add2", "6", "add2", SimpleStringProperty("add2"))))
    }


    fun treeTestUpt(actionEvent: ActionEvent) {

        rootTreeItem.children[0].UpdateItem(TreeListVO("修改测试", "5", "修改测试", SimpleStringProperty("修改测试")))
        val treeItem = centerTreeView.GetItemByBusiId("add1")
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
        EventBus.publish(TextEvent(DemoId.CHAT.toString(), sendText.text))
    }

    fun asyncTest() {
        //  showNotification("111")
        asyncDefault {
            println("asyncIO 111...")
            delay(1111)
            asyncUI {
                println("do ui opt...")
            }
            println("asyncIO 222...")
            delay(2222)
            println("asyncIO 333...")
        }
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

    fun customTreeTestAdd(actionEvent: ActionEvent) {
//        list.add(Person(5,"5",false))
        val id = UUID.randomUUID().toString()
        val datas = ArrayList<Person>()
        for (i in 1..100) {

            datas.add(Person(100 + i, "${id}_$i", false))
        }
        list.addAll(
            datas
        )
    }

    fun customTreeTestUpt(actionEvent: ActionEvent) {
        list[0].name = "6"

    }

    fun customTreeTestDel(actionEvent: ActionEvent) {
        list.removeIf { it.id == 5 }

    }

    fun customTreeTestReplace(actionEvent: ActionEvent) {
        list[1] = Person(7, "7", false)

    }

    fun clearTreeCache(actionEvent: ActionEvent) {

    }

    fun OpenFileDialogTest(actionEvent: ActionEvent) {
        OpenFileDialog(rootPane.scene.window, "选择文件") {
            it?.let {
                showMessage("选择了:${it.absolutePath}")
            }
        }
    }

    fun OpenMultipleFileDialogTest(actionEvent: ActionEvent) {
        OpenMultipleFileDialog(rootPane.scene.window, "选择多个文件") {
            it?.let {
                showMessage("选择了:${it.map { it.absolutePath }.joinToString("\n")}")
            }
        }
    }

    fun OpenDirectoryDialogTest(actionEvent: ActionEvent) {
        OpenDirectoryDialog(rootPane.scene.window, "选择目录") {
            showMessage("选择了:${it?.absolutePath}")
        }
    }

    fun SaveFileDialogTest(actionEvent: ActionEvent) {
        SaveFileDialog(rootPane.scene.window, "保存文件") {
            it?.let {
                showMessage("保存到:${it.absolutePath}")
            }
        }
    }

    fun treeViewDemo(actionEvent: ActionEvent) {
        SmartTreeViewDemo.start()
    }

    fun treeViewTest(actionEvent: ActionEvent) {
        val view = createView<TreeViewDemoView, AnchorPane>(
            CommonApplication::class.java.getResource("treeView.fxml")
        )
        val stage = Stage()
        val scene = Scene(view.ROOT_PANE)
        stage.title = "treeViewTest"
        stage.scene = scene
        stage.setOnCloseRequest {
            println("close...")
        }
        stage.show()
    }

}