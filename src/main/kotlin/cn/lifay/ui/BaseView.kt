package cn.lifay.ui

import atlantafx.base.theme.Styles
import atlantafx.base.util.Animations
import cn.lifay.extension.asyncDelayTask
import cn.lifay.extension.asyncTask
import cn.lifay.extension.bindEscKey
import cn.lifay.extension.platformRun
import cn.lifay.mq.FXEventBusException
import cn.lifay.mq.FXEventBusOpt
import cn.lifay.mq.Receiver
import cn.lifay.mq.event.Event
import cn.lifay.ui.message.MsgType
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.stage.Stage
import javafx.stage.Window
import javafx.util.Duration
import org.kordamp.ikonli.javafx.FontIcon
import org.kordamp.ikonli.material2.Material2OutlinedAL
import java.net.URL
import java.util.*


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

    var ROOT_PANE: R


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
         * 创建新的视图-fxml
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
                ROOT_PANE = load
                initNotificationPane()
            }
        }

        /**
         * 创建新的视图-fxml
         * @param fxml fxml资源
         * @param isGlobeTheme 是否跟随GlobeTheme样式
         * @param initFunc 视图初始化后执行的方法
         */
        fun <T : BaseView<R>, R : Pane> createView(
            fxml: URL,
            isGlobeTheme: Boolean = true,
            initFunc: (T.() -> Unit)? = null
        ): T {
            val loader = FXMLLoader(fxml)
            var load = loader.load<R>()
            if (isGlobeTheme && GlobeTheme.ELEMENT_STYLE) {
//                load.stylesheets.add(GlobeTheme.CSS_RESOURCE)
            }
            return loader.getController<T?>().apply {
                ROOT_PANE = load
                initFunc?.let { it() }
                initNotificationPane()
            }
        }

        /**
         * 创建新的视图-rootPane
         * @param fxml fxml资源
         * @param isGlobeTheme 是否跟随GlobeTheme样式
         * @param initFunc 视图初始化后执行的方法
         */
        fun <R : Pane> createView(rootPane: R): BaseView<R> {
            val baseView = object : BaseView<R>() {
                /**
                 * 注册根容器
                 */
                override fun rootPane(): R {
                    return rootPane
                }
            }
            baseView.initNotificationPane()
            return baseView
        }


        /**
         * 创建新的视图fxml、Stage窗口 并返回Stage
         * @param fxml fxml资源
         * @param isGlobeTheme 是否跟随GlobeTheme样式
         * @param closeFunc 窗口关闭后回调函数，默认null
         * @param initFunc 视图初始化后执行的方法
         */
        fun <T : BaseView<R>, R : Pane> createViewStage(
            title: String,
            fxml: URL, isGlobeTheme: Boolean = true,
            closeFunc: (() -> Unit)? = null,
            initFunc: (T.() -> Unit)? = null
        ): Stage {
            val view = createView(fxml, isGlobeTheme, initFunc)
            val scene = Scene(view.getRoot())
            return Stage().apply {
                this.title = title
                this.isResizable = false
                this.scene = scene
                this.setOnCloseRequest { closeFunc?.let { it() } }
                this.bindEscKey()
                GlobeTheme.loadIcon(this)
            }
        }

    }


    init {
        ROOT_PANE = rootPane()

        //注册方法
        val clazz = this.javaClass
        for (method in clazz.declaredMethods) {
            val receiver = method.getAnnotation(Receiver::class.java)
            if (receiver != null) {
                FXEventBusOpt.add(receiver.id, this, method::invoke)
            }
        }
    }

    /**
     * 注册根容器
     */
    abstract fun rootPane(): R
    override fun initialize(p0: URL?, p1: ResourceBundle?) {
        if (GlobeTheme.ELEMENT_STYLE) {
//            getRoot().stylesheets.add(GlobeTheme.CSS_RESOURCE)
        }
    }

    protected fun initNotificationPane() {
//        getRoot().scene.widthProperty().addListener { observableValue, old, new ->
//            println("root:$new")
//            println("msg w: ${MESSAGE_PANE.prefWidth}")
//
//        }
        NOTIFICATION_PANE.layoutXProperty().bind(rootPane().widthProperty().subtract(400.0).subtract(17))
        MESSAGE_PANE.layoutXProperty().bind(rootPane().widthProperty().divide(2).subtract(200))
    }

    open fun InitElemntStyle() {

    }

    open fun getRoot(): Parent {
        return rootPane()

    }

    open fun getWindow(): Window? {
        return rootPane().scene.window
    }

    /**
     * 发送消息
     * @param id 接受者ID
     * @param body 参数值
     * @author lifay
     * @return
     */
    open fun <T : Event> send(id: String, body: T) {
        val stackTraceElements = Thread.currentThread().stackTrace
//        stackTraceElements.forEach { println(it) }
        val element = stackTraceElements[2]
//        println(element)
        if (FXEventBusOpt.has(element.className, element.methodName)) {
            throw FXEventBusException("@FXReceiver 函数不能递归循环：class=${element.className} method=${element.methodName}")
        }
        asyncTask {
            FXEventBusOpt.invoke(id, body)
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
//    open fun showNotification(message: String) {
//        showNotification(message)
//    }

    /**
     * 弹出系统通知
     * @author lifay
     */
    open fun showNotification(
        message: String,
        msgType: MsgType = MsgType.ACCENT,
        millis: Long = 3000
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
        val rootPane = rootPane()

        val closeFunc = {
            NOTIFICATION_PANE.children.remove(msg)
            if (NOTIFICATION_PANE.children.size == 0) {
                platformRun { rootPane.children.remove(NOTIFICATION_PANE) }
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
                platformRun { rootPane.children.add(NOTIFICATION_PANE) }
            }
        }
        val openPlay = Animations.slideInDown(msg, Duration.millis(250.0))

        openPlay.playFromStart()
        if (millis != 0L) {
            asyncDelayTask(millis) {
                platformRun { closeFunc() }
            }
        }
    }


    /**
     * 弹出错误提示
     * @author lifay
     */
    open fun showErrMessage(message: String) {
        showMessage(message, MsgType.DANGER)
    }

    /**
     * 弹出提示
     * @author lifay
     */
    open fun showMessage(
        message: String,
        msgType: MsgType = MsgType.ACCENT,
        millis: Long = 3000
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
        val rootPane = rootPane()

        val closeFunc = {
            MESSAGE_PANE.children.remove(msg)
            if (MESSAGE_PANE.children.size == 0) {
                platformRun { rootPane.children.remove(MESSAGE_PANE) }
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
                platformRun { rootPane.children.add(MESSAGE_PANE) }
            }
        }
        val openPlay = Animations.slideInDown(msg, Duration.millis(250.0))

        openPlay.playFromStart()
        if (millis != 0L) {
            asyncDelayTask(millis) {
                platformRun { closeFunc() }
            }
        }
    }

    /*树视图部分*/


}