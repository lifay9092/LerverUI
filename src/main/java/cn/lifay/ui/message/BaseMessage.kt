package cn.lifay.ui.message

import cn.lifay.Demo
import cn.lifay.extension.backgroundColor
import cn.lifay.extension.textFillColor
import cn.lifay.util.StaticUtil
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.stage.Window

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
    val text = TextField().apply {
        this.backgroundColor(backgroundColor())
        this.isEditable = false
        this.textFillColor(baseColor())
        this.font = Font.font("serif", FontWeight.BOLD, 12.0)
    }

    var TIMES = 200L

    constructor(owner: Window, times: Long) : this(owner) {
        this.TIMES = times
    }

    init {
        stage.scene = scene

        root.apply {
            alignment = Pos.CENTER
            backgroundColor(backgroundColor())
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
        text.text = msg
        stage.show()
    }

}