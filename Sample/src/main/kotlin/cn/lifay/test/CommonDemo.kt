package cn.lifay.test

import cn.lifay.application.InitDbApplication
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
class CommonDemo : InitDbApplication(
//    configPath = GlobalResource.USER_DIR + "lerver.yml",
//    logPrefix = "test"
) {
    /*    @Throws(Exception::class)
    override fun start(primaryStage: Stage) {
//        GlobalConfig.WriteProperties("test1", "实时")
        println(GlobalConfig.ReadProperties("test2", "实时-默认"))
        val view = BaseView.createView<CommonDemoView, AnchorPane>(CommonDemo::class.java.getResource("demo.fxml"))
//        val fxmlLoader = FXMLLoader(CommonDemo::class.java.getResource("demo.fxml"))
//        val root = fxmlLoader.load<Pane>()
        val scene = Scene(view.ROOT_PANE)
        primaryStage.title = "Hello World"
        primaryStage.scene = scene

        primaryStage.onCloseRequest.handle(WindowEvent(primaryStage.owner, EventType(WindowEvent.WINDOW_CLOSE_REQUEST)))

//        primaryStage.setOnCloseRequest { e ->
//            println("close")
//        }
//        primaryStage.onCloseRequestProperty().addListener { ov ->
//            println("onCloseRequestProperty")
//        }
        primaryStage.show()

    }*/
    override fun addAppStage(): Stage {
        val primaryStage = Stage()
        val view = BaseView.createView<CommonDemoView, AnchorPane>(CommonDemo::class.java.getResource("demo.fxml"))
        val scene = Scene(view.ROOT_PANE)
        primaryStage.title = "Hello World"
        primaryStage.scene = scene
        primaryStage.setOnCloseRequest {
            println("close...")
        }
        return primaryStage
    }

//
//    override fun start(primaryStage: Stage?) {
//        AppManage.loadAppConfig(AppManage())
//        val view = BaseView.createView<CommonDemoView, AnchorPane>(CommonDemo::class.java.getResource("demo.fxml"))
//        val scene = Scene(view.ROOT_PANE)
//        primaryStage!!.title = "Hello World"
//        primaryStage.scene = scene
//        primaryStage.setOnCloseRequest {
//            println("close...")
//        }
//        primaryStage.show()
//    }


}