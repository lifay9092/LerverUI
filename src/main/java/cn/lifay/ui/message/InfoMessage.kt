package cn.lifay.ui.message

import javafx.stage.Window

/**
 *@ClassName InfoMessage
 *@Description TODO
 *@Author lifay
 *@Date 2023/2/12 18:13
 **/
class InfoMessage(owner: Window) : BaseMessage(owner) {
    override fun registerIcon(): String {
        return "info.png"
    }

    override fun baseColor(): String {
        return "#6cc446"
    }

    override fun backgroundColor(): String {
        return "#f3fbeb"
    }
}