package cn.lifay.ui.tree.test

import cn.lifay.ui.tree.test.ty.*
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.control.TextField
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.Stage
import kotlin.random.Random


class TreeViewDemo : Application() {

    override fun start(primaryStage: Stage) {
        val rootItem1 = SmartTreeItem<Department, Int>(Department(0, "根节点1", -1))
        val rootItem2 = SmartTreeItem<Department, Int>(Department(0, "根节点2", -1))
        val rootItem3 = SmartTreeItem<Department, Int>(Department(0, "根节点3", -1))

        // 数据源1：id/parentId结构
        val idParentData = listOf(
            Department(1, "总公司", 0),
            Department(2, "技术部", 1),
            Department(3, "市场部", 1),
            Department(4, "市场分部", 3)
        )

        // ID-PARENT_ID配置示例：
        val idParentConfig = TreeConfig.IdParentIdConfig(
            idProperty = Department::id,
            parentIdProperty = Department::parentId
        )

        // 数据源2：children结构
        val childrenData = listOf(
            Department(
                1, "总公司", children = listOf(
                    Department(
                        2, "技术部", children = listOf(
                            Department(4, "前端组", parentId = 2),
                            Department(5, "后端组", parentId = 2)
                        )
                    ),
                    Department(3, "市场部")
                )
            )
        )
        // CHILDREN属性配置示例：
        val childrenConfig = TreeConfig.ChildrenPropertyConfig<Department, Int>(
            childrenProperty = Department::children
        )

        // 懒加载配置示例：
        val lazyConfig = TreeConfig.LazyLoadingConfig<Department, Int>(
        )


        /* ----------------------------------- 创建树 -----------------------------------*/

        // 创建ID-PARENT_ID树
        val idParentTreeView = SmartTreeView(idParentConfig).apply {
            root = rootItem1
            loadData { idParentData }
        }
        // 创建CHILDREN树
        val childrenTreeView = SmartTreeView(childrenConfig).apply {
            root = rootItem2
            loadData { childrenData }
        }

        // 创建懒加载树
        val lazyTreeView = SmartTreeView(lazyConfig).apply {
            root = rootItem3
            loadData {
                listOf(
                    Department(
                        it.id + 100, "${it.name}_${it.id}"
                    )
                )
            }
        }

        // 过滤操作
        val filterText = TextField("").apply {
            textProperty().addListener { _, _, text ->
                if (text.isBlank()) {
                    childrenTreeView.restore()
                } else {
                    childrenTreeView.filter { it.value?.toString()?.contains(text) ?: false }
                }
            }

        }
        // 增删改操作示例
//        val addButton = Button("添加节点").apply {
//            setOnAction {
//                (treeView.selectionModel.selectedItem?.value as? Department)?.let { selected ->
//                    val newDept = Department(
//                        id = Random.nextInt(),
//                        name = "新部门",
//                        parentId = selected.id
//                    )
//                    (treeView.selectionModel.selectedItem as FilterableTreeItem<Department>)
//                        .addChild(newDept)
//                }
//            }
//        }
        val node = Department(
            id = Random.nextInt(),
            name = "新部门",
            parentId = 0
        )

        val scene = Scene(
            VBox(
                TabPane(
                    Tab("ID-PARENT_ID树", idParentTreeView),
                    Tab(
                        "CHILDREN树", VBox(
                            HBox(20.0, filterText, Button("添加节点").apply {
                                setOnAction {
                                    (childrenTreeView.root as SmartTreeItem<Department, Int>).addNode(
                                        node
                                    )
                                }
                            }, Button("删除节点").apply {
                                setOnAction {
                                    (childrenTreeView.root as SmartTreeItem<Department, Int>).removeNode(
                                        node
                                    )
                                }
                            }), childrenTreeView
                        )
                    ),
                    Tab("懒加载树", lazyTreeView)
                )
            ), 800.0, 900.0
        )
        primaryStage.scene = scene
        primaryStage.show()
    }

    data class Department(
        val id: Int,
        val name: String,
        val parentId: Int? = null,
        val children: List<Department>? = null

    ) {
        override fun toString(): String {
            return "id=$id name='$name', parentId=$parentId"
        }
    }

}

fun main() {
    Application.launch(TreeViewDemo::class.java)
}