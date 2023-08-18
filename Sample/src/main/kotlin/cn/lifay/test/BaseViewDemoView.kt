package cn.lifay.test

import cn.lifay.ui.BaseView
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.control.TextField
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.HBox
import java.net.URL
import java.util.*


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

    @FXML
    var notificationText = TextField()

    @FXML
    var messageText = TextField()

    override fun rootPane(): AnchorPane {
        return this.rootPane
    }

    override fun initialize(p0: URL?, p1: ResourceBundle?) {
        super.initialize(p0, p1)
    }

    fun addTab() {
        val tab1 = Tab("2222").apply {
            id = "2222"
            content = HBox(Button("22222"))
        }

        tabPane.tabs.add(tab1)
        println("ddddddddddddd")
    }

    fun showNotification() {
        showNotification(notificationText.text)

    }

    fun showMessage() {
        showMessage(messageText.text)
        showErrMessage("ErrorText:${messageText.text}")
    }
}
