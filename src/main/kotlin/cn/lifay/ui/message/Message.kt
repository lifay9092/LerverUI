package cn.lifay.ui.message

import cn.lifay.extension.asyncTask
import cn.lifay.extension.platformRun
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import java.util.*

/**
 * @author zhong
 * @date 2020-09-30 13:46:43 周三
 */
class Message constructor() {
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
    private var message: Label? = null

    private var owner: Pane? = null

    private fun schedule(task: TimerTask, milliseconds: Long) {
        if (timer == null) {
            timer = Timer(true)
        }
        timer!!.schedule(task, milliseconds)
    }

    companion object {
        private val messageContainerMap: MutableMap<Pane, VBox> = HashMap()

        fun show(owner: Pane, message: String?, type: Type, delay: Long) {
            asyncTask {
                val fxmlLoader = FXMLLoader(javaClass.getResource("/fxml/Message.fxml"))
                fxmlLoader.load<Pane>()
                val instance = fxmlLoader.getController<Message>()
                platformRun {
                    instance.owner = owner
                    instance.message!!.text = message
                    instance.root!!.styleClass.add("message-" + type!!.name.lowercase(Locale.getDefault()))
                }
                val container = messageContainerMap.computeIfAbsent(owner) { pane: Pane? ->
                    val vBox = VBox()
                    vBox.isManaged = false
                    vBox.spacing = 15.0
                    vBox.layoutY = 15.0
                    vBox.layoutXProperty()
                        .bind(owner.widthProperty().subtract(instance.root!!.widthProperty()).divide(2))
                    platformRun {
                        owner.children.add(vBox)
                    }
                    vBox
                }
                platformRun {
                    container.children.add(instance.root)
                }
                instance.schedule(object : TimerTask() {
                    override fun run() {
                        platformRun { container.children.remove(instance.root) }
                    }
                }, delay)
            }

        }

        private var timer: Timer? = null
    }
}