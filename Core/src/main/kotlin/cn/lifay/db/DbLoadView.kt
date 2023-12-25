package cn.lifay.db

import cn.lifay.extension.alertConfirmation
import cn.lifay.extension.asyncTask
import cn.lifay.extension.platformRun
import cn.lifay.logutil.LerverLog
import javafx.application.Platform
import javafx.beans.value.ChangeListener
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.TextArea
import javafx.scene.layout.VBox
import javafx.stage.Stage

class DbLoadView(val indexStage: Stage, val dbName: String) : VBox() {

    val targetBtn = Button("跳转").apply {
        alignment = Pos.CENTER
        setOnAction {
            targetIndex()
        }
    }

    val textArea = TextArea("").apply {
        prefHeight = 840.0
        prefWidth = 590.0
        alignment = Pos.CENTER
        isWrapText = true
        isEditable = false
    }

    init {
        prefHeight = 800.0
        prefWidth = 600.0

        children.addAll(textArea, targetBtn)

        targetBtn.isDisable = true
        initDb()
    }

    fun initDb() {

        asyncTask() {
            try {
                val changeListener = ChangeListener { ob, old, now ->
                    if (now.isNotBlank()) {
                        platformRun {
                            textArea.appendText(now)
                        }
                    }
                }

                DbManage.TEXT_PROPERTY.addListener(changeListener)

                DbManage.Init(dbName)

                targetBtn.isDisable = false
                DbManage.TEXT_PROPERTY.removeListener(changeListener)
            } catch (e: Exception) {
                e.printStackTrace()
                val msg = "初始化API信息失败，请联系管理员:$e"
                LerverLog.error(msg)
                platformRun {
                    textArea.appendText(msg)
                    if (alertConfirmation("程序将关闭", msg)) {
                        Platform.exit()
                    }
                }
            }

        }
    }

    private fun targetIndex() {


        scene.window.hide()
        indexStage.show()
    }

}