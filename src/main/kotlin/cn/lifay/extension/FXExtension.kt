package cn.lifay.extension

import cn.lifay.ui.LoadingUI
import javafx.application.Platform
import javafx.scene.Node
import javafx.scene.control.Alert
import javafx.scene.control.Button
import javafx.scene.control.ButtonType
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.paint.Color
import javafx.stage.Stage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.FutureTask

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
        } catch (e: Exception) {
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
    owner: Stage,
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
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            platformRun { loadingUI.closeStage() }
        }
    }
}

inline fun platformRun(crossinline f: () -> Unit) {
    Platform.runLater { f() }
}

inline fun <R> platformGet(crossinline f: () -> R): R {
    val task = FutureTask { f() }
    Platform.runLater { task }
    return task.get()
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
    this.styleClass.add("button-info")
    return this
}

fun Button.stylePrimary(): Button {
    //  this.backgroundColor("#2c8cf4")
    // this.textFill = Color.WHITE
    this.styleClass.add("button-primary")
    return this
}

fun Button.styleSuccess(): Button {
    //  this.backgroundColor("#1cbc6c")
    //  this.textFill = Color.WHITE
    this.styleClass.add("button-success")
    return this
}

fun Button.styleWarn(): Button {
    //  this.backgroundColor("#fc9c04")
    //  this.textFill = Color.WHITE
    this.styleClass.add("button-warning")
    return this
}

fun Button.styleDanger(): Button {
    //  this.backgroundColor("#ec4414")
    // this.textFill = Color.WHITE
    this.styleClass.add("button-danger")
    return this
}

/*
inline fun <T : Any> formUI(block: FormUI<T>.() -> FormUI<T>) {
//    block(FormUI<T>())
}*/
fun Stage.bindEscKey(): Stage {
    addEventHandler(KeyEvent.KEY_PRESSED) {
        if (it.code == KeyCode.ESCAPE) {
            close()
        }
    }
    return this
}
