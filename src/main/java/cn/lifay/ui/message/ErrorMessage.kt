package cn.lifay.ui.message

import javafx.stage.Window

/**
 *@ClassName ErrorMessage
 *@Description 错误
 *@Author lifay
 *@Date 2023/2/12 18:13
 **/
class ErrorMessage(owner: Window) : BaseMessage(owner) {
    override fun registerIcon(): String {
        return "error.png"
    }

    override fun baseColor(): String {
        return "#ed4814"
    }

    override fun backgroundColor(): String {
        return "#fcece4"
    }
}