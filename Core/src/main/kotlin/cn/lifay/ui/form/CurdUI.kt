package cn.lifay.ui.form

import atlantafx.base.theme.Styles
import cn.lifay.db.DbManage
import cn.lifay.exception.LerverUIException
import cn.lifay.extension.platformRun
import cn.lifay.ui.BaseView
import cn.lifay.ui.form.btn.CustomButton
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import org.ktorm.dsl.eq
import org.ktorm.dsl.select
import org.ktorm.dsl.where
import org.ktorm.schema.BaseTable
import org.ktorm.schema.Column
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList
import kotlin.reflect.KFunction1


/**
 *@ClassName CurdUI
 *@Description TODO
 *@Author lifay
 *@Date 2023/8/19 22:50
 **/
class CurdUI<T : Any> (    ): BaseView<VBox>() {


    @FXML
    val root = VBox()

    @FXML
    val form = GridPane()

    @FXML
    var dataTable = TableView<T>()
//
//    @FXML
//    var dataTable1 = TableView<T>()

    @FXML
    val btnGroup = HBox()

    @FXML
    var pagination = Pagination()

    @FXML
    var totalCountText = Label()

    @FXML
    var keyword = TextField()

    @FXML
    var pageIndexText = TextField()

    @FXML
    var pageCountText = TextField()
    val pageIndex: Int
        get() = pageIndexText.text.let {
            if (it.isBlank()) {
                return 0
            } else {
                return it.toInt() - 1
            }
        }

    val pageCount: Int
        get() = pageCountText.text.let {
            if (it.isBlank()) {
                return 10
            } else {
                return it.toInt()
            }
        }

    private lateinit var table: BaseTable<T>
    private lateinit var elements: ArrayList<FormElement<T, *>>
    private lateinit var pageFunc: (Int, Int) -> Pair<Int, List<T>>
    private lateinit var heads: List<TableColumn<T, out Any>>
//
//    protected val elements: ObservableList<FormElement<T, *>> = FXCollections.observableArrayList()
//    protected val saveBtn: Button = Button("保存").stylePrimary().icon(Feather.CHECK)
//    protected val delBtn: Button = Button("删除").styleDanger().icon(Feather.TRASH)
//    protected val clearBtn: Button = Button("清空").styleWarn().icon(Feather.X)
    protected val customButtons = ArrayList<CustomButton<FormUI<T>>>()
    protected lateinit var saveDataFunc: KFunction1<T, Boolean>
    protected lateinit var updateDataFunc: KFunction1<T, Boolean>
    private fun CurdUI() {}

    init {

        println("CurdUI init")
        try {
            ROOT_PANE = root
            //表单布局
            form.alignment = Pos.CENTER
            form.hgap = 10.0
            form.vgap = 10.0
            form.padding = Insets(25.0, 25.0, 25.0, 25.0)

        } catch (e: Exception) {
            e.printStackTrace()
            throw LerverUIException("表单初始化失败:${e.message}")
        }
    }

    /**
     * 注册根容器
     */
    override fun rootPane(): VBox {
        return this.root
    }

    fun InitElements(elements: List<FormElement<T, *>>) {
        this.elements = elements as ArrayList<FormElement<T, *>>
        this.heads = elements.map { it.getTableHead() }.toList()
    }

    fun InitDbFunc(table: BaseTable<T>, pageFunc: (Int, Int) -> Pair<Int, List<T>>) {
        //分页查询函数
        this.table = table
        this.pageFunc = pageFunc
    }
    fun InitFormFunc(saveDataFunc: KFunction1<T, Boolean>, updateDataFunc: KFunction1<T, Boolean>) {
        this.saveDataFunc = saveDataFunc
        this.updateDataFunc = updateDataFunc
    }


    @FXML
    override fun initialize(p0: URL?, p1: ResourceBundle?) {
        println("CurdUI initialize")
        super.initialize(p0, p1)
        this.dataTable.
        apply {
            padding = Insets(1.0, 2.0, 10.0, 2.0)
            columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
            Styles.toggleStyleClass(this, Styles.STRIPED)
            columns.addAll(heads)
            setRowFactory {
                val row = TableRow<T>()
                row.setOnMouseClicked { event ->
                    val item = row.item
                    if (item != null) {
                        if (event.clickCount == 2) {
                            editForm(item)
                        }
                    }
                }
                return@setRowFactory row
            }
        }
        println(this.dataTable.columns.size)

        pagination.currentPageIndexProperty().addListener { observableValue, old, new ->
            pageIndexText.text = (new.toInt() + 1).toString()
            search()
        }
        pageIndexText.text = "1"
        pageCountText.text = "10"

        search()
        //  initNotificationPane()
    }


    @FXML
    fun search(actionEvent: ActionEvent? = null) {
        platformRun {
            dataTable.items.clear()
            val pair = pageFunc(pageIndex, pageCount)
            totalCountText.text = "共 ${pair.first} 条"

            dataTable.items.addAll(
                pair.second
            )
//            dataTable1.items.addAll(
//                rows
//            )

        }
    }

    @FXML
    fun clear(actionEvent: ActionEvent? = null) {
        platformRun {
            keyword.clear()
        }
    }

    fun addForm(actionEvent: ActionEvent) {
        try {
//            dataTable1.items.addAll(add!!)
            val formUI = FormUI<T>("新增",null,
                elements = elements,
                customButtons = customButtons,
                saveDataFunc = {
                    DbManage.insert(table){
                        table.columns.forEach { col ->
                            for (element in elements) {
                                if (element.getPropName() == col.name) {
                                    val elementValue = element.getElementValue()
                                    println("name:${col.name} v:${elementValue}")
                                    elementValue?.let {
                                        when (it::class.java) {
                                            java.lang.Boolean::class.java -> {
                                                set(col as Column<Boolean> , it as Boolean)
                                            }
                                            java.lang.String::class.java -> {
                                                set(col as Column<String> , it as String)
                                            }

                                            java.lang.Integer::class.java -> {
                                                set(col as Column<Integer> , it as Integer)
                                            }
                                             java.lang.Double::class.java -> {
                                                set(col as Column<Double> , it as Double)
                                            }

                                            java.lang.Float::class.java -> {
                                                set(col as Column<Float> , it as Float)
                                            }
                                            java.lang.Long::class.java -> {
                                                set(col as Column<Long> , it as Long)
                                            }
                                            else -> {
                                                println("not surport")
                                                set(col as Column<String> , it.toString())
                                            }
                                        }
                                    }
                                    break
                                }
                            }
                        }
                    }
//
//                    saveDataFunc(it).apply {
//                        if (this) {
//                            search()
//                        }
//                    }
                    return@FormUI true
                },
                updateDataFunc = null)
            formUI.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun editForm(entity: T) {
        try {
//            dataTable1.items.addAll(add!!)
            val formUI = FormUI<T>("编辑",entity,
                elements = elements,
                customButtons = customButtons,
                saveDataFunc = null,
                updateDataFunc = {
                    DbManage.update(table){
                        table.columns.forEach { col ->
                            for (element in elements) {
                                if (element.getPropName() == col.name) {
                                    val elementValue = element.getElementValue()
                                    println("name:${col.name} v:${elementValue}")
                                    elementValue?.let {

                                        when (it::class.java) {
                                            java.lang.Boolean::class.java -> {
                                                set(col as Column<Boolean> , it as Boolean)
                                            }
                                            java.lang.String::class.java -> {
                                                set(col as Column<String> , it as String)
                                                if (element.primary) {
                                                    where {
                                                        col eq it
                                                    }
                                                }
                                            }

                                            java.lang.Integer::class.java -> {
                                                set(col as Column<Integer> , it as Integer)
                                                if (element.primary) {
                                                    where {
                                                        col eq it
                                                    }
                                                }
                                            }
                                            java.lang.Double::class.java -> {
                                                set(col as Column<Double> , it as Double)
                                            }

                                            java.lang.Float::class.java -> {
                                                set(col as Column<Float> , it as Float)
                                            }
                                            java.lang.Long::class.java -> {
                                                set(col as Column<Long> , it as Long)
                                                if (element.primary) {
                                                    where {
                                                        col eq it
                                                    }
                                                }
                                            }
                                            else -> {
                                                println("not surport")
                                                set(col as Column<String> , it.toString())
                                            }
                                        }
                                    }
                                    break
                                }
                            }

                        }
                    }
                    search()
                    true

                })
            formUI.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun addCustomButtons(customButtons: Array<out CustomButton<FormUI<T>>>) {
        this.customButtons.addAll(customButtons)
    }


}