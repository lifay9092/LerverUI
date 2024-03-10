package cn.lifay.extension

import atlantafx.base.theme.Styles
import cn.lifay.exception.LerverUIException
import cn.lifay.global.GlobalResource
import cn.lifay.ui.LoadingUI
import javafx.application.Platform
import javafx.css.PseudoClass
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.input.*
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.stage.Stage
import javafx.stage.Window
import kotlinx.coroutines.*
import org.kordamp.ikonli.Ikon
import org.kordamp.ikonli.javafx.FontIcon
import java.io.File
import java.util.*
import kotlin.coroutines.CoroutineContext


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
    context: CoroutineContext = Dispatchers.Default,
    crossinline block: () -> Unit
): Job {
    return CoroutineScope(context).launch {
        //执行任务
        block()
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
    noinline success: (() -> Unit)? = null,
    noinline fail: (() -> Unit)? = null,
    crossinline handle: () -> Unit
) {
    val loadingUI = LoadingUI(owner, animation = animation, msg = msg)
    CoroutineScope(Dispatchers.Default).launch {
        try {
            platformRun { loadingUI.show() }
            //执行任务
            handle()
            success?.let { it() }
        } catch (e: LerverUIException) {
            e.printStackTrace()
            fail?.let { it() }
        } finally {
            platformRun { loadingUI.closeStage() }
        }

    }.invokeOnCompletion {
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

fun iconButton(ikon: Ikon): Button {
    return Button(null, FontIcon(ikon)).apply {
        styleClass.add(Styles.BUTTON_ICON)
    }
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

fun Button.icon(icon: Ikon): Button {
    //  this.backgroundColor("#ec4414")
    // this.textFill = Color.WHITE
    this.graphic = FontIcon(icon)
    return this
}

/**
 * 自定义icon的样式
 *    testTbBtn.graphic = FontIcon("mdal-adb")
 *       .customStyle(16, Color.RED)
 *    copyBtn.graphic = FontIcon(Feather.COPY)
 *        .customStyle(18,Color.LIGHTSKYBLUE)
 *    sendBtn.graphic = FontIcon().apply {
 *        style = "-fx-font-family: 'Material Icons';-fx-icon-code: mdal-5g;-fx-icon-size: 16px;-fx-icon-color: #0014ea;"
 *    }
 */
fun FontIcon.customStyle(size: Int = 16, color: Color = Color.web("#242c2c")): FontIcon {
    appendStyle("-fx-icon-size: ${size}px;")
    appendStyle("-fx-icon-color: ${color.toWeb()};")
    if (this.iconLiteral.isNotBlank()) {
        appendStyle("-fx-icon-code: ${this.iconLiteral};")
    }
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

fun Stage.loadIcon(): Stage {
    GlobalResource.loadIcon(this)
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
inline fun alertError(
    message: String,
    isExpanded: Boolean = false,
    vararg buttonTypes: ButtonType?
): Optional<ButtonType> {
    return alertError("错误提示", "错误信息", message, isExpanded, *buttonTypes)
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
    message: String,
    isExpanded: Boolean = false,
    vararg buttonTypes: ButtonType?
): Optional<ButtonType> {
    return alertDetail(title, head, message, Alert.AlertType.ERROR, isExpanded, *buttonTypes)
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
                prefHeight = 500.0
                maxWidth = 900.0
                maxHeight = 600.0
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
                prefHeight = 500.0
                maxWidth = 900.0
                maxHeight = 600.0
            })
        }
        dialogPane.prefWidth = 750.0
//        dialogPane.prefHeight = 600.0
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
    val clipboard: Clipboard = Clipboard.getSystemClipboard()
    val clipboardContent = ClipboardContent()
    clipboardContent.putString(text)
    clipboard.setContent(clipboardContent)
}

/**
 * 把图片设置到系统剪贴板
 * @param image 图片
 * @author lifay
 */
fun copyToClipboard(image: Image) {
    // 获取系统剪贴板
    val clipboard: Clipboard = Clipboard.getSystemClipboard()
    val clipboardContent = ClipboardContent()
    clipboardContent.putImage(image)
    clipboard.setContent(clipboardContent)
}

/**
 * 把文件设置到系统剪贴板
 * @param files 文件
 * @author lifay
 */
fun copyToClipboard(files: MutableList<File>) {
    // 获取系统剪贴板
    val clipboard: Clipboard = Clipboard.getSystemClipboard()
    val clipboardContent = ClipboardContent()
    clipboardContent.putFiles(files)
    clipboard.setContent(clipboardContent)
}

/**
 * 递归地获取树状集合中所有子节点（children）中符合某个条件的所有数据，使用深度优先搜索（DFS）遍历整个树
 */
fun <T> findMatchingTreeItemChildren(node: TreeItem<T>, condition: (T) -> Boolean): List<TreeItem<T>> {
    // 基准情况：如果节点没有子节点，返回一个空列表
    if (node.children.isEmpty()) return emptyList()

    // 递归情况：先查找当前节点的子节点中符合条件的，然后再递归查找所有子节点的子节点
    return node.children.filter { condition.invoke(it.value) } +
            node.children.flatMap { findMatchingTreeItemChildren(it, condition) }
}


/**
 * 生成倒排旋转文字
 * rowSize = 每行的文字数量
 */
fun markLeftTabGroup(text: String, rowSize: Int): HBox {
    val split = text.toCharArray().toList()
    val textList = split.chunked(rowSize)

    //组装
    val hBox = HBox()
    val vBox = VBox()
    for ((index, chars) in textList.withIndex()) {
        val label = Label(chars.joinToString("")).apply {
            styleClass.addAll(
                Styles.TEXT_BOLD,
                Styles.TEXT_MUTED
            )
        }
        vBox.children.add(label)
    }
    hBox.children.addAll(
        vBox
    )
    return hBox
}