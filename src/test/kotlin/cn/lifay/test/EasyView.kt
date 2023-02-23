package cn.lifay.test

import xyz.yuelai.View
import xyz.yuelai.control.Notification
import java.net.URL


/**
 * EasyView TODO
 * @author lifay
 * @date 2023/2/21 8:56
 **/
class EasyView : View() {
    override fun fxml(): URL? {
        val resource = this::class.java.getResource("easy.fxml")
        println(resource)
        return resource
    }

    fun test() {
        println("ddddddddddddd")
        showNotification("测试消息")
        showNotification("测试消息2",Notification.Type.SUCCESS)
        showNotification("测试消息3",Notification.Type.ERROR,2000)


        showMessage("showMessage")
    }
}