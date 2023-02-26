package cn.lifay.ui.form

import cn.lifay.extension.*
import cn.lifay.ui.BaseView
import cn.lifay.ui.form.radio.RadioElement
import cn.lifay.ui.message.Message
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.geometry.HPos
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.WindowEvent
import java.lang.reflect.ParameterizedType
import java.util.function.Consumer
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KMutableProperty1


/**
 *@ClassName FormUI
 *@Description
 *@Author lifay
 *@Date 2023/2/4 18:15
 **/
abstract class FormUI<T : Any>(title: String, t: T?) : BaseView<VBox>() {

    protected var t: T? = null
    private val stage = Stage()
    private var root = VBox(10.0)
    private val form = GridPane()
    private val table = TableView<T>()
    protected var elements: ObservableList<FormElement<T, *>> = FXCollections.observableArrayList()
    protected var saveBtn: Button = Button("保存").styleInfo()
    protected var editBtn: Button = Button("修改").stylePrimary()
    protected var delBtn: Button = Button("删除").styleDanger()
    protected var clearBtn: Button = Button("清空").styleWarn()
    protected var btnGroup = HBox(20.0)

    /*
        companion object{
            inline operator fun <reified T : Any> invoke(): T {
                println("伴生对象方法")
                val kClass = T::class.java.newInstance()
                return kClass
            }
        }
    */

    private fun FormUI() {}

    init {
        uiInit(title)
        if (t == null) {
            val type = javaClass.genericSuperclass as ParameterizedType
            val clazz = type.actualTypeArguments[0] as Class<T>
            this.t = clazz.getConstructor().newInstance()
        } else {
            refreshForm(t)
        }
    }

    /**
     * 注册根容器
     * @author lifay
     * @return
     */
    override fun rootPane(): KMutableProperty0<VBox> {
        return this::root
    }

    /*
        界面初始化
     */
    private fun uiInit(title: String) {
        stage.initModality(Modality.APPLICATION_MODAL)
        root.children.addAll(form, table)

        //表单布局
        form.alignment = Pos.CENTER
        form.hgap = 10.0
        form.vgap = 10.0
        form.padding = Insets(25.0, 25.0, 25.0, 25.0)
        initElements()

        //表格布局
        this.table.apply {
            padding = Insets(1.0, 2.0, 10.0, 2.0)
            columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
            columns.addAll(tableHeadColumns())
            refreshTable()
            setRowFactory {
                val row = TableRow<T>()
                row.setOnMouseClicked { event ->
                    if (event.clickCount == 2) {
                        refreshForm(row.item)
                    }
                }
                return@setRowFactory row
            }
        }
        stage.title = title
        stage.scene = Scene(root)
    }

    private fun refreshForm(t: T) {
        this.t = t
        propToElement()
    }

    private fun refreshTable() {
        platformRun {
            table.items.clear()
            table.items.addAll(datas())
        }
    }

    /*
        表头设置
     */
    private fun tableHeadColumns(): List<TableColumn<T, out Any>> {
        return elements.map { it.getTableHead() }.toList()
    }

    inline fun <reified T> newRadioElement(
        label: String,
        property: KMutableProperty1<T, String?>,
        items: List<String>
    ): RadioElement<T> {
        val element = RadioElement(label, property, items)
        return element
    }


    /**
     * 加载布局
     *
     * @author lifay
     * @return
     */
    private fun initElements() {
        /*网格布局：元素*/
        val elementList: List<FormElement<T, *>> = buildElements()
        elements.addAll(elementList)
        //网格布局 算法 前提：纵向数量<=横向数量
        var h = 0
        var v = 0
        val size = elementList.size
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
        for (x in 0 until h) {
            for (y in 0 until v) {
                //System.out.println("x="+x + " y=" + y);
                val element: FormElement<T, *> = elements[elementIndex]
                GridPane.setHalignment(element, HPos.LEFT)
                form.add(element, y, x)
                elementIndex++
                if (elementIndex == size) {
                    break
                }
            }
        }
        form.add(btnGroup, Math.round((h / 2).toFloat()), v)
        //布局按钮组
        btnGroup.alignment = Pos.BOTTOM_RIGHT
        btnGroup.spacing = 20.0
        btnGroup.padding = Insets(20.0)
        btnGroup.children.addAll(saveBtn, editBtn, delBtn, clearBtn)
        //pane.getChildren().add(btnGroup);
        //保存
        saveBtn.setOnMouseClicked { mouseEvent ->
            if (mouseEvent.clickCount == 1) {
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
                        saveData(t)
                        showMessage("保存成功", Message.Type.SUCCESS)
                        refreshTable()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        showMessage("保存失败:" + e.message, Message.Type.ERROR)
                    } finally {
                        saveBtn.isDisable = false
                    }
                }
            }
        }
        //编辑
        editBtn.setOnMouseClicked { mouseEvent ->
            if (mouseEvent.clickCount == 1) {
                asyncTaskLoading(stage) {
                    try {
                        editBtn.isDisable = true
                        //检查
                        if (!checkElementValue()) {
                            return@asyncTaskLoading
                        }
                        //从元素赋值到实例
                        elementToProp()
                        //执行保存操作
                        editData(t)
                        showMessage("编辑成功", Message.Type.SUCCESS)
                        refreshTable()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        showMessage("编辑失败:" + e.message, Message.Type.ERROR)
                    } finally {
                        editBtn.isDisable = false
                    }
                }
            }
        }
        //删除
        delBtn.setOnMouseClicked { mouseEvent ->
            if (mouseEvent.clickCount == 1) {
                asyncTaskLoading(stage) {
                    try {
                        delBtn.isDisable = true
                        //确认
                        val alert =
                            Alert(Alert.AlertType.CONFIRMATION, "是否删除?")
                        val buttonType = alert.showAndWait()
                        if (buttonType.isPresent && buttonType.get() == ButtonType.OK) {
                            //从元素赋值到实例
                            elementToProp()
                            //获取主键值
                            val idValue = getPrimaryValue() ?: throw Exception("未获取到主键属性!")
                            //执行保存操作
                            delData(idValue)
                            showMessage("删除成功", Message.Type.SUCCESS)
                            clear()
                            refreshTable()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        showMessage("删除失败:" + e.message, Message.Type.ERROR)
                    } finally {
                        delBtn.isDisable = false
                    }
                }
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
                    showMessage("操作失败:" + e.message, Message.Type.ERROR)
                } finally {
                    clearBtn.isDisable = false
                }
            }
        }
    }

    private fun checkElementValue(): Boolean {
        for (element in elements) {
            if (!element.checkRequired()) {
                return false
            }
        }
        return true
    }

    private fun clear() {
        elements.forEach(Consumer<FormElement<T, *>> { obj: FormElement<T, *> -> obj.clear() })
    }

    private fun getPrimaryValue(): Any? {
        for (element in elements) {
            if (element.primary) {
                return element.elementValue
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
                element.setProp(t!!)
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
                element.setEle(this.t!!)
                if (element.primary) {
                    element.disable()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw RuntimeException(e)
        }
    }

    abstract fun buildElements(): List<FormElement<T, *>>

    abstract fun datas(): List<T>

    abstract fun saveData(t: T?)

    abstract fun editData(t: T?)

    abstract fun delData(primaryValue: Any?)

    fun checkId(id: Any?) {
        try {
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
        } catch (e: Exception) {
            e.printStackTrace()
            platformRun {
                Alert(Alert.AlertType.ERROR, e.message).show()
            }
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
