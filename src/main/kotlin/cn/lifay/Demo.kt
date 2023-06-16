package cn.lifay

import cn.lifay.db.DbManage
import cn.lifay.ui.GlobeTheme
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.stage.Stage

/*
 * Demo 测试类
 *
 * @author lifay
 * @date 2023/1/9 16:07
 */
class Demo : Application() {
    @Throws(Exception::class)
    override fun start(primaryStage: Stage) {
        DbManage.Init()
        GlobeTheme.enableElement(true)

        val scene = Scene(VBox(Button("测试")))
        primaryStage.title = "Hello World"
        primaryStage.scene = scene
        primaryStage.show()
    }
}