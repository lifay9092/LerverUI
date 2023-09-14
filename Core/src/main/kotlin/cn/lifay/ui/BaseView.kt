package cn.lifay.ui

import atlantafx.base.theme.Styles
import atlantafx.base.util.Animations
import cn.lifay.extension.asyncDelayTask
import cn.lifay.extension.backgroundColor
import cn.lifay.extension.bindEscKey
import cn.lifay.extension.platformRun
import cn.lifay.global.GlobalResource
import cn.lifay.logutil.StaticLog
import cn.lifay.ui.message.MsgType
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
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
 * GlobalResource.enableElement(true)
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
    constructor(fxml: URL) : this(){
        val loader = FXMLLoader(fxml)
//        loader.setRoot(rootPane())
        loader.setController(this)
        loader.load<R>()
        ROOT_PANE = loader.getRoot()
        initNotificationPane()
    }

    companion object {

//        /**
//         * 创建新的视图-fxml
//         * @param fxml fxml资源
//         * @param isGlobalResource 是否跟随GlobalResource样式
//         */
//        fun <T : BaseView<R>, R : Pane> createView(fxml: URL, isGlobalResource: Boolean = true): T {
//            val loader = FXMLLoader(fxml)
//            var load = loader.load<R>()
//            return loader.getController<T?>().apply {
//                ROOT_PANE = load
//                initNotificationPane()
//            }
//        }

        /**
         * 创建新的视图-fxml
         * @param fxml fxml资源
         * @param isGlobalResource 是否跟随GlobalResource样式
         * @param initFunc 视图初始化后执行的方法
         */
        fun <T : BaseView<R>, R : Pane> createView(
            fxml: URL,
            initFunc: (T.() -> Unit)? = null
        ): T {
            val loader = FXMLLoader(fxml)
            var load = loader.load<R>()
            return loader.getController<T?>().apply {
                ROOT_PANE = load
                initFunc?.let { it() }
                initNotificationPane()
            }
        }

        /**
         * 创建新的视图-rootPane
         * @param rootPane fxml资源
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
         * @param closeFunc 窗口关闭后回调函数，默认null
         * @param initFunc 视图初始化后执行的方法
         */
        fun <T : BaseView<R>, R : Pane> createViewStage(
            title: String,
            fxml: URL,
            closeFunc: (() -> Unit)? = null,
            initFunc: (T.() -> Unit)? = null
        ): Stage {
            val view = createView(fxml, initFunc)
            val scene = Scene(view.getRoot())
            return Stage().apply {
                this.title = title
                this.isResizable = false
                centerOnScreen()
                this.scene = scene
                this.setOnCloseRequest { closeFunc?.let { it() } }
                this.bindEscKey()
                GlobalResource.loadIcon(this)
            }
        }

    }


    init {
        ROOT_PANE = rootPane()

//        //注册方法
//        val clazz = this.javaClass
//        for (method in clazz.declaredMethods) {
//            val receiver = method.getAnnotation(Receiver::class.java)
//            if (receiver != null) {
//                EventBus.add(receiver.id, this, method::invoke)
//            }
//        }
    }

    /**
     * 注册根容器
     */
    abstract fun rootPane(): R
    override fun initialize(p0: URL?, p1: ResourceBundle?) {
    }

    protected fun initNotificationPane() {
//        getRoot().scene.widthProperty().addListener { observableValue, old, new ->
//            println("root:$new")
//            println("msg w: ${MESSAGE_PANE.prefWidth}")
//
//        }
//        println(GlobalResource.SCREEN_WIDTH)
//        println(GlobalResource.MSG_WIDTH)
//        println(ROOT_PANE.children.size)
        NOTIFICATION_PANE.layoutXProperty()
            .bind(ROOT_PANE.widthProperty().subtract(GlobalResource.MSG_WIDTH).subtract(17))
        MESSAGE_PANE.layoutXProperty().bind(ROOT_PANE.widthProperty().divide(2).subtract(GlobalResource.MSG_WIDTH / 2))
        MESSAGE_PANE.backgroundColor(Color.BLACK)
        ROOT_PANE.children.add(NOTIFICATION_PANE)
        ROOT_PANE.children.add(MESSAGE_PANE)
//        println(ROOT_PANE.children.size)

    }

    open fun InitElemntStyle() {

    }

    open fun getRoot(): Parent {
        return rootPane()
    }

    open fun getWindow(): Window? {
        return rootPane().scene.window
    }

//    /**
//     * 发送消息
//     * @param id 接受者ID
//     * @param body 参数值
//     * @author lifay
//     * @return
//     */
//    open fun <T : Event> send(id: String, body: T) {
////        val stackTraceElements = Thread.currentThread().stackTrace
//////        stackTraceElements.forEach { println(it) }
////        val element = stackTraceElements[2]
//////        println(element)
////        if (EventBus.has(element.className, element.methodName)) {
////            throw FXEventBusException("@FXReceiver 函数不能递归循环：class=${element.className} method=${element.methodName}")
////        }
////        asyncTask {
////            EventBus.invoke(id, body)
////        }
//    }


    private fun getMsgStyle(msgType: MsgType): String {
        return when (msgType) {
            MsgType.ACCENT -> {
                Styles.ACCENT
            }

            MsgType.WARNING -> {
                Styles.WARNING
            }

            MsgType.DANGER -> {
                Styles.DANGER
            }

            MsgType.SUCCESS -> {
                Styles.SUCCESS
            }
        }
    }

    /**
     * 弹出系统通知
     * @author lifay
     */
    open fun showNotification(
        message: String,
        msgType: MsgType = MsgType.ACCENT,
        millis: Long = 3000
    ) {
        if (!ROOT_PANE.children.contains(NOTIFICATION_PANE)) {
            StaticLog.error("BaseView实例化方式不对,未执行initNotificationPane")
            return
        }
        val msg = atlantafx.base.controls.Notification(
            message,
            FontIcon(Material2OutlinedAL.INFO)
        )
        msg.styleClass.addAll(getMsgStyle(msgType), Styles.ELEVATED_1)

        val closeFunc = {
            NOTIFICATION_PANE.children.remove(msg)
        }
        msg.setOnClose {
            val closePlay = Animations.slideOutUp(msg, Duration.millis(250.0))
            closePlay.setOnFinished {
                closeFunc()
            }
            closePlay.playFromStart()
        }
        msg.maxWidth = GlobalResource.MSG_WIDTH
        msg.minWidth = GlobalResource.MSG_WIDTH

        if (!NOTIFICATION_PANE.children.contains(msg)) {
            platformRun { NOTIFICATION_PANE.children.add(msg) }
//            if (!rootPane.children.contains(NOTIFICATION_PANE)) {
//                platformRun { rootPane.children.add(NOTIFICATION_PANE) }
//            }
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
    @Synchronized
    open fun showMessage(
        message: String,
        msgType: MsgType = MsgType.ACCENT,
        millis: Long = 3000
    ) {
        val msg = atlantafx.base.controls.Message(
            "提示",
            message,
            FontIcon(Material2OutlinedAL.INFO)
        )
        msg.styleClass.addAll(getMsgStyle(msgType), Styles.ELEVATED_1)

//        if (!ROOT_PANE.children.contains(MESSAGE_PANE)) {
//            platformRun { ROOT_PANE.children.add(MESSAGE_PANE) }
//        }
        val closeFunc = {
            platformRun { MESSAGE_PANE.children.remove(msg) }
        }
        msg.setOnClose {
            val closePlay = Animations.slideOutUp(msg, Duration.millis(250.0))
            closePlay.setOnFinished {
                closeFunc()
            }
            closePlay.playFromStart()
        }
        msg.maxWidth = GlobalResource.MSG_WIDTH
        msg.minWidth = GlobalResource.MSG_WIDTH

        if (!MESSAGE_PANE.children.contains(msg)) {
            platformRun { MESSAGE_PANE.children.add(msg) }
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