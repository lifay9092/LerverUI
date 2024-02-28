package cn.lifay.test

import cn.lifay.db.UserData
import cn.lifay.db.UserDatas
import cn.lifay.extension.alertConfirmation
import cn.lifay.extension.styleInfo
import cn.lifay.global.GlobalResource
import cn.lifay.ui.form.BaseFormUI
import cn.lifay.ui.form.btn.BaseButton
import cn.lifay.ui.form.check.CheckElement
import cn.lifay.ui.form.clearBtn
import cn.lifay.ui.form.radio.RadioElement
import cn.lifay.ui.form.select.SelectElement
import cn.lifay.ui.form.text.TextElement
import javafx.collections.FXCollections
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.Button
import javafx.scene.control.ListView
import javafx.scene.layout.AnchorPane
import org.ktorm.schema.BaseTable
import java.net.URL
import java.util.*


/**
 * FormUIView TODO
 * @author lifay
 * @date 2023/2/24 17:22
 **/
class FormUIView : Initializable{
    @FXML
    lateinit var listView : ListView<String>

    @FXML
    var rootPane = AnchorPane()


    fun baseFormTest(actionEvent: ActionEvent) {
        val baseFormUI = BaseFormUI<UserData>("测试基础表单") {
            //设置默认填充内容
            defaultEntity(UserData(11, "11", SelectTypeEnum.A, true, "男"))

            //定义和添加元素
            val nameElement =
                TextElement("名称1111111:", UserData::name, isTextArea = true, primary = false, initValue = "初始值") {
                    isDisable = true
                    isEditable = false
                }
            addElements(
                TextElement("ID:", UserData::id, true) {},
                nameElement,
                SelectElement("类型2222222:", UserData::type, SelectTypeEnum.values().toList()),
                CheckElement("是否未成年:", UserData::child),
                RadioElement("性别222222:", UserData::sex, listOf("男", "女")),
//                TextElement("S1名称1111111:", UserData::s1) {
//                    isEditable = false
//                },
//                TextElement("S2名称1111111:", UserData::s2) {
//                    isEditable = false
//                },
//                TextElement("S3名称1111111:", UserData::s3) {
//                    isEditable = false
//                },
//                TextElement("S4名称1111111:", UserData::s4) {
//                    isEditable = false
//                },

            )

            //添加按钮和操作
            addCustomButtons(
                BaseButton(Button("测试").styleInfo()) {
                    showNotification("测试获取name内容:${nameElement.getElementValue()}")
                },
                clearBtn()
            )

            //表单初始化后操作
            afterFormInitCall {
                showNotification("已经初始化完毕")
            }

            //窗口关闭操作
            setOnCloseRequest {
                println("窗口已关闭")
            }
        }
        baseFormUI.show()
    }

    fun formAddTest(actionEvent: ActionEvent) {
        val userForm = UserDataForm()
//        val userForm = UserForm("测试", UserData(1, "111111", SelectTypeEnum.C, true, "男"))
        userForm.ROOT_PANE.apply {
            prefWidth = GlobalResource.SCREEN_WIDTH * 0.9
            prefHeight = GlobalResource.SCREEN_HEIGHT * 0.9
        }
        userForm.show()
    }

    fun formUpdateTest(actionEvent: ActionEvent) {
        val userForm = UserDataForm(UserData(1, "121212", SelectTypeEnum.C, true, "男"), isUpdate = true)
//        val userForm = UserForm("测试", UserData(1, "111111", SelectTypeEnum.C, true, "男"))
        userForm.ROOT_PANE.apply {
            prefWidth = GlobalResource.SCREEN_WIDTH * 0.9
            prefHeight = GlobalResource.SCREEN_HEIGHT * 0.9
        }
        userForm.show()
    }

    fun formTestNew(actionEvent: ActionEvent) {
        val userForm = UserManage()
//        val userForm = UserForm("测试", UserData(1, "111111", SelectTypeEnum.C, true, "男"))
        userForm.show()
    }

    fun formTest2(actionEvent: ActionEvent) {
//        val personForm = PersonForm(Person(1, "111111", true))
//        personForm.show()
    }

    fun formMsg(actionEvent: ActionEvent) {

    }

    fun confirmationOld(actionEvent: ActionEvent) {
        alertConfirmation("将要执行删除操作,是否继续?")
    }

    fun confirmation(actionEvent: ActionEvent) {
        val alert = Alert(AlertType.CONFIRMATION)
        // alert.title = "Confirmation Dialog"
        //alert.headerText = "header"
        alert.contentText = "text"
//
//        val yesBtn = ButtonType("Yes", ButtonData.YES)
//        val noBtn = ButtonType("No", ButtonData.NO)
//        val cancelBtn = ButtonType(
//            "Cancel", ButtonData.CANCEL_CLOSE
//        )
//
//        alert.buttonTypes.setAll(yesBtn, noBtn, cancelBtn)
//        alert.initOwner(rootPane.scene.window)
        alert.showAndWait()
    }

    fun formTestTemp(actionEvent: ActionEvent) {
//        val stage = Stage()
//        val fxmlLoader = FXMLLoader(ManageUI::class.java.getResource("curd.fxml"))
//        val curdUI = CurdUI<UserData>(null, buildElements = {
//            val id = TextElement("ID:", UserData::id, true)
//            val name = TextElement("名称:", UserData::name, isTextArea = true, primary = false, initValue = "初始值")
//            val type = SelectElement("类型:", UserData::type, SelectTypeEnum.values().toList())
//            val child = CheckElement("是否未成年:", UserData::child)
//            val sex = RadioElement("性别:", UserData::sex, listOf("男", "女", "中间"))
//            addElement(id, name, type, child, sex)
//
//            addCustomBtn(CustomButtonNew(Button("测试自定义按钮").styleWarn()) {
//                println(it)
//            })
//        }, ::dbObject,add())
//        fxmlLoader.setController(curdUI)
//        val curdPane = fxmlLoader.load<Pane>()
//
//        stage.apply {
//            scene = Scene(curdPane)
//            this.title = title
//            GlobalResource.loadIcon(this)
//            show()
//        }
    }

    fun dbObject(): BaseTable<UserData> {
        return UserDatas
    }

    fun add(): List<UserData> {
        return listOf(
            UserData(1, "111111", SelectTypeEnum.A, true, "男"),
            UserData(2, "2222", SelectTypeEnum.B, false, "女"),
            UserData(3, "33333", SelectTypeEnum.C, true, "男")
        )
    }

    override fun initialize(p0: URL?, p1: ResourceBundle?) {
        listView = ListView(FXCollections.observableArrayList("1","1","1","1","1"))
    }


}