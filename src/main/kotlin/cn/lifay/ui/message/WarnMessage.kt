package cn.lifay.ui.message

import javafx.stage.Window

/**
 *@ClassName WarnMessage
 *@Description 警告
 *@Author lifay
 *@Date 2023/2/12 18:13
 **/
class WarnMessage(owner: Window) : BaseMessage(owner) {
    override fun registerIcon(): String {
        return "warn.png"
    }

    override fun baseColor(): String {
        return "#fc9e04"
    }

    override fun backgroundColor(): String {
        return "#fcfce4"
    }
}