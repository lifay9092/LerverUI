package cn.lifay.ui.form

import cn.lifay.exception.LerverUIException
import cn.lifay.extension.platformRun
import cn.lifay.ui.BaseView
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import java.net.URL
import java.util.*


/**
 *@ClassName CurdUI
 *@Description TODO
 *@Author lifay
 *@Date 2023/8/19 22:50
 **/
class CurdUI<T : Any> (): BaseView<VBox>() {


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

    private lateinit var pageFunc: (Int, Int) -> Pair<Int, List<T>>
    private lateinit var heads: List<TableColumn<T, out Any>>
//
//    protected val elements: ObservableList<FormElement<T, *>> = FXCollections.observableArrayList()
//    protected val saveBtn: Button = Button("保存").stylePrimary().icon(Feather.CHECK)
//    protected val delBtn: Button = Button("删除").styleDanger().icon(Feather.TRASH)
//    protected val clearBtn: Button = Button("清空").styleWarn().icon(Feather.X)
//    protected val customButtons: ObservableList<CustomButtonNew<FormUI<T>>> = FXCollections.observableArrayList()

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

    fun InitUI(heads: List<TableColumn<T, out Any>>, pageFunc: (Int, Int) -> Pair<Int, List<T>>) {
        //表格布局
        this.heads = heads

        this.pageFunc = pageFunc
    }

    @FXML
    override fun initialize(p0: URL?, p1: ResourceBundle?) {
        println("CurdUI initialize")
        super.initialize(p0, p1)
        this.dataTable.columns.addAll(heads)
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
            println("数量:${dataTable.items.size}")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}