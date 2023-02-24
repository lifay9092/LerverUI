/*
package cn.lifay.test

import cn.lifay.mq.FXReceiver
import cn.lifay.ui.BaseView
import cn.lifay.ui.message.Notification
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import java.net.URL
import java.util.*


*/
/**
 * EasyView TODO
 * @author lifay
 * @date 2023/2/21 8:56
 **//*

class EasyView : BaseView() {
    @FXML
    var rootPane = AnchorPane()

    @FXML
    var tabPane = TabPane()
    override fun rootPane(): Pane {
        return rootPane
    }

    override fun initialize(p0: URL?, p1: ResourceBundle?) {
        println("initialize")
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
        val resource = this::class.java.getResource("easy.fxml")
        println(resource)
        return resource
    }

    fun test() {
        */
/*     val tab1 = Tab("2222").apply {
                 id = "2222"
                 content = HBox(Button("22222"))
             }*//*

//        tabPane.tabs.add(tab1)
        println("ddddddddddddd")
        //  showNotification("测试消息")
        //  showNotification("测试消息2", Notification.Type.SUCCESS)
        // showNotification("测试消息3",Notification.Type.ERROR,2000)


        showMessage("showMessage")
    }

    fun msgTest() {
        val tab1 = Tab("3333").apply {
            id = "3333"
            content = HBox(Button("33333"))
        }
        tabPane.tabs.add(tab1)
        println(tabPane.tabs.size)
        println("ddddddddddddd")

        showNotification("测试消息3", Notification.Type.ERROR, 2000)
        */
/*  showNotification("测试消息")
          showNotification("测试消息2",Notification.Type.SUCCESS)
          showNotification("测试消息3",Notification.Type.ERROR,2000)


          showMessage("showMessage")*//*

    }

    fun sendTest() {
        send("mq", "ddddddddddd")
    }

    @FXReceiver("mq")
    fun mq(text: String) {
        println("text:$text")
    }
}*/
