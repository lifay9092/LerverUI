package cn.lifay.ui

import cn.lifay.global.GlobalResource
import javafx.application.Platform
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.stage.Window

/**
 * LoadingUI 加载中组件
 * @author lifay
 * @date 2022/10/9 20:26
 */
class LoadingUI(
    val owner: Window,
    val animation: Boolean = false,
    val msg: String = "请耐心等待..."
) {
    private val stage = Stage()
    private val root = StackPane()
    private val messageLb = Label(msg)

    init {
        val loadingView = ImageView(
            Image(if (animation) GlobalResource.loading() else GlobalResource.loadingGray(), 100.0, 100.0, true, true)
        )
        messageLb.font = Font.font(20.0)

        root.isMouseTransparent = true
        root.setPrefSize(owner.width, owner.height)
        root.background = Background(BackgroundFill(Color.rgb(0, 0, 0, 0.3), null, null))
        root.children.addAll(loadingView, messageLb)
        val scene = Scene(root)
        scene.fill = Color.TRANSPARENT

        stage.scene = scene
        stage.isResizable = false
        stage.initOwner(owner)
        stage.initStyle(StageStyle.TRANSPARENT)
        stage.initModality(Modality.APPLICATION_MODAL)
       // stage.icons.addAll(owner.icons)
        stage.x = owner.x
        stage.y = owner.y
        stage.height = owner.height
        stage.width = owner.width
    }

    // 显示
    fun show() {
        Platform.runLater { stage.show() }
    }

    // 关闭
    fun closeStage() {
        Platform.runLater { stage.close() }
    }
}