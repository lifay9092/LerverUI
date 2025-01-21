package cn.lifay.test

import cn.lifay.global.LerverResource
import javafx.application.Application
import javafx.scene.Scene
import javafx.stage.Stage

/*
 * Demo 测试类
 *
 * @author lifay
 * @date 2023/1/9 16:07
 */
class TestBaseViewDemo : Application() {
    @Throws(Exception::class)
    override fun start(primaryStage: Stage) {
        LerverResource.loadTheme()
        val baseView = TestBaseView()
        primaryStage.title = "Hello World"
        primaryStage.scene = Scene(baseView.ROOT_PANE)
        primaryStage.show()
    }
}