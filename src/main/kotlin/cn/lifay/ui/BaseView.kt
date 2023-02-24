package cn.lifay.ui

import cn.lifay.mq.FXEventBusException
import cn.lifay.mq.FXReceiver
import cn.lifay.ui.message.Message
import cn.lifay.ui.message.Notification
import javafx.concurrent.Task
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.scene.Parent
import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.ButtonType
import javafx.scene.layout.Pane
import javafx.stage.StageStyle
import javafx.stage.Window
import java.net.URL
import java.util.*
import java.util.concurrent.CompletableFuture
import kotlin.reflect.KMutableProperty0


/**
 * BaseView 基础控制器视图
 * @sample
 *
 *          1. instance
 *              val indexPane = FXMLLoader.load<Pane>(ResourceUtil.getResource("index.fxml"))
 *          2. instance
 *             val httpToolView = createView<HttpToolController,VBox>(ResourceUtil.getResource("httpTool.fxml")){
 *             initForm(indexController, httpTool.id)
 *         }
 * @author lifay
 * @date 2023/2/23 17:01
 **/
abstract class BaseView<R : Pane>() : Initializable {

    private var ELEMENT_STYLE = false

    companion object {
        fun <T : BaseView<R>, R : Pane> createView(fxml: URL): T {
            val loader = FXMLLoader(fxml)
            var load = loader.load<R>()
            return loader.getController<T?>().apply {
                rootPane().set(load)
            }
        }

        fun <T : BaseView<R>, R : Pane> createView(fxml: URL, initFunc: T.() -> Unit): T {
            val loader = FXMLLoader(fxml)
            var load = loader.load<R>()
            return loader.getController<T?>().apply {
                rootPane().set(load)
                initFunc()
            }
        }
    }

    init {
        //注册方法
        val clazz = this.javaClass
        for (method in clazz.declaredMethods) {
            val fxReceiver = method.getAnnotation(FXReceiver::class.java)
            if (fxReceiver != null) {
//                println(fxReceiver.id)
                /*if (FXReceiver.has(fxReceiver.id,clazz.name)) {
                    throw FXEventBusException("@FXReceiver[${fxReceiver.id}]已经存在")
                }*/
                FXReceiver.add(fxReceiver.id, this, method::invoke)
            }
        }
//        println()
    }

    /**
     * 注册根容器
     */
    protected abstract fun rootPane(): KMutableProperty0<R>

    fun enableElement(b: Boolean) {
        ELEMENT_STYLE = b
    }

    open fun getRoot(): Parent {
        return rootPane().get()

    }

    protected open fun getWindow(): Window? {
        return rootPane().get().scene.window
    }

    /**
     * 发送消息
     * @param id 接受者ID
     * @param args 参数值
     * @author lifay
     * @return
     */
    protected open fun send(id: String, vararg args: Any) {
        val stackTraceElements = Thread.currentThread().stackTrace
//        stackTraceElements.forEach { println(it) }
        val element = stackTraceElements[2]
//        println(element)
        if (FXReceiver.has(element.className, element.methodName)) {
            throw FXEventBusException("@FXReceiver 函数不能循环：class=${element.className} method=${element.methodName}")
        }
        FXReceiver.invoke(id, args)
    }

    /**
     * 弹窗提示
     *
     * @param message 弹窗展示信息
     * @return 接收用户点击按钮类型
     */
    protected open fun alertInfo(message: String, vararg buttonTypes: ButtonType?): Optional<ButtonType> {
        return alert(message, AlertType.INFORMATION, *buttonTypes)
    }

    /**
     * 弹窗警告
     *
     * @param message 弹窗展示信息
     * @return 接收用户点击按钮类型
     */
    protected open fun alertWarn(message: String, vararg buttonTypes: ButtonType?): Optional<ButtonType> {
        return alert(message, AlertType.WARNING, *buttonTypes)
    }

    /**
     * 弹窗报错
     *
     * @param message 弹窗展示信息
     * @return 接收用户点击按钮类型
     */
    protected open fun alertError(message: String, vararg buttonTypes: ButtonType?): Optional<ButtonType> {
        return alert(message, AlertType.ERROR, *buttonTypes)
    }

    /**
     * 弹窗，指定弹窗类型
     *
     * @param message   弹窗提示信息
     * @param alertType 弹窗类型
     * @return 接收用户点击按钮类型
     */
    protected open fun alert(
        message: String,
        alertType: AlertType,
        vararg buttonTypes: ButtonType?
    ): Optional<ButtonType> {
        val alert = Alert(alertType)
        alert.initStyle(StageStyle.TRANSPARENT)
        val scene = alert.dialogPane.scene
        scene.fill = null
        alert.buttonTypes.addAll(*buttonTypes)
        alert.initOwner(getWindow())
        alert.contentText = message
        if (ELEMENT_STYLE) {
            val s = javaClass.getResource("/css/element-ui.css").toExternalForm()
            alert.dialogPane.stylesheets.add(s)
        }
        return alert.showAndWait()
    }


    /**
     * 显示一则默认类型默认延迟的消息
     *
     * @param message 通知信息
     */
    protected open fun showMessage(message: String?) {
        showMessage(message, Message.Type.INFO)
    }

    /**
     * 显示一则指定类型默认延迟的消息
     *
     * @param message 通知信息
     */
    protected open fun showMessage(message: String?, type: Message.Type?) {
        showMessage(message, type, Message.DEFAULT_DELAY)
    }

    /**
     * 显示一则默认类型指定延迟的消息
     *
     * @param message 通知信息
     */
    protected open fun showMessage(message: String?, delay: Long) {
        showMessage(message, Message.Type.INFO, delay)
    }

    /**
     * 显示一则指定类型指定延迟的消息
     *
     * @param message 通知信息
     */
    protected open fun showMessage(message: String?, type: Message.Type?, delay: Long) {
        val root = getRoot()
        if (root !is Pane) {
            System.err.println("owner 必须是 Pane 或其子类")
            return
        }
        Message.show(root as Pane, message, type, delay)
    }

    /**
     * 显示一则默认类型的通知， 用户手动关闭
     *
     * @param message 通知信息
     */
    protected open fun showNotification(message: String?) {
        showNotification(message, Notification.Type.INFO)
    }

    /**
     * 显示一则指定类型的通知，用户手动关闭
     *
     * @param message 通知信息
     * @param type    通知类型
     */
    protected open fun showNotification(message: String?, type: Notification.Type) {
        showNotification(message, type, 0)
    }

    /**
     * 显示一则指定类型的通知，自动关闭，默认显示一秒
     *
     * @param message 通知信息
     */
    protected open fun showNotificationAutoClose(message: String?) {
        showNotificationAutoClose(message, Notification.Type.INFO)
    }

    protected open fun showNotificationAutoClose(message: String?, type: Notification.Type) {
        showNotification(message, type, Notification.DEFAULT_DELAY)
    }

    /**
     * 显示一则指定类型的通知，自动关闭，指定显示时间
     *
     * @param message      通知信息
     * @param type         通知类型
     * @param milliseconds 延迟时间 毫秒
     */
    protected open fun showNotification(message: String?, type: Notification.Type, milliseconds: Long) {
        val root = getRoot()
        if (root !is Pane) {
            System.err.println("owner 必须是 Pane 或其子类")
            return
        }
        Notification.showAutoClose(root as Pane, message, type, milliseconds)
    }

    /**
     * 异步执行任务
     *
     * @param task 任务
     */
    protected open fun runAsync(task: Task<*>?) {
        CompletableFuture.runAsync(task)
    }
}