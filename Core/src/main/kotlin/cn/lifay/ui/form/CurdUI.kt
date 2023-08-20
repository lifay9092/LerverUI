package cn.lifay.ui.form

import atlantafx.base.theme.Styles
import cn.lifay.db.DbManage
import cn.lifay.exception.LerverUIException
import cn.lifay.extension.*
import cn.lifay.ui.BaseView
import cn.lifay.ui.form.btn.CustomButtonNew
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.util.Callback
import org.ktorm.entity.*
import org.ktorm.schema.BaseTable
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
    lateinit var dataTable : TableView<T>

    @FXML
    val btnGroup = HBox()

    @FXML
    var pagination = Pagination()

    @FXML
    var totalCountText = Label()

    @FXML
    val pageIndexText = TextField()

    @FXML
    val pageCountText = TextField()

    val pageIndex :Int
        get() = pageIndexText.text.let {
            if (it.isBlank()) {
                return 0
            } else {
                return it.toInt()
            }
        }

    val pageCount :Int
        get() = pageCountText.text.let {
            if (it.isBlank()) {
                return 10
            } else {
                return it.toInt()
            }
        }

    var entity: T? = null
    var initDefaultEntity: T? = null
    lateinit var dbObject: EntitySequence<T, BaseTable<T>>

    protected val elements: ObservableList<FormElement<T, *>> = FXCollections.observableArrayList()
    protected val customButtons: ObservableList<CustomButtonNew<CurdUI<T>>> = FXCollections.observableArrayList()

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
//    /**
//     * 注册根容器
//     */
//    override fun rootPane(): VBox {
//        return this.root
//    }

    override fun initialize(p0: URL?, p1: ResourceBundle?) {
        println("CurdUI initialize")

        //表格布局
        this.dataTable = TableView<T>().apply {
            padding = Insets(1.0, 2.0, 10.0, 2.0)
            columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
            Styles.toggleStyleClass(dataTable, Styles.STRIPED)
            columns.addAll(listOf(
                TableColumn<T, String>("性别").apply {
                    cellValueFactory = PropertyValueFactory("sex")
                    prefWidth = 100.0
                }

            ))
            columns.addAll(tableHeadColumns())
            setRowFactory {
                val row = TableRow<T>()
                row.setOnMouseClicked { event ->
                    val item = row.item
                    if (item != null) {
                        if (event.clickCount == 2) {
                            //refreshForm(item)
                        }
                    }

                }
                return@setRowFactory row
            }
        }

        pagination.currentPageIndexProperty().addListener { observableValue, old, new ->
            refreshTable(new.toInt())
        }

        refreshTable(pageIndex)
        initNotificationPane()

    }
    var add: List<T>? = null
    constructor(initDefaultEntity: T?, buildElements: CurdUI<T>.() -> Unit, dbObjectGet: () -> BaseTable<T>, add: List<T>) : this() {
        println("CurdUI load")

        this.initDefaultEntity = initDefaultEntity
        this.dbObject = DbManage.database.sequenceOf(dbObjectGet())
        this.add = add
        buildElements()


    }

    /*
        表头设置
     */
    private fun tableHeadColumns(): List<TableColumn<T, out Any>> {
        return elements.map { it.getTableHead() }.toList()
    }

    fun addElement(vararg element: FormElement<T, *>) {
        println("addElement")
        elements.addAll(*element)
    }

    fun addCustomBtn(vararg customButton: CustomButtonNew<CurdUI<T>>) {
        customButtons.addAll(*customButton)
    }

    private fun refreshTable(index :Int) {
        platformRun {
           // dataTable.items.clear()
            totalCountText.text = "共 ${dbObject.totalRecordsInAllPages} 条"
            val rows = dbObject.drop(index * pageCount)
                .take(pageCount).toList()
            dataTable.items.addAll(
                rows
            )

        }
    }

    fun addForm(actionEvent: ActionEvent) {
        try {
            dataTable.items.addAll(add!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}