package cn.lifay.test

import atlantafx.base.theme.PrimerLight
import cn.lifay.global.BaseApplication
import cn.lifay.global.GlobalConfig
import cn.lifay.global.GlobalResource
import cn.lifay.ui.BaseView
import javafx.scene.Scene
import javafx.scene.layout.AnchorPane
import javafx.stage.Stage

/*
 * Demo 测试类
 *
 * @author lifay
 * @date 2023/1/9 16:07
 */
class CommonDemo : BaseApplication(
    theme = PrimerLight(),
    configPath = GlobalResource.USER_DIR + "lerver.yml",
    logPrefix = "test"
) {
    @Throws(Exception::class)
    override fun start(primaryStage: Stage) {
        GlobalConfig.WriteProperties("test1", "实时")
        println(GlobalConfig.ReadProperties("test2", "实时-默认"))
        val view = BaseView.createView<CommonDemoView, AnchorPane>(CommonDemo::class.java.getResource("demo.fxml"))
//        val fxmlLoader = FXMLLoader(CommonDemo::class.java.getResource("demo.fxml"))
//        val root = fxmlLoader.load<Pane>()
        val scene = Scene(view.ROOT_PANE)
        primaryStage.title = "Hello World"
        primaryStage.scene = scene
        primaryStage.show()
    }
}