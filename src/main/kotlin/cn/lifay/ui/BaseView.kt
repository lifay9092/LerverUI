package cn.lifay.ui

import atlantafx.base.theme.Styles
import atlantafx.base.util.Animations
import cn.lifay.extension.asyncDelayTask
import cn.lifay.extension.asyncTask
import cn.lifay.extension.platformRun
import cn.lifay.mq.FXEventBusException
import cn.lifay.mq.FXEventBusOpt
import cn.lifay.mq.FXReceiver
import cn.lifay.ui.message.MsgType
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.scene.Parent
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.stage.Window
import javafx.util.Duration
import org.kordamp.ikonli.javafx.FontIcon
import org.kordamp.ikonli.material2.Material2OutlinedAL
import java.net.URL
import java.util.*
import kotlin.reflect.KMutableProperty0


/**
 * BaseView 基础控制器视图
 *
 * 普通创建新的视图
 * ```
 * GlobeTheme.enableElement(true)
 * val view = BaseView.createView<BaseViewDemoView, AnchorPane>(xxx.fxml)
 * val scene = Scene(view.getRoot())
 * primaryStage.title = "Hello World"
 * primaryStage.scene = scene
 * primaryStage.show()
 * ```
 * 从父视图内创建新的视图直接使用[BaseView.createView]
 * @author lifay
 * @date 2023/2/23 17:01
 **/
abstract class BaseView<R : Pane>() : Initializable {

    val NOTIFICATION_PANE = VBox(5.0).apply {
        isManaged = false
        spacing = 15.0
        layoutY = 15.0
    }

    val MESSAGE_PANE = VBox(5.0).apply {
        isManaged = false
        spacing = 15.0
        layoutY = 15.0
    }

    companion object {

        /**
         * 创建新的视图fxml
         * @param fxml fxml资源
         * @param isGlobeTheme 是否跟随GlobeTheme样式
         */
        fun <T : BaseView<R>, R : Pane> createView(fxml: URL, isGlobeTheme: Boolean = true): T {
            val loader = FXMLLoader(fxml)
            var load = loader.load<R>()
            if (isGlobeTheme && GlobeTheme.ELEMENT_STYLE) {
//                load.stylesheets.add(GlobeTheme.CSS_RESOURCE)
            }
            return loader.getController<T?>().apply {
                rootPane().set(load)
                initNotificationPane()
            }
        }

        /**
         * 创建新的视图fxml
         * @param fxml fxml资源
         * @param isGlobeTheme 是否跟随GlobeTheme样式
         * @param initFunc 视图初始化后执行的方法
         */
        fun <T : BaseView<R>, R : Pane> createView(fxml: URL, isGlobeTheme: Boolean = true, initFunc: T.() -> Unit): T {
            val loader = FXMLLoader(fxml)
            var load = loader.load<R>()
            if (isGlobeTheme && GlobeTheme.ELEMENT_STYLE) {
//                load.stylesheets.add(GlobeTheme.CSS_RESOURCE)
            }
            return loader.getController<T?>().apply {
                rootPane().set(load)
                initFunc()
                initNotificationPane()
            }
        }
    }

    init {
        //注册方法
        val clazz = this.javaClass
        for (method in clazz.declaredMethods) {
            val fxReceiver = method.getAnnotation(FXReceiver::class.java)
            if (fxReceiver != null) {
                FXEventBusOpt.add(fxReceiver.id, this, method::invoke)
            }
        }
    }

    /**
     * 注册根容器
     */
    abstract fun rootPane(): KMutableProperty0<R>
    override fun initialize(p0: URL?, p1: ResourceBundle?) {
        if (GlobeTheme.ELEMENT_STYLE) {
//            getRoot().stylesheets.add(GlobeTheme.CSS_RESOURCE)
        }
    }

    private fun initNotificationPane() {
        NOTIFICATION_PANE.layoutXProperty().bind(rootPane().get().widthProperty().subtract(400.0).subtract(17))
        MESSAGE_PANE.layoutXProperty().bind(rootPane().get().widthProperty().divide(2).subtract(200))
    }

    open fun InitElemntStyle() {

    }

    open fun getRoot(): Parent {
        return rootPane().get()

    }

    open fun getWindow(): Window? {
        return rootPane().get().scene.window
    }

    /**
     * 发送消息
     * @param id 接受者ID
     * @param args 参数值
     * @author lifay
     * @return
     */
    open fun send(id: String, vararg args: Any) {
        val stackTraceElements = Thread.currentThread().stackTrace
//        stackTraceElements.forEach { println(it) }
        val element = stackTraceElements[2]
//        println(element)
        if (FXEventBusOpt.has(element.className, element.methodName)) {
            throw FXEventBusException("@FXReceiver 函数不能递归循环：class=${element.className} method=${element.methodName}")
        }
        asyncTask {
            FXEventBusOpt.invoke(id, args)
        }
    }
//
//
//    /**
//     * 显示一则指定类型默认延迟的消息
//     *
//     * @param message 通知信息
//     */
//    open fun showMessage(message: String, delay: Long = 2500) {
//        showMessage(message, Message.Type.INFO, delay)
//    }
//
//    /**
//     * 显示一则指定类型指定延迟的消息
//     *
//     * @param message 通知信息
//     */
//    @JvmOverloads
//    open fun showMessage(message: String, type: Message.Type = Message.Type.INFO, delay: Long = 2500) {
//        val root = getRoot()
//        if (root !is Pane) {
//            alertError("root 必须是 Pane 或其子类 : ${root}")
//            return
//        }
//        Message.show(root, message, type, delay)
//    }
//
//    open fun showNotification(message: String, milliseconds: Long = Notification.DEFAULT_DELAY) {
//        showNotification(message, Notification.Type.INFO, milliseconds)
//    }
//
//    /**
//     * 显示一则指定类型的通知，自动关闭，指定显示时间
//     *
//     * @param message      通知信息
//     * @param type         通知类型
//     * @param milliseconds 延迟时间 毫秒
//     */
//    @JvmOverloads
//    open fun showNotification(
//        message: String,
//        type: Notification.Type,
//        milliseconds: Long = Notification.DEFAULT_DELAY
//    ) {
//        val root = getRoot()
//        if (root !is Pane) {
//            alertError("root 必须是 Pane 或其子类 : ${root}")
//            return
//        }
//        Notification.showAutoClose(root, message, type, milliseconds)
//    }
//
    /**
     * 弹出系统通知
     * @author lifay
     */
    open fun showNotification(message: String, autoClose: Boolean = true) {
        showNotification(MsgType.ACCENT, message, autoClose)
    }

    /**
     * 弹出系统通知
     * @author lifay
     */
    open fun showNotification(
        msgType: MsgType = MsgType.ACCENT,
        message: String,
        autoClose: Boolean = true
    ) {
        val msg = atlantafx.base.controls.Notification(
            message,
            FontIcon(Material2OutlinedAL.HELP_OUTLINE)
        )
        when (msgType) {
            MsgType.ACCENT -> {
                msg.styleClass.addAll(
                    Styles.ACCENT, Styles.ELEVATED_1
                )
            }

            MsgType.WARNING -> {
                msg.styleClass.addAll(
                    Styles.WARNING, Styles.ELEVATED_1
                )
            }

            MsgType.DANGER -> {
                msg.styleClass.addAll(
                    Styles.DANGER, Styles.ELEVATED_1
                )
            }

            MsgType.SUCCESS -> {
                msg.styleClass.addAll(
                    Styles.SUCCESS, Styles.ELEVATED_1
                )
            }
        }
        val rootPane = rootPane().get()

        val closeFunc = {
            NOTIFICATION_PANE.children.remove(msg)
            if (NOTIFICATION_PANE.children.size == 0) {
                rootPane.children.remove(NOTIFICATION_PANE)
            }
        }
        msg.setOnClose {
            val closePlay = Animations.slideOutUp(msg, Duration.millis(250.0))
            closePlay.setOnFinished {
                closeFunc()
            }
            closePlay.playFromStart()
        }
        msg.maxWidth = 400.0
        msg.minWidth = 400.0

        if (!NOTIFICATION_PANE.children.contains(msg)) {
            NOTIFICATION_PANE.children.add(msg)
            if (!rootPane.children.contains(NOTIFICATION_PANE)) {
                rootPane.children.add(NOTIFICATION_PANE)
            }
        }
        val openPlay = Animations.slideInDown(msg, Duration.millis(250.0))

        openPlay.playFromStart()
        if (autoClose) {
            asyncDelayTask(3000) {
                platformRun { closeFunc() }
            }
        }
    }

    /**
     * 弹出提示
     * @author lifay
     */
    open fun showMessage(message: String) {
        showMessage(MsgType.ACCENT, message)
    }

    /**
     * 弹出错误提示
     * @author lifay
     */
    open fun showErrMessage(message: String) {
        showMessage(MsgType.DANGER, message)
    }

    /**
     * 弹出提示
     * @author lifay
     */
    open fun showMessage(
        msgType: MsgType,
        message: String,
        autoClose: Boolean = true
    ) {
        val msg = atlantafx.base.controls.Message(
            "提示",
            message,
            FontIcon(Material2OutlinedAL.HELP_OUTLINE)
        )
        when (msgType) {
            MsgType.ACCENT -> {
                msg.styleClass.addAll(
                    Styles.ACCENT, Styles.ELEVATED_1
                )
            }

            MsgType.WARNING -> {
                msg.styleClass.addAll(
                    Styles.WARNING, Styles.ELEVATED_1
                )
            }

            MsgType.DANGER -> {
                msg.styleClass.addAll(
                    Styles.DANGER, Styles.ELEVATED_1
                )
            }

            MsgType.SUCCESS -> {
                msg.styleClass.addAll(
                    Styles.SUCCESS, Styles.ELEVATED_1
                )
            }
        }
        val rootPane = rootPane().get()

        val closeFunc = {
            MESSAGE_PANE.children.remove(msg)
            if (MESSAGE_PANE.children.size == 0) {
                rootPane.children.remove(MESSAGE_PANE)
            }
        }
        msg.setOnClose {
            val closePlay = Animations.slideOutUp(msg, Duration.millis(250.0))
            closePlay.setOnFinished {
                closeFunc()
            }
            closePlay.playFromStart()
        }
        msg.maxWidth = 400.0
        msg.minWidth = 400.0

        if (!MESSAGE_PANE.children.contains(msg)) {
            MESSAGE_PANE.children.add(msg)
            if (!rootPane.children.contains(MESSAGE_PANE)) {
                rootPane.children.add(MESSAGE_PANE)
            }
        }
        val openPlay = Animations.slideInDown(msg, Duration.millis(250.0))

        openPlay.playFromStart()
        if (autoClose) {
            asyncDelayTask(3000) {
                platformRun { closeFunc() }
            }
        }
    }

    /*树视图部分*/


}