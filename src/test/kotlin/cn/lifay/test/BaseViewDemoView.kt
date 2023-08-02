package cn.lifay.test

import cn.lifay.ui.BaseView
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.HBox
import java.net.URL
import java.util.*
import kotlin.reflect.KMutableProperty0


/*
 * EasyView TODO
 * @author lifay
 * @date 2023/2/21 8:56
 */

class BaseViewDemoView : BaseView<AnchorPane>() {
    @FXML
    var rootPane = AnchorPane()

    @FXML
    var tabPane = TabPane()
    override fun rootPane(): KMutableProperty0<AnchorPane> {
        return this::rootPane
    }

    fun aaa(): String {
        return "aaaaaaaaaa"
    }

    override fun initialize(p0: URL?, p1: ResourceBundle?) {
        super.initialize(p0, p1)
//        println("initialize")

        val tab1 = Tab("2222").apply {
            id = "3333"
            content = Label("22222")
        }
        tabPane.tabs.add(tab1)
//        val tab1 = Tab("测试111").apply {
//            id = "111111"
//            content = HBox(Button("测试啊11111"))
//        }
//        tabPane.tabs.add(tab1)
    }

    fun fxml(): URL? {
        val resource = this::class.java.getResource("baseViewDemo.fxml")
        println(resource)
        return resource
    }

    fun test() {
        val tab1 = Tab("2222").apply {
            id = "2222"
            content = HBox(Button("22222"))
        }

        tabPane.tabs.add(tab1)
        println("ddddddddddddd")
        //  showNotification("测试消息")
        //  showNotification("测试消息2", Notification.Type.SUCCESS)
        // showNotification("测试消息3",Notification.Type.ERROR,2000)


    }

    fun msgTest() {
        val tab1 = Tab("3333").apply {
            id = "3333"
            content = HBox(Button("33333"))
        }
        tabPane.tabs.add(tab1)
        println(tabPane.tabs.size)
        println("ddddddddddddd")


    }

    fun msgTestNew() {
        showNotification("DASDASDASD")
//        val vBox = VBox()
//        val msg = atlantafx.base.controls.Notification(
//            "消息sda打萨达萨达撒旦",
//            FontIcon(Material2OutlinedAL.HELP_OUTLINE)
//        )
//        msg.getStyleClass().addAll(
//            Styles.ACCENT, Styles.ELEVATED_1
//        )
//        msg.setOnClose {
//            val closePlay = Animations.slideOutUp(msg, Duration.millis(250.0))
//            closePlay.setOnFinished { rootPane.children.remove(vBox) }
//            closePlay.playFromStart()
//        }
//        msg.maxWidth = 400.0
//        msg.minWidth = 400.0
////        msg.setMaxHeight(Region.USE_PREF_SIZE)
//        vBox.children.add(msg)
//        vBox.isManaged = false
//        vBox.spacing = 15.0
////        stackPane.padding = 15.0
//        vBox.layoutY = 15.0
//      //  vBox.layoutX = rootPane.prefWidth - msg.prefWidth - 17
////        rootPane.widthProperty().addListener { value, old, new ->
////            println("root:${new}")
////            println("msg:${msg.width} ${msg.prefWidth}")
////        }
//        vBox.layoutXProperty().bind(rootPane.widthProperty().subtract(msg.widthProperty()).subtract(17))
//
//        val openPlay = Animations.slideInDown(msg, Duration.millis(250.0))
//        if (!rootPane.children.contains(msg)) {
//            rootPane.children.add(vBox)
//        }
//        openPlay.playFromStart()
    }

    fun showMessage() {
        showMessage("测试")
        showErrMessage("测试")
    }
}
