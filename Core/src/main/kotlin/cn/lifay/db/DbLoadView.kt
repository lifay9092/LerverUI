package cn.lifay.db

import cn.lifay.extension.alertConfirmation
import cn.lifay.extension.asyncTask
import cn.lifay.extension.platformRun
import cn.lifay.logutil.LerverLog
import javafx.application.Platform
import javafx.scene.control.Button
import javafx.scene.control.TextArea
import javafx.scene.layout.AnchorPane
import javafx.stage.Stage

class DbLoadView(val indexStage: Stage, val dbName: String) : AnchorPane() {

    lateinit var targetBtn: Button

    val textArea = TextArea("ssss").apply {
        isWrapText = true
    }

    init {
        prefHeight = 400.0
        prefWidth = 600.0

        targetBtn.isDisable = true
        initDb()
    }

    fun initDb() {

        asyncTask() {
            try {
                DbManage.TEXT_PROPERTY.addListener { ob, old, now ->
                    textArea.appendText(now)
                }
                DbManage.Init(dbName)

                targetBtn.isDisable = false
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