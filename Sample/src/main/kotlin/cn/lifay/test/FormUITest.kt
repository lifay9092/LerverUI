package cn.lifay.test

import cn.lifay.application.InitDbApplication
import javafx.application.Application
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
class FormUITest : InitDbApplication() {

    override fun addAppStage(): Stage {
        val primaryStage = Stage()
        val fxmlLoader = FXMLLoader(FormUITest::class.java.getResource("formTest.fxml"))
        val load = fxmlLoader.load<Parent>()
        val scene = Scene(load)
        primaryStage!!.title = "Hello World111"
        primaryStage.scene = scene
        primaryStage.setOnCloseRequest {
            println("GlobeStartUp.launch close window...")
        }
        return primaryStage
    }


}

fun main() {
    Application.launch(FormUITest::class.java)
}