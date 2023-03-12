package cn.lifay.ui

import cn.lifay.extension.asyncTask
import cn.lifay.mq.FXEventBusException
import cn.lifay.mq.FXReceiver
import cn.lifay.ui.message.Message
import cn.lifay.ui.message.Notification
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

    companion object {
        val ELEMENT_UI_CSS = this::class.java.getResource("/css/element-ui.css").toExternalForm()

        /**
         * 创建新的视图
         * @param fxml fxml资源
         */
        fun <T : BaseView<R>, R : Pane> createView(fxml: URL): T {
            val loader = FXMLLoader(fxml)
            var load = loader.load<R>()
            if (GlobeTheme.ELEMENT_STYLE) {
                load.stylesheets.add(ELEMENT_UI_CSS)
            }
            return loader.getController<T?>().apply {
                rootPane().set(load)
            }
        }

        /**
         * 创建新的视图
         * @param fxml fxml资源
         * @param initFunc 视图初始化后执行的方法
         */
        fun <T : BaseView<R>, R : Pane> createView(fxml: URL, initFunc: T.() -> Unit): T {
            val loader = FXMLLoader(fxml)
            var load = loader.load<R>()
            if (GlobeTheme.ELEMENT_STYLE) {
                load.stylesheets.add(ELEMENT_UI_CSS)
            }
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
                FXReceiver.add(fxReceiver.id, this, method::invoke)
            }
        }
    }

    /**
     * 注册根容器
     */
    protected abstract fun rootPane(): KMutableProperty0<R>
    override fun initialize(p0: URL?, p1: ResourceBundle?) {
        if (GlobeTheme.ELEMENT_STYLE) {
            getRoot().stylesheets.add(ELEMENT_UI_CSS)
        }
    }

    open fun InitElemntStyle() {

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
        asyncTask {
            FXReceiver.invoke(id, args)
        }
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
     * 弹窗提示
     *
     * @param message 弹窗确认
     * @return 接收用户确认结果
     */
    protected open fun alertConfirmation(message: String): Boolean {
        val buttonType = alert(message, AlertType.CONFIRMATION)
        return buttonType.isPresent && buttonType.get() == ButtonType.OK
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
        val alert = Alert(alertType, message, *buttonTypes).apply {
            initStyle(StageStyle.TRANSPARENT)
            dialogPane.scene.fill = null
            initOwner(getWindow())
            contentText = message
        }
        if (GlobeTheme.ELEMENT_STYLE) {
            val s = javaClass.getResource("/css/element-ui.css").toExternalForm()
            alert.dialogPane.stylesheets.add(s)
        }
        return alert.showAndWait()
    }


    /**
     * 显示一则指定类型默认延迟的消息
     *
     * @param message 通知信息
     */
    protected open fun showMessage(message: String, delay: Long = 2500) {
        showMessage(message, Message.Type.INFO, delay)
    }

    /**
     * 显示一则指定类型指定延迟的消息
     *
     * @param message 通知信息
     */
    @JvmOverloads
    protected open fun showMessage(message: String, type: Message.Type = Message.Type.INFO, delay: Long = 2500) {
        val root = getRoot()
        if (root !is Pane) {
            alertError("root 必须是 Pane 或其子类 : ${root}")
            return
        }
        Message.show(root, message, type, delay)
    }

    protected open fun showNotification(message: String, milliseconds: Long = Notification.DEFAULT_DELAY) {
        showNotification(message, Notification.Type.INFO, milliseconds)
    }

    /**
     * 显示一则指定类型的通知，自动关闭，指定显示时间
     *
     * @param message      通知信息
     * @param type         通知类型
     * @param milliseconds 延迟时间 毫秒
     */
    @JvmOverloads
    protected open fun showNotification(
        message: String,
        type: Notification.Type,
        milliseconds: Long = Notification.DEFAULT_DELAY
    ) {
        val root = getRoot()
        if (root !is Pane) {
            alertError("root 必须是 Pane 或其子类 : ${root}")
            return
        }
        Notification.showAutoClose(root, message, type, milliseconds)
    }


    /*树视图部分*/



}