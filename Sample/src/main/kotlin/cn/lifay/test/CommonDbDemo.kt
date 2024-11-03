package cn.lifay.test

import cn.lifay.application.InitDbApplication
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.VBox
import javafx.stage.Stage

/*
 * Demo 测试类
 *
 * @author lifay
 * @date 2023/1/9 16:07
 */
class CommonDbDemo : InitDbApplication() {

    override fun addAppStage(): Stage {

        val pane = VBox(42.0)
        pane.children.add(Button("dasdsadasd"))

        val stage = Stage()
        stage.title = "首页"
        stage.centerOnScreen()
        stage.setOnCloseRequest {
            println("CommonDbDemo close window...")
        }
        stage.scene = Scene(pane)
        return stage
    }
}