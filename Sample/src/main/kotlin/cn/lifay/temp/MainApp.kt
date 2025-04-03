//package cn.lifay.temp
//
//import javafx.application.Application
//import javafx.scene.Scene
//import javafx.stage.Stage
//import tornadofx.*
//
//// 1. 应用启动类
//class TreeViewDemoApp : App(MainView::class) {
//    override fun start(stage: Stage) {
//        stage.width = 800.0
//        stage.height = 600.0
//        stage.title = "增强型TreeView演示"
//        super.start(stage)
//    }
//}
//
//// 2. 主视图定义
//class MainView : View("树形视图增强功能演示") {
//    // 声明可观察数据源
//    private val data = FXCollections.observableArrayList(
//        BindableTreeItem("技术文档"),
//        BindableTreeItem("项目计划")
//    )
//
//    // 界面组件声明
//    private lateinit var treeView: TreeView<String>
//    private lateinit var searchField: TextField
//    private lateinit var addButton: Button
//    private lateinit var deleteButton: Button
//
//    // 动态绑定示例
//    private val dynamicText = SimpleStringProperty("动态节点内容")
//
//    override val root = borderpane {
//        // 顶部工具栏
//        top = toolbar {
//            addButton = button("添加节点") {
//                graphic = graphic("plus-circle")
//                action { onAddNode() }
//            }
//
//            deleteButton = button("删除节点") {
//                graphic = graphic("trash")
//                action { onDeleteNode() }
//                enableWhen(treeView.selectionModel.selectedItemProperty().isNotNull)
//            }
//
//            searchField = textfield {
//                promptText = "输入搜索内容..."
//                style { padding = box(5.px) }
//            }
//        }
//
//        // 中央树视图
//        center = stackpane {
//            treeview<String> {
//                treeView = this
//                bindItems(data)
//
//                // 初始化搜索功能
//                createSearchEngine().apply {
//                    searchField.textProperty().addListener { _, _, newValue ->
//                        search(newValue) { node, keyword ->
//                            node.contains(keyword, true)
//                        }
//                    }
//                }
//
//                // 单元格样式
//                cellFormat {
//                    style {
//                        fontSize = 14.px
//                        padding = box(5.px)
//                    }
//                }
//            }
//        }
//
//        // 底部状态栏
//        bottom = hbox(10) {
//            paddingAll = 10
//            label("节点数量: ")
//            label().bind(data.sizeProperty().asString())
//        }
//    }
//
//    // 初始化完成后的操作
//    override fun onDock() {
//        // 动态绑定示例
//        data[0].bind(dynamicText)
//
//        // 初始化测试数据
//        initSampleData()
//    }
//
//    // 初始化测试数据
//    private fun initSampleData() {
//        val techNode = data[0]
//        techNode.children.addAll(
//            BindableTreeItem("需求文档"),
//            BindableTreeItem("设计文档").apply {
//                children.addAll(
//                    BindableTreeItem("架构设计"),
//                    BindableTreeItem("详细设计")
//                )
//            }
//        )
//
//        val projectNode = data[1]
//        projectNode.children.addAll(
//            BindableTreeItem("阶段1").apply {
//                children.addAll(
//                    BindableTreeItem("任务A"),
//                    BindableTreeItem("任务B")
//                )
//            },
//            BindableTreeItem("阶段2")
//        )
//    }
//
//    // 添加节点操作
//    private fun onAddNode() {
//        treeView.createOperator().addNode(
//            treeView.selectionModel.selectedItem,
//            "新建节点-${System.currentTimeMillis()}"
//        )
//    }
//
//    // 删除节点操作
//    private fun onDeleteNode() {
//        treeView.selectionModel.selectedItem?.let {
//            treeView.createOperator().deleteNode(it)
//        }
//    }
//}
//
//// 3. 主函数入口
//fun main(args: Array<String>) {
//    Application.launch(TreeViewDemoApp::class.java, *args)
//}