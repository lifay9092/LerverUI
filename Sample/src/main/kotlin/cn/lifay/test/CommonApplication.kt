package cn.lifay.test

import cn.lifay.application.BaseApplication
import cn.lifay.ui.BaseView
import cn.lifay.view.CommonView
import javafx.scene.Scene
import javafx.scene.layout.AnchorPane
import javafx.stage.Stage

/*
 * Common 测试类
 *
 * @author lifay
 * @date 2023/1/9 16:07
 */
class CommonApplication : BaseApplication() {

    override fun start(primaryStage: Stage) {
        //AnchorPane跟CommonView中的Pane类型一致
        val view = BaseView.createView<CommonView, AnchorPane>(
            CommonApplication::class.java.getResource("common.fxml")
        )
        val scene = Scene(view.ROOT_PANE)
        primaryStage.title = "Hello World"
        primaryStage.scene = scene
        primaryStage.setOnCloseRequest {
            println("close...")
        }
        primaryStage.show()
    }
}