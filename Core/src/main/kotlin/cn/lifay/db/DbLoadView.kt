package cn.lifay.db

import atlantafx.base.theme.Styles
import cn.lifay.extension.*
import cn.lifay.global.LerverConfig
import cn.lifay.logutil.LerverLog
import javafx.application.Platform
import javafx.beans.value.ChangeListener
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.Stage

class DbLoadView(
    val isShowStage: Boolean,
    val targetIndexFunc: () -> Unit,
    val dbName: String,
//    val dbLoadStage: Stage,
    val afterDbInit: (() -> Unit)? = null
) :
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
    val autoTarget =
        if (!isShowStage) {
            true
        } else {
            LerverConfig.ReadProperties("db.auto_target", false)
        }

    val checkBox = CheckBox("初始化后自动跳转")

    init {
        prefHeight = 650.0
        prefWidth = 750.0

        children.addAll(textArea, progressBarPane, HBox(10.0).apply {
            setMargin(this, Insets(0.0, 0.0, 0.0, 20.0))
            children.addAll(checkBox, targetBtn)
        })
//        println("autoTarget:$autoTarget")
        checkBox.apply {
            isSelected = autoTarget == true
            this.selectedProperty().addListener { observableValue, old, new ->
                if (autoTarget != new) {
                    LerverConfig.WritePropertiesForKey("db", mapOf("auto_target" to checkBox.isSelected))
//                    println("upt")
                }
            }
        }
        targetBtn.isDisable = true
        initDb()
    }

    fun initDb() {

        asyncTask {
            try {
                //添加消息文本、进度条绑定
                val textChangeListener = ChangeListener<String> { ob, old, now ->
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

                //执行db初始化
                println("DbManage.Init(dbName)")
                DbManage.Init(dbName)

                //解除绑定
                DbManage.TEXT_PROPERTY.removeListener(textChangeListener)
                DbManage.PROGRESS_PROPERTY.removeListener(progressChangeListener)

                //善后
                targetBtn.isDisable = false

                afterDbInit?.let {
                    it()
                }

                //自动跳转到主界面
                if (checkBox.isSelected) {
                    platformRun {
                        targetIndex()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                val msg = "初始化API信息失败，请联系管理员:$e"
                LerverLog.error(msg)
                platformRun {
                    textArea.appendText(msg)
                    if (alertConfirmation("程序将关闭", msg)) {
                        closeWindow()
                        Platform.exit()
                    }
                }
            }

        }
    }

    fun closeWindow() {
        (this.scene.window as Stage).close()
    }
    fun getStage(): Stage {
        if (indexStage == null) {
            throw Exception("请先初始化indexStage")
        }
        return indexStage!!
    }

    private fun targetIndex() {
        try {
            targetIndexFunc()
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
                    closeWindow()
                    Platform.exit()
                }
            }
            platformRun {
                textArea.appendText(msg)
            }
            return
        }

    }

}