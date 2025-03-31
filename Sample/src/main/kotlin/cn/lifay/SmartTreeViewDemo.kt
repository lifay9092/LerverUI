package cn.lifay

import cn.lifay.ui.tree.*
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.control.cell.CheckBoxTreeCell
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.Stage
import kotlin.random.Random


object SmartTreeViewDemo {

    fun start() {
        val primaryStage: Stage = Stage()
        val rootItem1 = CheckBoxTreeItem(Department(0, "根节点1", -1))
        val rootItem2 = CheckBoxTreeItem(Department(0, "根节点2", -1))
        val rootItem3 = TreeItem<Department>(Department(0, "根节点3", -1))

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
            parentIdProperty = Department::parentId,
            isCheckBox = true,
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
            idProperty = Department::id,
            childrenProperty = Department::children,
            isCheckBox = true
        )

        // 懒加载配置示例：
        val lazyConfig = TreeConfig.LazyLoadingConfig<Department, Int>(
            idProperty = Department::id,
            imgCall = { it ->
                ImageView(Image("cn/lifay/test/folder.png"))
            }
        )


        /* ----------------------------------- 创建树 -----------------------------------*/

        // 创建ID-PARENT_ID树
        val idParentTreeView = LerverTreeView<Department, Int>().apply {
            root = rootItem1
            cellFactory = CheckBoxTreeCell.forTreeView()
            LoadTreeConfig(idParentConfig)
            LoadTree { idParentData }
        }
        // 创建CHILDREN树
        val childrenTreeView = LerverTreeView<Department, Int>().apply {
            root = rootItem2
            cellFactory = CheckBoxTreeCell.forTreeView()
            LoadTreeConfig(childrenConfig)
            LoadTree { childrenData }
        }

        // 创建懒加载树
        val lazyTreeView = LerverTreeView<Department, Int>().apply {
            root = rootItem3
            LoadTreeConfig(lazyConfig)
            LoadTree {
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
                    childrenTreeView.Restore()
                } else {
                    childrenTreeView.FilterTree { it.id.toString().contains(text) }
                }
            }

        }

        val scene = Scene(
            VBox(
                TabPane(
                    Tab("ID-PARENT_ID树", idParentTreeView),
                    Tab(
                        "CHILDREN树", VBox(
                            HBox(20.0, filterText,
                                Button("添加节点").apply {
                                    setOnAction {
                                        val nextInt = Random.nextInt()
                                        (if (childrenConfig.isCheckBox) childrenTreeView.root as CheckBoxTreeItem<Department> else childrenTreeView.root as TreeItem<Department>)
                                            .AddChildren(
                                                Department(
                                                    id = nextInt,
                                                    name = "新部门-$nextInt",
                                                    parentId = 0
                                                )
                                            )
                                    }
                                }, Button("修改节点-新节点").apply {
                                    setOnAction {
                                        val nextInt = Random.nextInt()
                                        childrenTreeView.selectionModel.selectedItem?.let {
                                            it.apply {
                                                UpdateItem(
                                                    Department(
                                                        id = nextInt,
                                                        name = "${value.name}-$nextInt",
                                                        parentId = 0
                                                    )
                                                )
                                            }
                                        }
                                    }
                                }, Button("修改节点-只更新名称").apply {
                                    setOnAction {
                                        val nextInt = Random.nextInt()
                                        childrenTreeView.selectionModel.selectedItem?.let {
//                                            TreeItem.UpdateItem()
                                            it.apply {
                                                // println("old:${value.hashCode()}")
                                                value.apply {
                                                    name = "${value.name}-$nextInt"
                                                }
                                                // println("new:${value.hashCode()}")
                                                UpdateItem(
                                                    value
                                                )
                                            }
                                        }
                                    }
                                }, Button("删除节点").apply {
                                    setOnAction {
                                        childrenTreeView.selectionModel.selectedItem?.let {
                                            (it as TreeItem<Department>).DeleteThis(
                                            )

                                        }
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
        var name: String,
        val parentId: Int? = null,
        val children: List<Department>? = null

    ) {
        override fun toString(): String {
            return "id=$id name='$name', parentId=$parentId"
        }
    }

}
