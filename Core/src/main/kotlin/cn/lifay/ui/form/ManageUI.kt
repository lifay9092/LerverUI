//package cn.lifay.ui.form
//
//import cn.lifay.db.DbManage
//import cn.lifay.extension.bindEscKey
//import cn.lifay.global.GlobalResource
//import cn.lifay.ui.BaseView
//import cn.lifay.ui.form.btn.CustomButton
//import javafx.fxml.FXMLLoader
//import javafx.scene.Scene
//import javafx.scene.layout.VBox
//import javafx.stage.Stage
//import org.ktorm.entity.*
//import org.ktorm.schema.BaseTable
//import org.ktorm.schema.Column
//import java.net.URL
//import java.util.*
//
///**
// *@ClassName FormUINew
// *@Description TODO
// *@Author lifay
// *@Date 2023/8/19 23:03
// **/
//abstract class ManageUI<T : Any>(
//    title: String,
//    buildElements: ManageUI<T>.() -> Unit,
//) : BaseView<VBox>() {
//
//    private val stage = Stage().bindEscKey()
//
//    protected lateinit var root: VBox
//    private var table: BaseTable<T>
//    private val curdUI = CurdUI<T>()
//
//    /**
//     * 表单不是fxml导入，子类不需要当前方法
//     */
//    override fun initialize(p0: URL?, p1: ResourceBundle?) {
//        super.initialize(p0, p1)
//    }
//
//    init {
//        println("FormUINew init")
//        this.table = dbObject()
//        buildElements()
//
//
////        val fxmlLoader = FXMLLoader(ManageUI::class.java.getResource("curd.fxml"))
//
//        curdUI.apply {
//            InitDbFunc(table)
////            InitFormFunc(::saveDataFunc,::updateDataFunc)
//        }
//
////        fxmlLoader.setController(curdUI)
////        val curdPane = fxmlLoader.load<VBox>()
//        root.children.add(curdUI.root)
//
//
//        stage.apply {
//            scene = Scene(root)
//            this.title = title
//            GlobalResource.loadIcon(this)
//        }
//    }
//
//
//    /**
//     * 注册根容器
//     * @author lifay
//     * @return
//     */
//    override fun rootPane(): VBox {
//        this.root = VBox(10.0).apply {
//           // prefWidth = GlobalResource.FormWidth()
//           // prefHeight = GlobalResource.FormHeight()
//        }
//        return this.root
//    }
//
//    //    abstract fun <V : BaseTable<T>> dbObject(): EntitySequence<T,V>
//    abstract fun dbObject(): BaseTable<T>
//    abstract fun saveDataFunc(entity:T): Boolean
//
//    abstract fun updateDataFunc(entity:T): Boolean
//
//    fun addElements(vararg elements: FormElement<T, *>) {
//        curdUI.InitElements(elements.toList())
//    }
//
//    fun addCustomBtns(vararg customButtons: CustomButton<FormUI<T>>) {
//        curdUI.addCustomButtons(customButtons)
//    }
//
//    fun show() {
//        stage.show()
//    }
//
//    fun showAndWait() {
//        stage.showAndWait()
//    }
//
//    fun close() {
//        stage.close()
//    }
//
//}