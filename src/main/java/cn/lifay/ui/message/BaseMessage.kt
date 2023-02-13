package cn.lifay.ui.message

import cn.lifay.Demo
import cn.lifay.extension.appendStyle
import cn.lifay.extension.backgroundColor
import cn.lifay.extension.platformRun
import cn.lifay.extension.textFillColor
import cn.lifay.util.StaticUtil
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.TextArea
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.stage.Window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 *@ClassName BaseMessage
 *@Description 父类-消息提示组件
 *@Author lifay
 *@Date 2023/2/12 18:04
 **/
abstract class BaseMessage(owner: Window) {

    val stage = Stage(StageStyle.UNDECORATED).apply {
        initOwner(owner)
        initModality(Modality.NONE)
    }

    val root = HBox(20.0)

    val scene = Scene(root)
    val text = TextArea().apply {
        appendStyle("-fx-control-inner-background: ${backgroundColor()};")
        appendStyle("-fx-background-color: -fx-control-inner-background;")
        appendStyle("-fx-text-box-border: transparent;")
        this.isEditable = false
        this.isWrapText = true
        this.opacity = 50.0
        this.textFillColor(baseColor())
        this.font = Font.font("serif", FontWeight.BOLD, 16.0)
        this.prefWidth = 400.0
        this.prefHeight = 100.0
    }

    init {
        stage.scene = scene
//        println("w:${owner.scene.widthProperty()}")
//        println("h:${owner.scene.heightProperty()}")
//        println("x:${owner.xProperty()}")
//        println("y:${owner.yProperty()}")

        root.apply {
            alignment = Pos.TOP_CENTER
            backgroundColor(backgroundColor())
            //去除默认焦点
            requestFocus()
            //绑定位置
            children.addAll(
                ImageView(Image(Demo::class.java.getResourceAsStream(StaticUtil.ROOT_PATH_IMG + registerIcon()))),
                text
            )
        }

    }

    abstract fun registerIcon(): String

    abstract fun baseColor(): String

    abstract fun backgroundColor(): String

    fun show(msg: String) {
        CoroutineScope(Dispatchers.Default).launch {
            platformRun {
                text.text = msg
                stage.show()
            }
            delay(3000)
            platformRun {
                stage.close()
            }
        }
    }


}