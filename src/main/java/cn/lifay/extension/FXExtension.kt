package cn.lifay.extension

import javafx.application.Platform
import javafx.scene.Node
import javafx.scene.paint.Color

inline fun platformRun(crossinline f : () -> Unit){
    Platform.runLater{f()}
}

fun Node.borderColor(color:String){

 this.style = "-fx-border-color: ${color};"
}

fun Node.borderColor(color:Color){
    val red = (color.red * 255).toInt()
    val green = (color.green * 255).toInt()
    val blue = (color.blue * 255).toInt()
    val web = String.format("#%02X%02X%02X", red, green, blue)
    println("R:${red} G:${green} B:${blue} $web")
    this.style = "-fx-border-color: ${web};"
}
