package cn.lifay.test

import cn.lifay.db.UserData
import cn.lifay.db.UserDatas
import cn.lifay.extension.alertConfirmation
import cn.lifay.global.GlobalResource
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType
import javafx.scene.layout.AnchorPane
import org.ktorm.schema.BaseTable


/**
 * FormUIView TODO
 * @author lifay
 * @date 2023/2/24 17:22
 **/
class FormUIView {
    @FXML
    var rootPane = AnchorPane()

    fun formTest(actionEvent: ActionEvent) {
        val userForm = UserForm()
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
    fun add():  List<UserData> {
        return listOf(
            UserData(1, "111111", SelectTypeEnum.A, true, "男"),
            UserData(2, "2222", SelectTypeEnum.B, false, "女"),
            UserData(3, "33333", SelectTypeEnum.C, true, "男")
        )
    }


}