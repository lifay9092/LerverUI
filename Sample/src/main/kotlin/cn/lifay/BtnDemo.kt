package cn.lifay

import atlantafx.base.theme.PrimerLight
import atlantafx.base.theme.Styles
import cn.lifay.db.DbManage
import cn.lifay.global.GlobalResource
import javafx.application.Application
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
class BtnDemo : Application() {
    @Throws(Exception::class)
    override fun start(primaryStage: Stage) {
        DbManage.Init()
        // GlobalResource.enableElement(true)
        GlobalResource.loadTheme(PrimerLight())
        var normalBtn = Button("_Normal");
        normalBtn.setMnemonicParsing(true);

        var defaultBtn = Button("_Default");
        defaultBtn.setDefaultButton(true);
        defaultBtn.setMnemonicParsing(true);

        var successBtn = Button("_SUCCESS");
        successBtn.getStyleClass().add(Styles.SUCCESS);

        var outlinedBtn = Button("Out_lined");
        outlinedBtn.getStyleClass().addAll(Styles.BUTTON_OUTLINED);
        outlinedBtn.setMnemonicParsing(true);

        var flatBtn = Button("_Flat");
        flatBtn.getStyleClass().add(Styles.FLAT);


        val vBox = VBox(10.0)
        vBox.children.addAll(normalBtn, defaultBtn, successBtn, outlinedBtn, flatBtn)

        val scene = Scene(vBox)
        primaryStage.title = "Hello World"
        primaryStage.scene = scene
        primaryStage.show()
    }
}