package cn.lifay.test

import cn.lifay.GlobeStartUp
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage

/**
 * FormUITest TODO
 *
 * @author lifay
 * @date 2023/2/24 17:19
 */
object FormUITest {

    @JvmStatic
    fun run() {
        GlobeStartUp.launch {
            val primaryStage = Stage()
            val fxmlLoader = FXMLLoader(FormUITest::class.java.getResource("formTest.fxml"))
            val load = fxmlLoader.load<Parent>()
            val scene = Scene(load)
            primaryStage.title = "Hello World"
            primaryStage.scene = scene
            primaryStage.show()
            primaryStage
        }
    }

}