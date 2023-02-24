package cn.lifay.ui.message

import cn.lifay.extension.asyncTask
import cn.lifay.extension.platformRun
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.Label
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import java.util.*

/**
 * @author zhong
 * @date 2020-09-30 13:46:43 周三
 */
class Notification constructor() {
    enum class Type {
        /**
         * 信息展示
         */
        INFO,

        /**
         * 警告用户
         */
        WARNING,

        /**
         * 提示用户错误
         */
        ERROR,

        /**
         * 告知用户成功
         */
        SUCCESS
    }

    @FXML
    private var root: HBox? = null

    @FXML
    private var iconClose: Label? = null

    @FXML
    private var title: Label? = null

    @FXML
    private var content: Label? = null

    private var owner: Pane? = null


    private fun schedule(task: TimerTask, milliseconds: Long) {
        if (timer == null) {
            timer = Timer(true)
        }
        timer!!.schedule(task, milliseconds)
    }

    companion object {
        private val notificationContainerMap: MutableMap<Pane, VBox> = HashMap()
        const val DEFAULT_DELAY: Long = 4500

        /**
         * 显示通知
         *
         * @param owner   通知显示的面板
         * @param message 通知消息
         * @param type    通知类型
         * @param delay   通知停留时间，毫秒，为 0 则不会自动关闭
         */
        @JvmOverloads
        fun showAutoClose(owner: Pane, message: String, type: Type = Type.INFO, delay: Long = DEFAULT_DELAY) {
            asyncTask {
                val fxmlLoader = FXMLLoader(javaClass.getResource("/fxml/Notification.fxml"))
                fxmlLoader.load<Pane>()
                val instance = fxmlLoader.getController<Notification>()
                platformRun {
                    instance.owner = owner
                    instance.content!!.text = message
                }
                when (type) {
                    Type.INFO -> instance.title!!.text = "信息"
                    Type.WARNING -> instance.title!!.text = "警告"
                    Type.ERROR -> instance.title!!.text = "错误"
                    else -> instance.title!!.text = "成功"
                }
                platformRun {
                    instance.root!!.styleClass.add("notification-" + type.name.lowercase(Locale.getDefault()))
                }
                val container = notificationContainerMap.computeIfAbsent(owner) { pane: Pane? ->
                    val vBox = VBox()
                    vBox.isManaged = false
                    vBox.spacing = 15.0
                    vBox.layoutY = 15.0
                    vBox.layoutXProperty()
                        .bind(owner.widthProperty().subtract(instance.root!!.widthProperty()).subtract(17))
                    platformRun {
                        owner.children.add(vBox)
                    }
                    vBox
                }
                platformRun {
                    container.children.add(instance.root)
                }
                //自动关闭
                val d = if (delay <= 0) DEFAULT_DELAY else delay
                instance.schedule(object : TimerTask() {
                    override fun run() {
                        platformRun { container.children.remove(instance.root) }
                    }
                }, d)

                instance.iconClose!!.onMouseClicked =
                    EventHandler { event: MouseEvent? -> container.children.remove(instance.root) }
            }
        }

        private var timer: Timer? = null
    }
}