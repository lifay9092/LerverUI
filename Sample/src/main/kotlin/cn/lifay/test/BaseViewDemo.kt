package cn.lifay.test

import cn.lifay.global.LerverResource
import cn.lifay.ui.BaseView
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.layout.AnchorPane
import javafx.stage.Stage

/*
 * Demo 测试类
 *
 * @author lifay
 * @date 2023/1/9 16:07
 */
class BaseViewDemo : Application() {
    @Throws(Exception::class)
    override fun start(primaryStage: Stage) {
        LerverResource.loadTheme()

        val view =
            BaseView.createView<BaseViewDemoView, AnchorPane>(BaseViewDemo::class.java.getResource("baseViewDemo.fxml"))
        val scene = Scene(view.getRoot())
        primaryStage.title = "Hello World"
        primaryStage.scene = scene
        primaryStage.show()
    }
}