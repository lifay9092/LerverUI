package cn.lifay.ui.form

import cn.lifay.db.DbManage
import cn.lifay.extension.bindEscKey
import cn.lifay.global.GlobalResource
import cn.lifay.ui.BaseView
import cn.lifay.ui.form.btn.CustomButtonNew
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.layout.VBox
import javafx.stage.Stage
import org.ktorm.entity.*
import org.ktorm.schema.BaseTable
import java.net.URL
import java.util.*

/**
 *@ClassName FormUINew
 *@Description TODO
 *@Author lifay
 *@Date 2023/8/19 23:03
 **/
abstract class ManageUI<T : Any>(
    title: String,
    buildElements: ManageUI<T>.() -> Unit,
) : BaseView<VBox>() {

    private val stage = Stage().bindEscKey()

    protected lateinit var root: VBox
    private val elements: ObservableList<FormElement<T, *>> = FXCollections.observableArrayList()
    private var dbObject: EntitySequence<T, BaseTable<T>>
    private val customButtons: ObservableList<CustomButtonNew<ManageUI<T>>> = FXCollections.observableArrayList()

    /**
     * 表单不是fxml导入，子类不需要当前方法
     */
    override fun initialize(p0: URL?, p1: ResourceBundle?) {
        super.initialize(p0, p1)
    }

    init {
        println("FormUINew init")
        this.dbObject = DbManage.database.sequenceOf(dbObject())
        buildElements()

        val fxmlLoader = FXMLLoader(ManageUI::class.java.getResource("curd.fxml"))
        val curdUI = CurdUI<T>()
        curdUI.InitUI(
            elements.map { it.getTableHead() }.toList()
        ) { pageIndex, pageCount ->

            return@InitUI Pair(
                dbObject.totalRecordsInAllPages, dbObject.drop(pageIndex * pageCount)
                    .take(pageCount).toList()
            )
        }
        fxmlLoader.setController(curdUI)
        val curdPane = fxmlLoader.load<VBox>()
        root.children.add(curdPane)

        stage.apply {
            scene = Scene(root)
            this.title = title
            GlobalResource.loadIcon(this)
        }
    }

    /**
     * 注册根容器
     * @author lifay
     * @return
     */
    override fun rootPane(): VBox {
        this.root = VBox(10.0).apply {
            prefWidth = GlobalResource.FormWidth()
            prefHeight = GlobalResource.FormHeight()
        }
        return this.root
    }

    //    abstract fun <V : BaseTable<T>> dbObject(): EntitySequence<T,V>
    abstract fun dbObject(): BaseTable<T>

    fun addElement(vararg element: FormElement<T, *>) {
        println("addElement")
        elements.addAll(*element)
    }

    fun addCustomBtn(vararg customButton: CustomButtonNew<ManageUI<T>>) {
        customButtons.addAll(*customButton)
    }

    fun page(pageIndex: Int, pageCount: Int): Pair<Int, List<T>> {
        return Pair(
            dbObject.totalRecordsInAllPages, dbObject.drop(pageIndex * pageCount)
                .take(pageCount).toList()
        )
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