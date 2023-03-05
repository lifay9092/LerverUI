package cn.lifay.ui

import cn.lifay.exception.LerverUIException
import cn.lifay.extension.asyncTask
import cn.lifay.mq.FXEventBusException
import cn.lifay.mq.FXReceiver
import cn.lifay.ui.message.Message
import cn.lifay.ui.message.Notification
import cn.lifay.ui.tree.TreeViewHelp
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.scene.Parent
import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.ButtonType
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.scene.layout.Pane
import javafx.stage.StageStyle
import javafx.stage.Window
import java.net.URL
import java.util.*
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty1


/**
 * BaseView 基础控制器视图
 * @sample
 *
 *          1. instance
 *              val indexPane = FXMLLoader.load<Pane>(ResourceUtil.getResource("index.fxml"))
 *          2. instance
 *             val VView = createView<VController,VBox>(ResourceUtil.getResource("V.fxml")){
 *             initForm(indexController, V.id)
 *         }
 * @author lifay
 * @date 2023/2/23 17:01
 **/
abstract class BaseView<R : Pane>() : Initializable {

    companion object {

        fun <T : BaseView<R>, R : Pane> createView(fxml: URL): T {
            val loader = FXMLLoader(fxml)
            var load = loader.load<R>()
            if (GlobeTheme.ELEMENT_STYLE) {
                load.stylesheets.add(this::class.java.getResource("/css/element-ui.css").toExternalForm())
            }
            return loader.getController<T?>().apply {
                rootPane().set(load)
            }
        }

        fun <T : BaseView<R>, R : Pane> createView(fxml: URL, initFunc: T.() -> Unit): T {
            val loader = FXMLLoader(fxml)
            var load = loader.load<R>()
            if (GlobeTheme.ELEMENT_STYLE) {
                load.stylesheets.add(this::class.java.getResource("/css/element-ui.css").toExternalForm())
            }
            return loader.getController<T?>().apply {
                rootPane().set(load)
                initFunc()
            }
        }
    }

    init {
        //注册方法
        val clazz = this.javaClass
        for (method in clazz.declaredMethods) {
            val fxReceiver = method.getAnnotation(FXReceiver::class.java)
            if (fxReceiver != null) {
                FXReceiver.add(fxReceiver.id, this, method::invoke)
            }
        }
    }

    /**
     * 注册根容器
     */
    protected abstract fun rootPane(): KMutableProperty0<R>
    override fun initialize(p0: URL?, p1: ResourceBundle?) {
        if (GlobeTheme.ELEMENT_STYLE) {
            getRoot().stylesheets.add(this::class.java.getResource("/css/element-ui.css").toExternalForm())
        }
    }

    open fun InitElemntStyle() {

    }

    open fun getRoot(): Parent {
        return rootPane().get()

    }

    protected open fun getWindow(): Window? {
        return rootPane().get().scene.window
    }

    /**
     * 发送消息
     * @param id 接受者ID
     * @param args 参数值
     * @author lifay
     * @return
     */
    protected open fun send(id: String, vararg args: Any) {
        val stackTraceElements = Thread.currentThread().stackTrace
//        stackTraceElements.forEach { println(it) }
        val element = stackTraceElements[2]
//        println(element)
        if (FXReceiver.has(element.className, element.methodName)) {
            throw FXEventBusException("@FXReceiver 函数不能循环：class=${element.className} method=${element.methodName}")
        }
        asyncTask {
            FXReceiver.invoke(id, args)
        }
    }

    /**
     * 弹窗提示
     *
     * @param message 弹窗展示信息
     * @return 接收用户点击按钮类型
     */
    protected open fun alertInfo(message: String, vararg buttonTypes: ButtonType?): Optional<ButtonType> {
        return alert(message, AlertType.INFORMATION, *buttonTypes)
    }

    /**
     * 弹窗提示
     *
     * @param message 弹窗确认
     * @return 接收用户确认结果
     */
    protected open fun alertConfirmation(message: String): Boolean {
        val buttonType = alert(message, AlertType.CONFIRMATION)
        return buttonType.isPresent && buttonType.get() == ButtonType.OK
    }

    /**
     * 弹窗警告
     *
     * @param message 弹窗展示信息
     * @return 接收用户点击按钮类型
     */
    protected open fun alertWarn(message: String, vararg buttonTypes: ButtonType?): Optional<ButtonType> {
        return alert(message, AlertType.WARNING, *buttonTypes)
    }

    /**
     * 弹窗报错
     *
     * @param message 弹窗展示信息
     * @return 接收用户点击按钮类型
     */
    protected open fun alertError(message: String, vararg buttonTypes: ButtonType?): Optional<ButtonType> {
        return alert(message, AlertType.ERROR, *buttonTypes)
    }

    /**
     * 弹窗，指定弹窗类型
     *
     * @param message   弹窗提示信息
     * @param alertType 弹窗类型
     * @return 接收用户点击按钮类型
     */
    protected open fun alert(
        message: String,
        alertType: AlertType,
        vararg buttonTypes: ButtonType?
    ): Optional<ButtonType> {
        val alert = Alert(alertType, message, *buttonTypes).apply {
            initStyle(StageStyle.TRANSPARENT)
            dialogPane.scene.fill = null
            initOwner(getWindow())
            contentText = message
        }
        if (GlobeTheme.ELEMENT_STYLE) {
            val s = javaClass.getResource("/css/element-ui.css").toExternalForm()
            alert.dialogPane.stylesheets.add(s)
        }
        return alert.showAndWait()
    }


    /**
     * 显示一则指定类型默认延迟的消息
     *
     * @param message 通知信息
     */
    protected open fun showMessage(message: String, delay: Long = 2500) {
        showMessage(message, Message.Type.INFO, delay)
    }

    /**
     * 显示一则指定类型指定延迟的消息
     *
     * @param message 通知信息
     */
    @JvmOverloads
    protected open fun showMessage(message: String, type: Message.Type = Message.Type.INFO, delay: Long = 2500) {
        val root = getRoot()
        if (root !is Pane) {
            alertError("root 必须是 Pane 或其子类 : ${root}")
            return
        }
        Message.show(root, message, type, delay)
    }

    protected open fun showNotification(message: String, milliseconds: Long = Notification.DEFAULT_DELAY) {
        showNotification(message, Notification.Type.INFO, milliseconds)
    }

    /**
     * 显示一则指定类型的通知，自动关闭，指定显示时间
     *
     * @param message      通知信息
     * @param type         通知类型
     * @param milliseconds 延迟时间 毫秒
     */
    @JvmOverloads
    protected open fun showNotification(
        message: String,
        type: Notification.Type,
        milliseconds: Long = Notification.DEFAULT_DELAY
    ) {
        val root = getRoot()
        if (root !is Pane) {
            alertError("root 必须是 Pane 或其子类 : ${root}")
            return
        }
        Notification.showAutoClose(root, message, type, milliseconds)
    }


    /*树视图部分*/
    val HELP_MAP = HashMap<String, TreeViewHelp<*, *>>()
    val <T> TreeView<T>.treeId: String
        get() {
            return this.id ?: this.hashCode().toString()
        }

    fun <V, B> TreeView<V>.Register(
        idProp: KProperty1<V, B>,
        parentProp: KProperty1<V, B>
    ) {
        HELP_MAP[treeId] = TreeViewHelp(idProp, parentProp)
    }

    inline fun <reified V, B> TreeView<V>.InitTreeItems(datas: List<V>) {
        if (!HELP_MAP.containsKey(treeId)) {
            throw LerverUIException("TreeView未进行注册:${treeId}")
        }
        val help = HELP_MAP[treeId] as TreeViewHelp<V, B>
        val prop = help.TREE_VIEW_PROP
        AddChildren(this.root, prop, datas.toTypedArray())
        //添加索引
        InitIndexs(this.root, prop.first)
        // println(findTreeItem("ee1feccca35a4645a3940bc51dd90836"))
    }

    fun <V, B> TreeView<V>.AddChildren(
        panTreeItem: TreeItem<V>,
        prop: Pair<KProperty1<V, B>, KProperty1<V, B>>,
        datas: Array<V>
    ) {
        //获取子节点
        val childtren = datas.filter { prop.first.get(panTreeItem.value!!) == prop.second.get(it) }.map {
            val item = TreeItem(it)
            AddChildren(item, prop, datas)
            item
        }
        //添加子节点
        if (childtren.isNotEmpty()) {
            panTreeItem.children.addAll(childtren)
        }
    }

    fun <V, B> TreeView<V>.AddChild(
        panTreeItem: TreeItem<V>,
        prop: Pair<KProperty1<V, B>, KProperty1<V, B>>,
        data: V
    ) {
        //获取子节点
        val child = TreeItem(data)
        //添加子节点
        panTreeItem.children.add(child)
    }

    fun <V, B> TreeView<V>.InitIndexs(treeItem: TreeItem<V>, idProp: KProperty1<V, B>, indexs: String = "") {
        for ((i, child) in treeItem.children.withIndex()) {
            //添加索引
            val tempIndexs = if (indexs.isBlank()) i.toString() else "${indexs}-$i"
            //TREE_ITEM_INDEX[idProp.get(child.value)] = tempIndexs
            AddIndex(idProp.get(child.value), tempIndexs)
            //子节点
            if (child.children.isNotEmpty()) {
                InitIndexs(child, idProp, tempIndexs)
            }
        }
    }

    fun <V, B> TreeView<V>.AddIndex(id: B, indexs: String) {
        if (!HELP_MAP.containsKey(treeId)) {
            throw LerverUIException("TreeView未进行注册:${treeId}")
        }
        val help = HELP_MAP[treeId]!! as TreeViewHelp<V, B>
        help.TREE_VIEW_INDEX[id] = indexs
    }

    fun <V, B> TreeView<V>.FindTreeItem(id: B): TreeItem<V>? {
        if (!HELP_MAP.containsKey(treeId)) {
            throw LerverUIException("TreeView未进行注册:${treeId}")
        }
        val help = HELP_MAP[treeId]!! as TreeViewHelp<V, B>
        val indexs = help.TREE_VIEW_INDEX[id]
        var temp: TreeItem<V>? = null
        for (index in indexs!!.split("-")) {
            if (temp == null) {
                temp = this.root
            }
            temp = GetChildByIndex(temp!!, index.toInt(), help.TREE_VIEW_PROP.first)
            if (temp == null) {
                return null;
            }
        }
        return temp
    }

    fun <V, B> TreeView<V>.GetChildByIndex(node: TreeItem<V>, index: Int, idProp: KProperty1<V, B>): TreeItem<V>? {
        if (index >= node.children.size) {
            println("节点:${idProp.get(node.value)} 没有索引:${index}")
            return null
        }
        return node.children[index]
    }


}