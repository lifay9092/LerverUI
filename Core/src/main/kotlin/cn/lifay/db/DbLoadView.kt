package cn.lifay.db

import atlantafx.base.theme.Styles
import cn.lifay.extension.*
import cn.lifay.logutil.LerverLog
import javafx.application.Platform
import javafx.beans.value.ChangeListener
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ProgressBar
import javafx.scene.control.TextArea
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.Stage
import java.util.function.Supplier

class DbLoadView(val indexStageGet: Supplier<Stage>, val dbName: String, val afterDbInit: (() -> Unit)? = null) :
    VBox(20.0) {

    var indexStage: Stage? = null

    val targetBtn = Button("进入程序").apply {
        alignment = Pos.BOTTOM_RIGHT
        setMargin(this, Insets(0.0, 20.0, 20.0, 580.0))
        styleInfo()
        setOnAction {
            targetIndex()
        }
    }

    val textArea = TextArea("").apply {
        prefHeight = 440.0
        prefWidth = 550.0
        setMargin(this, Insets(0.0, 20.0, 0.0, 20.0))
        alignment = Pos.CENTER
        isWrapText = true
        isEditable = false
        appendStyle("-fx-background-insets: 0;")
    }
    val progressBar = ProgressBar().apply {
        prefWidth = 650.0
    }
    val label = Label().apply {
        minWidth = 100.0
        styleClass.add(Styles.ACCENT)
    }
    val progressBarPane = HBox(5.0).apply {
        alignment = Pos.CENTER
        setMargin(this, Insets(0.0, 20.0, 0.0, 20.0))
        children.addAll(
            progressBar,
            label
        )
    }

    init {
        prefHeight = 650.0
        prefWidth = 750.0

        children.addAll(textArea, progressBarPane, targetBtn)

        targetBtn.isDisable = true
        initDb()
    }

    fun initDb() {

        asyncTask {
            try {
                val textChangeListener = ChangeListener { ob, old, now ->
                    if (now.isNotBlank()) {
                        platformRun {
                            textArea.appendText(now)
                        }
                    }
                }
                DbManage.TEXT_PROPERTY.addListener(textChangeListener)

                val progressChangeListener = ChangeListener<Number> { ob, old, now ->
                    if (now != null) {
                        platformRun {
                            progressBar.progress = now.toDouble()
                            label.text = "进度：${now}%"
                        }
                    }
                }
                DbManage.PROGRESS_PROPERTY.addListener(progressChangeListener)

                DbManage.Init(dbName)


                DbManage.TEXT_PROPERTY.removeListener(textChangeListener)
                DbManage.PROGRESS_PROPERTY.removeListener(progressChangeListener)

                targetBtn.isDisable = false

                afterDbInit?.let {
                    it()
                }
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

    fun getStage(): Stage {
        if (indexStage == null) {
            throw Exception("请先初始化indexStage")
        }
        return indexStage!!
    }
    private fun targetIndex() {
        try {
            indexStage = indexStageGet.get()
        } catch (e: Exception) {
            e.printStackTrace()
            val msg = "初始化界面失败,请检查db脚本:${e.stackTraceToString()}"
            LerverLog.error(msg)
            platformRun {
                if (alertConfirmation(
                        "是否关闭程序?",
                        "初始化界面失败,请检查db脚本和界面初始化是否冲突:${e.message}"
                    )
                ) {
                    Platform.exit()
                }
            }
            platformRun {
                textArea.appendText(msg)
            }
            return
        }
        scene.window.hide()
        indexStage!!.show()
    }

}