package cn.lifay

import atlantafx.base.theme.PrimerLight
import cn.lifay.ui.GlobeTheme
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.layout.Pane
import javafx.stage.Stage

/*
 * Demo 测试类
 *
 * @author lifay
 * @date 2023/1/9 16:07
 */
class CommonDemo : Application() {
    @Throws(Exception::class)
    override fun start(primaryStage: Stage) {
        GlobeTheme.enableElement(true)
        setUserAgentStylesheet(PrimerLight().userAgentStylesheet)

        val fxmlLoader = FXMLLoader(CommonDemo::class.java.getResource("demo.fxml"))
        val root = fxmlLoader.load<Pane>()
        val scene = Scene(root)
        primaryStage.title = "Hello World"
        primaryStage.scene = scene
        primaryStage.show()
    }
}