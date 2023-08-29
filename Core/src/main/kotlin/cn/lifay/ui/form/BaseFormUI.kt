package cn.lifay.ui.form

import cn.lifay.exception.LerverUIException
import cn.lifay.extension.bindEscKey
import cn.lifay.extension.platformRun
import cn.lifay.global.GlobalResource
import cn.lifay.ui.BaseView
import cn.lifay.ui.form.btn.BaseButton
import javafx.event.EventHandler
import javafx.geometry.HPos
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.TextArea
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.WindowEvent
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
open class BaseFormUI<T : Any>(
    val title: String,
    val buildFormUI: BaseFormUI<T>.() -> Unit,
) : BaseView<VBox>() {

    protected var entity: T? = null
    private val stage = Stage().bindEscKey()
    private lateinit var root: VBox
    private val form = GridPane()

    protected var btnGroup = HBox(20.0)

    protected val ELEMENTS_LIST = ArrayList<FormElement<T, *>>()
    protected val BASE_BUTTON_LIST = ArrayList<BaseButton<BaseFormUI<T>>>()
    protected var initDefaultEntity: T? = null
    protected val BEFORE_FORM_INIT_CALL_LIST = ArrayList<BaseFormUI<T>.() -> Unit>()
    protected var AFTER_FORM_INIT_CALL_LIST = ArrayList<BaseFormUI<T>.() -> Unit>()

    init {
        println("BaseFormUI init")
        try {

            BEFORE_FORM_INIT_CALL_LIST.forEach { it() }
            //执行构建
            buildFormUI()
            //初始化ui
            uiInit()
            if (initDefaultEntity == null) {
                val tc = ELEMENTS_LIST[0].tc
//                println("tc:$tc")
                val args = getElementInitValue()
//                println("args:${args.contentToString()}")
//                args.forEach { println(it) }
                this.entity = tc!!.primaryConstructor!!.call(*args)
            } else {
                //isUpdate = true
                refreshForm(initDefaultEntity!!)
            }
            initNotificationPane()
            AFTER_FORM_INIT_CALL_LIST.forEach { it() }
        } catch (e: Exception) {
            e.printStackTrace()
            throw LerverUIException("表单初始化失败:${e.message}")
        }
    }

    /**
     * 表单不是fxml导入，子类不需要当前方法
     */
    override fun initialize(p0: URL?, p1: ResourceBundle?) {
        println("BaseFormUI initialize")
    }

    fun defaultEntity(entity: T) {
        this.initDefaultEntity = entity
    }

    fun addElements(vararg elements: FormElement<T, *>) {
        this.ELEMENTS_LIST.addAll(elements)
    }

    fun addElements(elements: ArrayList<FormElement<T, *>>) {
        this.ELEMENTS_LIST.addAll(elements)
    }

    fun addCustomButtons(vararg customButtons: BaseButton<BaseFormUI<T>>) {
        this.BASE_BUTTON_LIST.addAll(customButtons)
    }

    fun beforeFormInitCall(call: BaseFormUI<T>.() -> Unit) {
        this.BEFORE_FORM_INIT_CALL_LIST.add(call)
    }

    fun afterFormInitCall(call: BaseFormUI<T>.() -> Unit) {
        this.AFTER_FORM_INIT_CALL_LIST.add(call)
    }

    fun elements() = ELEMENTS_LIST
    private fun getElementInitValue(): Array<Any?> {
        return Array(ELEMENTS_LIST.size) {
            val element = ELEMENTS_LIST[it]
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

        if (ELEMENTS_LIST.isEmpty()) {
            throw LerverUIException("未获取到有效表单元素!")
        }
        initElements()

        stage.title = title
        stage.setOnCloseRequest {
            clear()
        }
        stage.scene = Scene(root)

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
        val size = ELEMENTS_LIST.size
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
                val element: FormElement<T, *> = ELEMENTS_LIST[elementIndex]
                element.enable()
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

        BASE_BUTTON_LIST.forEach { baseButton ->
            baseButton.btn.setOnAction {
                baseButton.actionFunc(this)
            }
            btnGroup.children.addAll(baseButton.btn)
        }

    }

    protected fun checkElementValue(isUpdate: Boolean = false): Boolean {
        for (element in ELEMENTS_LIST) {
            if (element.fillValue != null) {
                continue
            }
            if (!element.checkRequired()) {
                return false
            }
        }
        return true
    }


    protected fun initValue() {
        ELEMENTS_LIST.forEach { it.initValue() }
    }

    protected fun clear() {
        ELEMENTS_LIST.forEach(Consumer { obj: FormElement<T, *> ->
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
        for (element in ELEMENTS_LIST) {
            if (element.primary) {
                return element.getElementValue()
            }
        }
        return null
    }

    fun getValue(): T? {
        return entity
    }

    /**
     * 将组件数据赋值到实例属性
     *
     * @author lifay
     * @return void
     */
    protected fun elementToProp() {
        try {
            for (element in ELEMENTS_LIST) {
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
            for (element in ELEMENTS_LIST) {
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
        stage.setOnCloseRequest {
            value.handle(it)
            clear()
        }
    }

}

