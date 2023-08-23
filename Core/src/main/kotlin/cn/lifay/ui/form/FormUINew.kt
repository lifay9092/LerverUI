package cn.lifay.ui.form

import cn.lifay.extension.*
import cn.lifay.global.GlobalResource
import cn.lifay.ui.BaseView
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableValue
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.layout.VBox
import javafx.stage.Stage
import javafx.util.Callback
import org.ktorm.schema.BaseTable
import java.net.URL
import java.util.*

/**
 *@ClassName FormUINew
 *@Description TODO
 *@Author lifay
 *@Date 2023/8/19 23:03
 **/
abstract class FormUINew<T : Any>(
    title: String,
    initDefaultEntity: T? = null,
    buildElements: CurdUI<T>.() -> Unit,
) : BaseView<VBox>() {

    protected var entity: T? = null
    private val stage = Stage().bindEscKey()

    protected lateinit var root : VBox


    /**
     * 表单不是fxml导入，子类不需要当前方法
     */
    override fun initialize(p0: URL?, p1: ResourceBundle?) {
        super.initialize(p0, p1)
    }
    init {
        println("FormUINew init")

        println(title)



        val fxmlLoader = FXMLLoader(FormUINew::class.java.getResource("curd.fxml"))
        val curdUI = CurdUI<T>(initDefaultEntity,buildElements,::dbObject,add())
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

    abstract fun add():List<T>


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