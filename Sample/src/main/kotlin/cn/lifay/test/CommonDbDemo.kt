package cn.lifay.test

import atlantafx.base.theme.PrimerLight
import cn.lifay.application.InitDbApplication
import cn.lifay.global.GlobalResource
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
class CommonDbDemo : InitDbApplication(
    appTheme = PrimerLight(),
    appConfigPath = GlobalResource.USER_DIR + "lerver.yml",
    appLogPrefix = "test"
) {

    override fun addIndexStage(): Stage {

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