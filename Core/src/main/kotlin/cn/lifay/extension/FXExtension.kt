package cn.lifay.extension

import atlantafx.base.theme.Styles
import cn.lifay.exception.LerverUIException
import cn.lifay.ui.LoadingUI
import cn.lifay.ui.form.BaseFormUI
import cn.lifay.ui.form.btn.BaseButton
import javafx.application.Platform
import javafx.css.PseudoClass
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.stage.Stage
import javafx.stage.Window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.kordamp.ikonli.feather.Feather
import org.kordamp.ikonli.javafx.FontIcon
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.util.*

/**
 * 检查参数
 * @param name 名称
 * @param value 值
 * @author lifay
 * @return
 */
fun checkParam(name: String, value: Any?): Boolean {
    if (value == null || value.toString().isBlank()) {
        platformRun { Alert(Alert.AlertType.ERROR, "${name}不能为空", ButtonType.CLOSE).show() }
        return false
    }
    return true
}


/**
 * 异步执行耗时操作
 * @author lifay
 * @return
 */
inline fun asyncTask(
    crossinline block: () -> Unit
) {
    CoroutineScope(Dispatchers.Default).launch {
        try {
            //执行任务
            block()
        } catch (e: LerverUIException) {
            e.printStackTrace()
        }
    }
}

/**
 * 异步延迟执行耗时操作 时间：毫秒
 * @author lifay
 * @return
 */
inline fun asyncDelayTask(
    time: Long,
    crossinline block: () -> Unit
) {
    CoroutineScope(Dispatchers.Default).launch {
        try {
            delay(time)
            //执行任务
            block()
        } catch (e: LerverUIException) {
            e.printStackTrace()
        }
    }
}

/**
 * 异步执行耗时操作,同时有加载提示
 * @author lifay
 * @return
 */
inline fun asyncTaskLoading(
    owner: Window,
    msg: String = "请耐心等待...",
    animation: Boolean = false,
    crossinline block: () -> Unit
) {
    val loadingUI = LoadingUI(owner, animation = animation, msg = msg)
    CoroutineScope(Dispatchers.Default).launch {
        try {
            platformRun { loadingUI.show() }
            //执行任务
            block()
        } catch (e: LerverUIException) {
            e.printStackTrace()
        } finally {
            platformRun { loadingUI.closeStage() }
        }
    }
}

fun platformRun(f: Runnable) {
    Platform.runLater(f)
}

inline fun platformRun(crossinline f: () -> Unit) {
    Platform.runLater { f() }
}

fun <T : Node> T.appendStyle(styleStr: String): T {
    val styles = this.style.split(";")
    val list = styles.filter { it.isNotBlank() }.map { it.replace(";", "") }.toMutableList()
    list.add(if (styleStr.endsWith(";")) ("$styleStr;") else styleStr)
    this.style = list.joinToString(";")
    return this
}

fun <T : Node> T.backgroundColor(color: String): T {
    return this.appendStyle("-fx-background-color: ${color};")
}

fun <T : Node> T.backgroundColor(color: Color): T {
    return this.backgroundColor(color.toWeb())
}

fun <T : Node> T.textFillColor(color: String): T {
    return this.appendStyle("-fx-text-fill: ${color};")
}

fun <T : Node> T.textFillColor(color: Color): T {
    return this.textFillColor(color.toWeb())
}

fun <T : Node> T.borderColor(color: String): T {
    return this.appendStyle("-fx-border-color: ${color};")
}

fun <T : Node> T.borderColor(color: Color): T {
    return this.borderColor(color.toWeb())
}

fun Color.toWeb(): String {
    val red = (this.red * 255).toInt()
    val green = (this.green * 255).toInt()
    val blue = (this.blue * 255).toInt()
    //    println("R:${red} G:${green} B:${blue} $web")
    return String.format("#%02X%02X%02X", red, green, blue)
}

fun Button.styleInfo(): Button {
    //this.backgroundColor("#5264AE")
    // this.textFill = Color.WHITE
    this.styleClass.add(Styles.ACCENT)
    return this
}

fun Button.stylePrimary(): Button {
    //  this.backgroundColor("#2c8cf4")
    // this.textFill = Color.WHITE
    this.styleClass.addAll(Styles.ACCENT)
    return this
}

fun Button.styleSuccess(): Button {
    //  this.backgroundColor("#1cbc6c")
    //  this.textFill = Color.WHITE
    this.styleClass.addAll(Styles.SUCCESS)
    return this
}

fun Button.styleWarn(): Button {
    //  this.backgroundColor("#fc9c04")
    //  this.textFill = Color.WHITE
    this.styleClass.addAll(Styles.WARNING)
    return this
}

fun Button.styleDanger(): Button {
    //  this.backgroundColor("#ec4414")
    // this.textFill = Color.WHITE
    this.styleClass.addAll(Styles.DANGER)
    return this
}

fun Button.outline(): Button {
    this.styleClass.addAll(Styles.BUTTON_OUTLINED)
    return this
}

fun Button.icon(icon: Feather): Button {
    //  this.backgroundColor("#ec4414")
    // this.textFill = Color.WHITE
    this.graphic = FontIcon(icon)
    return this
}

/**
 * 文本框变色 例如：Styles.STATE_DANGER
 * @param pseudoClass 颜色
 * @author lifay
 */
fun <T : TextInputControl> T.changeColor(pseudoClass: PseudoClass): T {
    this.pseudoClassStateChanged(pseudoClass, true)
    return this
}

fun Stage.bindEscKey(): Stage {
    addEventHandler(KeyEvent.KEY_PRESSED) {
        if (it.code == KeyCode.ESCAPE) {
            close()
        }
    }
    return this
}

/**
 * 弹窗提示
 *
 * @param message 弹窗展示信息
 * @return 接收用户点击按钮类型
 */
inline fun alertInfo(message: String, vararg buttonTypes: ButtonType?): Optional<ButtonType> {
    return alertInfo("信息提示框", message, *buttonTypes)
}

/**
 * 弹窗提示
 *
 * @param message 弹窗展示信息
 * @return 接收用户点击按钮类型
 */
inline fun alertInfo(title: String, message: String, vararg buttonTypes: ButtonType?): Optional<ButtonType> {
    return alert(title, message, Alert.AlertType.INFORMATION, *buttonTypes)
}

/**
 * 弹窗提示
 *
 * @param message 弹窗确认
 * @return 接收用户确认结果
 */
inline fun alertConfirmation(message: String): Boolean {
    val buttonType = alert("确认提示框", message, Alert.AlertType.CONFIRMATION)
    return buttonType.isPresent && buttonType.get() == ButtonType.OK
}

/**
 * 弹窗提示
 *
 * @param message 弹窗确认
 * @return 接收用户确认结果
 */
inline fun alertConfirmation(title: String, message: String): Boolean {
    val buttonType = alert(title, message, Alert.AlertType.CONFIRMATION)
    return buttonType.isPresent && buttonType.get() == ButtonType.OK
}

/**
 * 弹窗警告
 *
 * @param message 弹窗展示信息
 * @return 接收用户点击按钮类型
 */
inline fun alertWarn(message: String, vararg buttonTypes: ButtonType?): Optional<ButtonType> {
    return alertWarn("警告提示框", message, *buttonTypes)
}

/**
 * 弹窗警告
 *
 * @param message 弹窗展示信息
 * @return 接收用户点击按钮类型
 */
inline fun alertWarn(title: String, message: String, vararg buttonTypes: ButtonType?): Optional<ButtonType> {
    return alert(title, message, Alert.AlertType.WARNING, *buttonTypes)
}

/**
 * 弹窗报错
 *
 * @param head 弹窗展示信息
 * @param message 弹窗展示信息
 * @return 接收用户点击按钮类型
 */
inline fun alertError(head: String, message: String = head, vararg buttonTypes: ButtonType?): Optional<ButtonType> {
    return alertError("错误提示框", head, message, *buttonTypes)
}

/**
 * 弹窗报错
 *
 * @param head 弹窗展示信息
 * @param message 弹窗展示信息
 * @return 接收用户点击按钮类型
 */
inline fun alertError(
    title: String,
    head: String,
    message: String = head,
    vararg buttonTypes: ButtonType?
): Optional<ButtonType> {
    return alertDetail(title, head, message, Alert.AlertType.ERROR, false, *buttonTypes)
}

/**
 * 弹窗，指定弹窗类型
 *
 * @param message   弹窗提示信息
 * @param alertType 弹窗类型
 * @return 接收用户点击按钮类型
 */
inline fun alert(
    title: String,
    message: String,
    alertType: Alert.AlertType?,
    vararg buttonTypes: ButtonType?
): Optional<ButtonType> {
    val alert = Alert(alertType, message, *buttonTypes).apply {
        this.title = title
        headerText = null
    }
    return alert.showAndWait()
}

/**
 * 弹窗-详细-多行文本，指定弹窗类型
 *
 * @param message   弹窗提示信息
 * @param alertType 弹窗类型
 * @return 接收用户点击按钮类型
 */
inline fun alertDetail(
    head: String,
    text: String = head,
    alertType: Alert.AlertType?,
    isExpanded: Boolean = false,
    vararg buttonTypes: ButtonType?
): Optional<ButtonType> {

    val alert = Alert(alertType, head, *buttonTypes).apply {
        this.title = "详细信息"
        dialogPane.expandableContent = VBox().apply {
            alignment = Pos.CENTER
            children.add(TextArea(text).apply {
                isEditable = false
                isWrapText = true
                prefWidth = 800.0
                prefHeight = 800.0
                maxWidth = Double.MAX_VALUE
                maxHeight = Double.MAX_VALUE
            })
        }
        dialogPane.prefWidth = 750.0
        dialogPane.isExpanded = isExpanded
    }
    return alert.showAndWait()
}

/**
 * 弹窗-详细-多行文本，指定弹窗类型
 *
 * @param message   弹窗提示信息
 * @param alertType 弹窗类型
 * @return 接收用户点击按钮类型
 */
inline fun alertDetail(
    title: String,
    head: String,
    text: String = head,
    alertType: Alert.AlertType?,
    isExpanded: Boolean = false,
    vararg buttonTypes: ButtonType?
): Optional<ButtonType> {

    val alert = Alert(alertType, head, *buttonTypes).apply {
        this.title = title
        dialogPane.expandableContent = VBox().apply {
            alignment = Pos.CENTER
            children.add(TextArea(text).apply {
                isEditable = false
                isWrapText = true
                prefWidth = 800.0
                prefHeight = 800.0
                maxWidth = Double.MAX_VALUE
                maxHeight = Double.MAX_VALUE
            })
        }
        dialogPane.prefWidth = 750.0
        dialogPane.isExpanded = isExpanded
    }
    return alert.showAndWait()
}

/**
 * 把文本内容设置到系统剪贴板
 * @param text 文本内容
 * @author lifay
 */
fun copyToClipboard(text: String) {
    // 获取系统剪贴板
    val clipboard = Toolkit.getDefaultToolkit().systemClipboard
    // 封装文本内容
    val trans = StringSelection(text)
    clipboard.setContents(trans, null)
}
