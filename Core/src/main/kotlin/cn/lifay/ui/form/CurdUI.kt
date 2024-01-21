package cn.lifay.ui.form

import atlantafx.base.theme.Styles
import cn.lifay.exception.LerverUIException
import cn.lifay.extension.*
import cn.lifay.ui.BaseView
import cn.lifay.ui.form.btn.BaseButton
import javafx.beans.property.SimpleBooleanProperty
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.control.cell.CheckBoxTableCell
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.text.TextAlignment
import javafx.stage.Stage
import org.kordamp.ikonli.feather.Feather
import org.kordamp.ikonli.material2.Material2AL
import org.ktorm.entity.*
import org.ktorm.schema.BaseTable
import org.ktorm.schema.ColumnDeclaring
import java.net.URL
import java.util.*


/**
 *@ClassName CurdUI
 *@Description TODO
 *@Author lifay
 *@Date 2023/8/19 22:50
 **/
abstract class CurdUI<T : Any,E : BaseTable<T>>(
    title: String,
    buildElements: CurdUI<T,E>.() -> Unit,
) : BaseView<VBox>() {

    private val stage = Stage().apply {
        bindEscKey()
        loadIcon()
    }

    @FXML
    val root = VBox().apply {
        prefHeight = 576.0
        prefWidth = 918.0
    }


    @FXML
    val btnGroup = HBox()


    @FXML
    var dataTable = TableView<T>()
//
//    @FXML
//    var dataTable1 = TableView<T>()

    @FXML
    val endPane = HBox()

    @FXML
    var pagination = Pagination()

    @FXML
    var totalCountText = Label()

    @FXML
    var keyword = TextField().apply {
        HBox.setMargin(this, Insets(0.0, 20.0, 0.0, 10.0))
    }

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


    private val elements = ArrayList<FormElement<T, *>>()

    protected val customButtons = ArrayList<BaseButton<BaseFormUI<T>>>()

    private fun CurdUI() {}

    init {

       // println("CurdUI init")
        try {
            buildElements()

            ROOT_PANE = root

            btnGroup.apply {
                alignment = Pos.CENTER_LEFT
                prefHeight = 47.0
                prefWidth = 850.0
                VBox.setMargin(this, Insets(20.0, 0.0, 10.0, 0.0))
                children.addAll(
                    keyword,
                    Button("搜索").apply {
                        prefHeight = 23.0
                        prefWidth = 62.0
                        HBox.setMargin(this, Insets(0.0, 20.0, 0.0, 0.0))
                        stylePrimary()
                        setOnAction {
                            search(it)
                        }
                    },
                    Button("重置").apply {
                        prefHeight = 23.0
                        prefWidth = 62.0
                        HBox.setMargin(this, Insets(0.0, 300.0, 0.0, 0.0))
                        setOnAction {
                            clear(it)
                        }
                    },
                    Button("新增").apply {
                        prefHeight = 23.0
                        prefWidth = 82.0
                        stylePrimary()
                        icon(Material2AL.ADD)
                        outline()
                        setOnAction {
                            addForm(it)
                        }
                    },
                    Button("删除").apply {
                        alignment = Pos.CENTER
                        textAlignment = TextAlignment.CENTER
                        prefHeight = 23.0
                        prefWidth = 82.0
                        styleDanger()
                        icon(Feather.TRASH)
                        HBox.setMargin(this, Insets(0.0, 10.0, 0.0, 10.0))
                        setOnAction {
                            batchDelete(it)
                        }
                    }
                )
            }
            this.dataTable.apply {
                padding = Insets(1.0, 2.0, 10.0, 2.0)
                prefHeight = 558.0
                prefWidth = 850.0
                columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
                Styles.toggleStyleClass(this, Styles.STRIPED)
                this.isEditable = true
                columns.addAll(InitTableHeads())
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
            endPane.apply {
                alignment = Pos.CENTER_LEFT
                prefHeight = 26.0
                prefWidth = 850.0
                children.addAll(
                    totalCountText.apply {
                        HBox.setMargin(this, Insets(0.0, 20.0, 0.0, 10.0))
                    },
                    pagination.apply {
                        prefHeight = 26.0
                        prefWidth = 287.0
                        style = "-fx-page-information-visible: false;"
                        HBox.setMargin(this, Insets(0.0, 0.0, 0.0, 30.0))
                    },
                    Label("跳转到").apply {
                        prefHeight = 15.0
                        prefWidth = 43.0
                        HBox.setMargin(this, Insets(0.0, 0.0, 0.0, 5.0))
                    },
                    pageIndexText.apply {
                        prefHeight = 23.0
                        prefWidth = 48.0
                        HBox.setMargin(this, Insets(0.0, 0.0, 0.0, 5.0))
                    },
                    Button("GO").apply {
                        prefHeight = 23.0
                        prefWidth = 48.0
                        HBox.setMargin(this, Insets(0.0, 0.0, 0.0, 3.0))
                        setOnAction {
                            search(it)
                        }
                    },
                    Label("每页数量").apply {
                        HBox.setMargin(this, Insets(0.0, 0.0, 0.0, 11.0))
                    },
                    pageCountText.apply {
                        prefHeight = 23.0
                        prefWidth = 48.0
                        HBox.setMargin(this, Insets(0.0, 0.0, 0.0, 5.0))
                    },
                )
            }
            root.children.addAll(btnGroup, dataTable, endPane)
            stage.scene = Scene(root)

            //   println(this.dataTable.columns.size)

            pagination.currentPageIndexProperty().addListener { observableValue, old, new ->
                pageIndexText.text = (new.toInt() + 1).toString()
                search()
            }
            pageIndexText.text = "1"
            pageCountText.text = "10"

            initNotificationPane()

            search()
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

    //是否操作所有元素
    var selectAll = true

    //为元素提供索引
    val checkMap = HashMap<Int, SimpleBooleanProperty>()

    //清空checkMap回调函数
    lateinit var clearDataTableCheck: () -> Unit

    fun InitTableHeads(): List<TableColumn<T, *>> {
        val checkBox = CheckBox()
        val checkTableColumn = TableColumn<T, Boolean>().apply {
            this.graphic = checkBox
            this.isEditable = true
            this.isSortable = false
            this.cellFactory = CheckBoxTableCell.forTableColumn(this)
            this.setCellValueFactory {
                val code = it.value.hashCode()
                if (!checkMap.containsKey(code)) {
                    val defaultBool = SimpleBooleanProperty(false)
                    checkMap[code] = defaultBool
                    return@setCellValueFactory defaultBool
                } else {
                    val result = checkMap[code]
//                    println("setCellValueFactory:$result")
                    if (!result!!.value) {
                        //部分取消勾选
                        selectAll = false
                        checkBox.isSelected = false
                        selectAll = true
                    } else {
                        //部分勾选
                        var all = true
                        checkMap.forEach { (k, v) ->
                            if (k != code && !v.value) {
                                all = false
                                return@forEach
                            }
                        }
                        if (all) {
                            selectAll = false
                            checkBox.isSelected = true
                            selectAll = true
                        }
                    }
                    return@setCellValueFactory result
                }
            }
        }
        checkBox.selectedProperty().addListener { observableValue, old, new ->
            if (selectAll) {
                if (!old && new) {
                    //全选
                    checkMap.forEach { k, v ->
                        v.value = true
                    }
                } else if (old && !new) {
                    //取消全选
                    checkMap.forEach { k, v ->
                        v.value = false
                    }
                }
            }

        }
        clearDataTableCheck = {
            checkMap.clear()
            checkBox.isSelected = false
        }
        return mutableListOf<TableColumn<T, *>>(
            checkTableColumn
        ).apply {
            addAll(elements.map { it.getTableHead() }.toList())
        }

    }

//    fun InitFormFunc(saveDataFunc: KFunction1<T, Boolean>, updateDataFunc: KFunction1<T, Boolean>) {
//        this.saveDataFunc = saveDataFunc
//        this.updateDataFunc = updateDataFunc
//    }


    override fun initialize(p0: URL?, p1: ResourceBundle?) {
      //  println("CurdUI initialize")
        super.initialize(p0, p1)

        //  initNotificationPane()
    }

    /**
     * 分页查询函数
     *
     * @param keyword 关键字
     * @param pageIndex 页码
     * @param pageCount 每页数量
     * @return f-总数量 s-分页数据列表
     */
//    abstract fun pageDataFunc(keyword : String,pageIndex: Int, pageCount: Int): Pair<Int, Collection<T>>

    /**
     * 分页查询函数
     *
     * @param keyword 关键字
     * @return f-ObjectBaseTable s-关键字匹配逻辑
     */
    abstract fun pageInit(keyword:String): Pair<EntitySequence<T, E>,((E) -> ColumnDeclaring<Boolean>)?>

    /**
     * 保存数据函数
     *
     * @param entity 数据
     * @return 执行结果
     */
    abstract fun saveDataFunc(entity: T): Boolean

    /**
     * 更新数据函数
     *
     * @param entity 数据
     * @return 执行结果
     */
    abstract fun updateDataFunc(entity: T): Boolean

    /**
     * 删除数据函数
     *
     * @param entity 数据
     * @return 执行结果
     */
    abstract fun delDataFunc(entity: T): Boolean

    fun search(actionEvent: ActionEvent? = null) {
        platformRun {
            dataTable.items.clear()
            clearDataTableCheck()

            val pageInit = pageInit(keyword.text)
            val entitySequence = pageInit.first
            val filterFunc = pageInit.second
            if (filterFunc != null) {
                totalCountText.text = "共 ${entitySequence.totalRecordsInAllPages} 条"
                dataTable.items.addAll(
                    entitySequence.filter {
                        return@filter filterFunc(it)
                    }.drop(pageIndex * pageCount)
                        .take(pageCount).toList()
                )
            } else {
                totalCountText.text = "共 ${entitySequence.totalRecordsInAllPages} 条"
                dataTable.items.addAll(
                    entitySequence.drop(pageIndex * pageCount)
                        .take(pageCount).toList()
                )
            }

        }
    }

    /*
        获取勾选的元素
     */
    fun getCheckedItems(): List<T> {
        return dataTable.items.filter {
            val hashCode = it.hashCode()
            if (checkMap.containsKey(hashCode)) {
                return@filter checkMap[hashCode]!!.value
            }
            return@filter false
        }.toList()
    }

    @FXML
    fun clear(actionEvent: ActionEvent? = null) {
        platformRun {
            keyword.clear()
        }
    }

    @FXML
    fun addForm(actionEvent: ActionEvent) {
        try {
//            dataTable1.items.addAll(add!!)
            val dataFormUI = object : DataFormUI<T>(buildFormUI = {
                addElements(elements)
                addCustomButtons(*customButtons.toTypedArray())
            }) {
                override fun saveData(entity: T): Boolean {
                    if (this@CurdUI.saveDataFunc(entity)) {
                        search()
                        return true
                    }
                    return false
                }

                override fun updateData(entity: T): Boolean {
                    if (this@CurdUI.updateDataFunc(entity)) {
                        search()
                        return true
                    }
                    return false
                }

            }
            dataFormUI.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun editForm(entity: T) {
        try {
            val dataFormUI = object : DataFormUI<T>(true, buildFormUI = {
                defaultEntity(entity)
                addElements(elements)
                addCustomButtons(*customButtons.toTypedArray())
            }) {
                override fun saveData(entity: T): Boolean {
                    if (this@CurdUI.saveDataFunc(entity)) {
                        search()
                        return true
                    }
                    return false
                }

                override fun updateData(entity: T): Boolean {
                    if (this@CurdUI.updateDataFunc(entity)) {
                        search()
                        return true
                    }
                    return false
                }

            }
            dataFormUI.setOnCloseRequest {
                elements.forEach {
                    it.graphic().isDisable = false
                    it.clear()
//                    println(it.getElementValue())
                }
            }
            dataFormUI.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    @FXML
    fun batchDelete(actionEvent: ActionEvent) {
        try {
            val items = getCheckedItems()
            if (items.isEmpty()) {
                return
            }
            if (alertConfirmation("是否删除这 ${items.size} 条数据?")) {
                for (checkedItem in items) {
                    delDataFunc(checkedItem)
                }
                showNotification("删除成功")
                search()
            }

        } catch (e: Exception) {
            e.printStackTrace()
            alertError("删除失败:${e.message}", true)
        }
    }

    fun addElements(vararg elements: FormElement<T, *>) {
        this.elements.addAll(elements)
    }

    fun addCustomButtons(vararg customButtons: BaseButton<BaseFormUI<T>>) {
        this.customButtons.addAll(customButtons)
    }

    fun show() {
        stage.show()
    }

    fun showAndWait() {
        stage.showAndWait()
    }

    fun close() {
        stage.close()
    }

}