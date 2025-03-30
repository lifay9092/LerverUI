package cn.lifay.test

import atlantafx.base.controls.Spacer
import atlantafx.base.theme.Styles
import cn.lifay.db.UserData
import cn.lifay.extension.*
import cn.lifay.ui.BaseView
import cn.lifay.ui.tree.*
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.control.cell.CheckBoxTreeCell
import javafx.scene.input.MouseButton
import javafx.scene.layout.AnchorPane
import kotlinx.coroutines.delay
import java.net.URL
import java.util.*


/**
 * @ClassName DemoView
 * @Description TODO
 * @Author 李方宇
 * @Date 2023/1/9 16:14
 */
class TreeViewDemoView : BaseView<AnchorPane>() {

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

    @FXML
    var botTreeView = LerverTreeView<Person, Int>()

    lateinit var rootTreeItem: TreeItem<TreeListVO>
    lateinit var rootCheckTreeItem: CheckBoxTreeItem<TreeTreeVO>

    override fun rootPane(): AnchorPane {
        return rootPane
    }

    override fun initialize(p0: URL?, p1: ResourceBundle?) {

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
            RegisterByList(
                LerverTreeNodeListProp(TreeListVO::id, TreeListVO::parentId),
                checkBox = false, init = true
            ) {
                println("treeView initDataCall...")
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
                        items.add(MenuItem("根据主键更新子节点").apply {
                            setOnAction {
                                println("根据主键更新子节点")
                                TextInputDialog().apply {
                                    headerText = "输入主键"
                                    contentText = null
                                    setOnCloseRequest {
                                        println(editor.text)
                                        val selectedItem = centerTreeView.selectionModel.selectedItem
                                        if (selectedItem.children.isNotEmpty()) {
                                            selectedItem.UpdateChild(
                                                TreeListVO(
                                                    editor.text,
                                                    selectedItem.value.id,
                                                    "${editor.text}_修改了",
                                                    SimpleStringProperty(editor.text)
                                                )
                                            )
                                        }
                                    }
                                    show()
                                }
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
                        if (selectedItem.AllowLoadChildren()) {
                            asyncTask {
                                val id = UUID.randomUUID().toString()
                                selectedItem.AddChildren(
                                    TreeListVO(id, value.id, "${value.name}-$id", SimpleStringProperty())
                                )
                            }
                        }
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
            RegisterByTree(
                LerverTreeNodeTreeProp(TreeTreeVO::id, TreeTreeVO::children, TreeTreeVO::leaf),
                true, true
            ) {
                println("checkTreeView initDataCall...")

//                findMatchingTreeChildren(TreeTreeVO(
//                    "1", "0", "1", false, arrayListOf(
//                        TreeTreeVO(
//                            "2", "1", "2", false, arrayListOf(
//                                TreeTreeVO("4", "2", "4", true, null)
//                            )
//                        ),
//                        TreeTreeVO("3", "1", "3", true, null)
//                    )
//                ),TreeTreeVO::children){
//                    true
//                }
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
        val botTreeItem = LerverTreeItem<Person, Int>(Person(0, "根节点", false))
        botTreeView.apply {
            root = botTreeItem


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


}