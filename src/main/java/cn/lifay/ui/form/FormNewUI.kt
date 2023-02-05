package cn.lifay.ui.form

import cn.lifay.ui.form.check.CheckElement
import cn.lifay.ui.form.select.SelectElement
import cn.lifay.ui.form.text.TextNewElement
import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.geometry.HPos
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.scene.control.Button
import javafx.scene.control.ButtonType
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.stage.Stage
import java.lang.reflect.ParameterizedType
import java.util.function.Consumer
import kotlin.reflect.KMutableProperty1


/**
 *@ClassName FormNewUI
 *@Description TODO
 *@Author lifay
 *@Date 2023/2/4 18:15
 **/
abstract class FormNewUI<T : Any>(title: String, t: T?) : Stage() {

    protected var t: T? = null
    private val pane = GridPane()
    protected var elements: ObservableList<FormElement<T, *>> = FXCollections.observableArrayList()
    protected var saveBtn: Button = Button("保存")
    protected var editBtn: Button = Button("修改")
    protected var delBtn: Button = Button("删除")
    protected var clearBtn: Button = Button("清空")
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
//
//    fun FormUI(title: String, clazz: Class<T?>) {
//        t = try {
//            clazz.getDeclaredConstructor().newInstance()
//        } catch (e: Exception) {
//            e.printStackTrace()
//            throw RuntimeException(e)
//        }
//        formInit(title)
//    }
//    constructor(title: String,t: T?):this(title){
//        if (t != null) {
//            this.t = t
//        }
//    }

    init {
        this.title = title
        formInit(title)
        if (t == null) {
            val type = javaClass.genericSuperclass as ParameterizedType
            val clazz = type.actualTypeArguments[0] as Class<T>
            this.t = clazz.getConstructor().newInstance()
        } else {
            this.t = t
            propToElement()
        }
    }

//    fun FormUI(title: String, t: T?) {
//        this.t = t
//        formInit(title)
//        propToElement()
//    }

    private fun formInit(title: String) {
        pane.alignment = Pos.CENTER
        pane.hgap = 10.0
        pane.vgap = 10.0
        pane.padding = Insets(25.0, 25.0, 25.0, 25.0)
        this.setTitle(title)
        this.setScene(Scene(pane))
        initElements()
    }
/*    inline fun <reified T,R> newTextElement(label: String?,
                                  primary: Boolean = false,
                                           setFunc: (User,R) -> Unit,
    getFunc:(User) -> R): TextNewElement<T, R> {
        val element = TextNewElement<T, R>(label, fieldName)
        return element
    }*/

    inline fun <reified T, R : Any> newCheckElement(
        label: String,
        property: KMutableProperty1<T, R>
    ): CheckElement<T, R> {
        val element = CheckElement(label, property)
        return element
    }

    inline fun <reified T, R : Any> newTextElement(
        label: String,
        property: KMutableProperty1<T, R>,
        primary: Boolean = false,
        isTextArea: Boolean = false
    ): TextNewElement<T, R> {
        val element = TextNewElement(label, property, primary, isTextArea)
        return element
    }

    inline fun <reified T, R : Any> newSelectElement(
        label: String,
        property: KMutableProperty1<T, R>,
        items: Array<R>
    ): SelectElement<T, R> {
        return newSelectElement(label, property, items.toList())
    }

    inline fun <reified T, R : Any> newSelectElement(
        label: String,
        property: KMutableProperty1<T, R>,
        items: Collection<R>
    ): SelectElement<T, R> {
        val element = SelectElement(label, property, items)
        return element
    }


    /**
     * 加载布局
     *
     * @author lifay
     * @return
     */
    fun initElements() {
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
                pane.add(element, y, x)
                elementIndex++
                if (elementIndex == size) {
                    break
                }
            }
        }
        pane.add(btnGroup, Math.round((h / 2).toFloat()), v)
        //布局按钮组
        btnGroup.alignment = Pos.BOTTOM_RIGHT
        btnGroup.spacing = 20.0
        btnGroup.padding = Insets(20.0)
        btnGroup.children.addAll(saveBtn, editBtn, delBtn, clearBtn)
        //pane.getChildren().add(btnGroup);
        //保存
        saveBtn.setOnMouseClicked { mouseEvent ->
            if (mouseEvent.getClickCount() === 1) {
                try {
                    saveBtn.setDisable(true)
                    //从元素赋值到实例
                    elementToProp()
                    //执行保存操作
                    saveData(t)
                    Platform.runLater {
                        Alert(Alert.AlertType.INFORMATION, "保存成功").show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Platform.runLater {
                        Alert(
                            Alert.AlertType.ERROR,
                            "保存失败:" + e.message
                        ).show()
                    }
                } finally {
                    saveBtn.setDisable(false)
                }
            }
        }
        //编辑
        editBtn.setOnMouseClicked { mouseEvent ->
            if (mouseEvent.getClickCount() === 1) {
                try {
                    editBtn.setDisable(true)
                    //从元素赋值到实例
                    elementToProp()
                    //执行保存操作
                    editData(t)
                    Platform.runLater {
                        Alert(Alert.AlertType.INFORMATION, "编辑成功").show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Platform.runLater {
                        Alert(
                            Alert.AlertType.ERROR,
                            "编辑失败:" + e.message
                        ).show()
                    }
                } finally {
                    editBtn.setDisable(false)
                }
            }
        }
        //删除
        delBtn.setOnMouseClicked { mouseEvent ->
            if (mouseEvent.getClickCount() === 1) {
                try {
                    delBtn.setDisable(true)
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
                        clear()
                        Platform.runLater {
                            Alert(Alert.AlertType.INFORMATION, "删除成功").show()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Platform.runLater {
                        Alert(
                            Alert.AlertType.ERROR,
                            "删除失败:" + e.message
                        ).show()
                    }
                } finally {
                    delBtn.setDisable(false)
                }
            }
        }
        //清空
        clearBtn.setOnMouseClicked { mouseEvent ->
            if (mouseEvent.getClickCount() === 1) {
                try {
                    clearBtn.setDisable(true)
                    clear()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Platform.runLater {
                        Alert(
                            Alert.AlertType.ERROR,
                            "操作失败:" + e.message
                        ).show()
                    }
                } finally {
                    clearBtn.setDisable(false)
                }
            }
        }
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
                element.setEle(t!!)
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

    abstract fun datas(list: List<T?>?)

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
                is Long -> id == 0;
                else -> throw RuntimeException("不支持当前类型:" + id.javaClass);
            };
            if (blank) {
                throw RuntimeException("主键值不能为空!");
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Platform.runLater {
                Alert(Alert.AlertType.ERROR, e.message).show()
            }
        }
    }
}

