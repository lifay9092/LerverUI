package cn.lifay.test

import atlantafx.base.theme.PrimerDark
import cn.lifay.global.BaseApplication
import cn.lifay.global.GlobalResource
import cn.lifay.ui.BaseView
import javafx.scene.Scene
import javafx.scene.layout.AnchorPane
import javafx.stage.Stage
import java.io.File

/*
 * Demo 测试类
 *
 * @author lifay
 * @date 2023/1/9 16:07
 */
class CommonDemo : BaseApplication(
    theme = PrimerDark(),
    configPath = GlobalResource.USER_DIR + "test" + File.separator + "cc.config",
    logPrefix = "test"
) {
    @Throws(Exception::class)
    override fun start(primaryStage: Stage) {
        println("首页")
        val view = BaseView.createView<CommonDemoView, AnchorPane>(CommonDemo::class.java.getResource("demo.fxml"))
//        val fxmlLoader = FXMLLoader(CommonDemo::class.java.getResource("demo.fxml"))
//        val root = fxmlLoader.load<Pane>()
        val scene = Scene(view.ROOT_PANE)
        primaryStage.title = "Hello World"
        primaryStage.scene = scene
        primaryStage.show()
    }
}