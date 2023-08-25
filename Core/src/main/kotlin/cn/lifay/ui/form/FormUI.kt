package cn.lifay.ui.form

import cn.lifay.exception.LerverUIException
import cn.lifay.extension.*
import cn.lifay.global.GlobalResource
import cn.lifay.ui.BaseView
import cn.lifay.ui.form.btn.CustomButton
import javafx.event.EventHandler
import javafx.geometry.HPos
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.TextArea
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.WindowEvent
import org.kordamp.ikonli.feather.Feather
import java.net.URL
import java.util.*
import java.util.function.Consumer
import kotlin.reflect.full.primaryConstructor


/**
 * Form类示例：
 *
 *```
 * class UserForm(t: UserData? = null) : FormUI<UserData>("用户管理", t)
 * class AddrForm(t: HttpAddr? = null) : FormUI<HttpAddr>("地址管理", t)
 *```
 * 【元素类使用示例】
 * dada class:
 * ```
 * val id = TextElement("ID:", UserData::id, true)
 * val name = TextElement("名称:", UserData::name, isTextArea = true)
 * val type = SelectElement("类型:", UserData::type, SelectTypeEnum.values().toList())
 * val child = CheckElement("是否未成年:", UserData::child)
 * val sex = RadioElement("性别:", UserData::sex, listOf("男", "女", "中间"))
 * addElement(id, name,type, child,sex)
 * ```
 * javaBean use [cn.lifay.ui.DelegateProp]:
 * ```
 * val id = TextElement<Person, Int>("ID：", DelegateProp("id"), primary = true)
 * val name = TextElement<Person, String>("姓名：", DelegateProp("name"))
 * val child = CheckElement<Person, Boolean>("成年：", DelegateProp("child"))
 * addElement(id, name, child)
 * ```
 *@author lifay
 **/
class FormUI<T : Any>(
    val title: String,
    initDefaultEntity: T? = null,
    val elements: ArrayList<FormElement<T, *>>,
    val customButtons: ArrayList<CustomButton<FormUI<T>>>,
    val saveDataFunc: ((T) -> Boolean)?,
    val updateDataFunc: ((T) -> Boolean)?,
) : BaseView<VBox>() {

    protected var entity: T? = null
    private val stage = Stage().bindEscKey()
    private lateinit var root: VBox
    private val form = GridPane()

    protected var btnGroup = HBox(20.0)
    protected var isUpdate: Boolean = false

    //    protected lateinit var elements: ObservableList<FormElement<T, *>>
    protected val saveBtn: Button = Button("保存").stylePrimary().icon(Feather.CHECK)

    //    protected val updateBtn: Button = Button("更新").styleDanger().icon(Feather.EDIT)
    protected val clearBtn: Button = Button("清空").styleWarn().icon(Feather.X)

//    protected lateinit var saveDataFunc : (T) -> Boolean
//    protected lateinit var updateDataFunc : (T) -> Boolean
//    protected lateinit var deleteDataFunc : (Any) -> Boolean
//
//    fun FormUI(
//        elements: ObservableList<FormElement<T, *>>,
//        saveDataFunc : (T) -> Boolean,
//        updateDataFunc : (T) -> Boolean,
//        deleteDataFunc : (Any) -> Boolean) {
//        this.saveDataFunc = elements
//        this.saveDataFunc = saveDataFunc
//        this.updateDataFunc = updateDataFunc
//        this.deleteDataFunc = deleteDataFunc
//    }

    init {
        // println("FormUI init")
        try {
            uiInit()
            if (initDefaultEntity == null) {
                val tc = elements[0].tc
//                println("tc:$tc")
                val args = getElementInitValue()
//                println("args:${args.contentToString()}")
//                args.forEach { println(it) }
                this.entity = tc!!.primaryConstructor!!.call(*args)
            } else {
                isUpdate = true
                elements.forEach {
                    if (it.primary){
                        it.disable()
                    }
                }
                refreshForm(initDefaultEntity!!)
            }
            initNotificationPane()
        } catch (e: Exception) {
            e.printStackTrace()
            throw LerverUIException("表单初始化失败:${e.message}")
        }
    }

    /**
     * 表单不是fxml导入，子类不需要当前方法
     */
    override fun initialize(p0: URL?, p1: ResourceBundle?) {
    }

    private fun getElementInitValue(): Array<Any?> {
        return Array(elements.size) {
            val element = elements[it]
            val get = element.defaultValue()
//            println("${element.label} = ${get}")
            get
        }
    }

    /**
     * 注册根容器
     * @author lifay
     * @return
     */
    override fun rootPane(): VBox {
        this.root = VBox(10.0)
        return this.root
    }

    /*
        界面初始化
     */
    private fun uiInit() {
        this.root.apply {
            prefWidth = GlobalResource.FormWidth()
//            prefHeight = 120.0 * elements.size
        }
        stage.initModality(Modality.APPLICATION_MODAL)
        GlobalResource.loadIcon(stage)

        root.children.addAll(form)

        //表单布局
        form.alignment = Pos.CENTER
        form.hgap = 5.0
        form.vgap = 5.0
        form.padding = Insets(5.0, 5.0, 5.0, 5.0)

        if (elements.isEmpty()) {
            throw LerverUIException("未获取到有效表单元素!")
        }
        initElements()

        stage.title = title
        stage.scene = Scene(root)

        //快捷键
        val saveKey = KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN)
        root.scene.accelerators[saveKey] = Runnable {
            if (isUpdate) {
                updateFunc()
            } else {
                saveFunc()
            }
        }
    }

    private fun refreshForm(t: T) {
        this.entity = t
        propToElement()
    }


    /**
     * 加载布局
     *
     * @author lifay
     * @return
     */
    private fun initElements() {
        /*网格布局：元素*/
        //设置初始值
        initValue()
        //网格布局 算法 前提：纵向数量<=横向数量
        var h = 0
        var v = 0
        val size = elements.size
        //System.out.println("元素数量:" + size);
        val s = Math.sqrt(size.toDouble()) //开根号
        if (s.toInt().toDouble() == s) {
            h = s.toInt()
            v = s.toInt()
        } else {
            h = Math.ceil(s).toInt()
            v = Math.round(s).toInt()
        }
        //System.out.println("横向数量="+h + " 竖向数量=" + v);
        //布局表单元素 0 0 ,1 0,01
        var elementIndex = 0
//        var tx :Int? = null
//        var ty :Int? = null
        for (x in 0 until h) {
            for (y in 0 until v) {
                //System.out.println("x="+x + " y=" + y);
//                if (tx != null && ty != null && tx == x && ty == y) {
//                     tx = null
//                     ty  = null
//                    continue
//                }
                val element: FormElement<T, *> = elements[elementIndex]
                GridPane.setHalignment(element, HPos.LEFT)
                if (element.graphic() is TextArea) {
                    form.addRow(x, element)
//                    tx = x
//                    ty = y
                } else {
                    form.add(element, y, x)
                }
                elementIndex++
                if (elementIndex == size) {
                    break
                }
            }
        }
        //form.add(btnGroup, Math.round((h / 2).toFloat()), v)
        form.add(btnGroup, v - 1, h)
        //布局按钮组
        btnGroup.alignment = Pos.BOTTOM_RIGHT
        btnGroup.spacing = 20.0
        btnGroup.padding = Insets(10.0)
        btnGroup.children.addAll(saveBtn, clearBtn)

        customButtons.forEach { customButton ->
            customButton.btn.setOnAction {
                customButton.actionFunc(this)
            }
            btnGroup.children.addAll(customButton.btn)
        }

        //pane.getChildren().add(btnGroup);
        //保存
        saveBtn.setOnMouseClicked { mouseEvent ->
            if (mouseEvent.clickCount == 1) {
                if (isUpdate) updateFunc() else saveFunc()
            }
        }

        //清空
        clearBtn.setOnMouseClicked { mouseEvent ->
            if (mouseEvent.clickCount == 1) {
                try {
                    clearBtn.isDisable = true
                    clear()
                } catch (e: Exception) {
                    e.printStackTrace()
                    showMessage("操作失败:" + e.message)
                } finally {
                    clearBtn.isDisable = false
                }
            }
        }
    }

    private fun checkElementValue(isUpdate: Boolean = false): Boolean {
        for (element in elements) {
            if (!isUpdate && element.primary) {
                continue
            }
            if (!element.checkRequired()) {
                return false
            }
        }
        return true
    }

    private fun saveFunc() {
        asyncTaskLoading(stage, "保存中") {
            try {
                saveBtn.isDisable = true
                //检查
                if (!checkElementValue()) {
                    return@asyncTaskLoading
                }
                //从元素赋值到实例
                elementToProp()
                //执行保存操作
                if (saveDataFunc!!(entity!!)) {
                    showMessage("保存成功")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                showErrMessage("保存失败:" + e.message)
            } finally {
                saveBtn.isDisable = false
            }
        }
    }

    private fun updateFunc() {
        asyncTaskLoading(stage, "更新中") {
            try {
                saveBtn.isDisable = true
                //检查
                if (!checkElementValue(true)) {
                    return@asyncTaskLoading
                }
                //从元素赋值到实例
                elementToProp()
                //执行保存操作
                if (updateDataFunc!!(entity!!)) {
                    showMessage("更新成功")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                showErrMessage("更新失败:" + e.message)
            } finally {
                saveBtn.isDisable = false
            }
        }
    }

    protected fun initValue() {
        elements.forEach { it.initValue() }
    }

    private fun clear() {
        elements.forEach(Consumer { obj: FormElement<T, *> ->
            platformRun {
                if (obj.initValue != null) {
                    obj.initValue()
                } else {
                    obj.clear()
                }
            }
        })
    }

    private fun getPrimaryValue(): Any? {
        for (element in elements) {
            if (element.primary) {
                return element.getElementValue()
            }
        }
        return null
    }

    /**
     * 将组件数据赋值到实例属性
     *
     * @author lifay
     * @return void
     */
    private fun elementToProp() {
        try {
            for (element in elements) {
                element.eleToProp(entity!!)
            }
        } catch (e: NoSuchFieldException) {
            throw RuntimeException(e)
        } catch (e: IllegalAccessException) {
            throw RuntimeException(e)
        }
    }

    /**
     * 将实例属性赋值到组件数据
     *
     * @author lifay
     * @return void
     */
    private fun propToElement() {
        try {
            for (element in elements) {
                element.propToEle(this.entity!!)
                /*if (element.primary) {
                    element.disable()
                }*/
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw RuntimeException(e)
        }
    }

    private fun checkPrimaryValue(id: Any?) {
        if (id == null) {
            throw RuntimeException("主键值不能为空!");
        }
        val blank = when (id) {
            is String -> id.isBlank();
            is Int -> id == 0;
            is Long -> id == 0L;
            else -> throw RuntimeException("不支持当前类型:" + id.javaClass);
        };
        if (blank) {
            throw RuntimeException("主键值不能为空!");
        }
    }

    fun show() {
        stage.show()
    }

    fun showAndWait() {
        stage.showAndWait()
    }

    fun close() {
        stage.close()
    }

    fun setOnCloseRequest(value: EventHandler<WindowEvent>) {
        stage.onCloseRequest = value
    }

}

