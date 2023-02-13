package cn.lifay.extension

import cn.lifay.ui.form.FormUI
import javafx.application.Platform
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.paint.Color

inline fun platformRun(crossinline f: () -> Unit) {
    Platform.runLater { f() }
}

fun Node.appendStyle(styleStr: String) {
    val styles = this.style.split(";")
    val list = styles.filter { it.isNotBlank() }.map { it.replace(";", "") }.toMutableList()
    list.add(if (styleStr.endsWith(";")) ("$styleStr;") else styleStr)
    this.style = list.joinToString(";")
}

fun Node.backgroundColor(color: String) {
    this.appendStyle("-fx-background-color: ${color};")
}

fun Node.backgroundColor(color: Color) {
    this.backgroundColor(color.toWeb())
}

fun Node.textFillColor(color: String) {
    this.appendStyle("-fx-text-fill: ${color};")
}

fun Node.textFillColor(color: Color) {
    this.textFillColor(color.toWeb())
}

fun Node.borderColor(color: String) {
    this.appendStyle("-fx-border-color: ${color};")
}

fun Node.borderColor(color: Color) {
    this.borderColor(color.toWeb())
}

fun Color.toWeb(): String {
    val red = (this.red * 255).toInt()
    val green = (this.green * 255).toInt()
    val blue = (this.blue * 255).toInt()
    //    println("R:${red} G:${green} B:${blue} $web")
    return String.format("#%02X%02X%02X", red, green, blue)
}

fun Button.styleInfo(): Button {
    this.backgroundColor("#5264AE")
    this.textFill = Color.WHITE
    return this
}

fun Button.stylePrimary(): Button {
    this.backgroundColor("#2c8cf4")
    this.textFill = Color.WHITE
    return this
}

fun Button.styleSuccess(): Button {
    this.backgroundColor("#1cbc6c")
    this.textFill = Color.WHITE
    return this
}

fun Button.styleWarn(): Button {
    this.backgroundColor("#fc9c04")
    this.textFill = Color.WHITE
    return this
}

fun Button.styleDanger(): Button {
    this.backgroundColor("#ec4414")
    this.textFill = Color.WHITE
    return this
}

inline fun <T : Any> formUI(block: FormUI<T>.() -> FormUI<T>) {
//    block(FormUI<T>())
}