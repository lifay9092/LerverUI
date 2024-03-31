package cn.lifay.ui.form

import cn.lifay.extension.asyncTaskLoading
import cn.lifay.extension.icon
import cn.lifay.extension.stylePrimary
import cn.lifay.ui.form.btn.BaseButton
import javafx.scene.control.Button
import org.kordamp.ikonli.feather.Feather


/**
 * Form类示例：
 *
 *```
 * class UserForm(t: UserData? = null) : FormUI<UserData>("用户管理", t)
 * class AddrForm(t: HttpAddr? = null) : FormUI<HttpAddr>("地址管理", t)
 *```
 * 【元素类使用示例】
 * dada class:
 * ```11111111111111
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
abstract class DataFormUI<T : Any>(
    open val _title: String? = null,
    open val _isUpdate: Boolean = false,
    buildFormUI: BaseFormUI<T>.() -> Unit,
) : BaseFormUI<T>(if (_title.isNullOrBlank()) if (_isUpdate) "编辑" else "新增" else _title, buildFormUI) {

    //    protected lateinit var elements: ObservableList<FormElement<T, *>>
    private var saveBtn: BaseButton<BaseFormUI<T>>

    init {
        //   println("DataFormUI init isUpdate:${_isUpdate}")
        if (_isUpdate) {
            saveBtn = BaseButton(Button("更新").stylePrimary().icon(Feather.EDIT)) {
                updateFunc()
            }
        } else {
            saveBtn = BaseButton(Button("保存").stylePrimary().icon(Feather.CHECK)) {
                saveFunc()
            }
        }

        val customBtns = listOf(
            saveBtn,
            clearBtn()
        ).map { baseButton ->
            baseButton.btn.setOnAction {
                baseButton.actionFunc(this)
            }
            baseButton.btn
        }
        btnGroup.children.addAll(0, customBtns)
        if (_isUpdate) {
            ELEMENTS_LIST.forEach {
                if (it.isDisable) {
                    it.disable()
                } else if (it.primary) {
                    it.disable()
                }
            }
        }
    }

    /**
     * 保存数据函数
     *
     * @param entity 数据
     * @return 执行结果
     */
    abstract fun saveData(entity: T): Boolean

    /**
     * 更新数据函数
     *
     * @param entity 数据
     * @return 执行结果
     */
    abstract fun updateData(entity: T): Boolean

    private fun saveFunc() {
        asyncTaskLoading(getWindow(), "保存中") {
            try {
                saveBtn.disable()
                //检查
                if (!checkElementValue(true)) {
                    return@asyncTaskLoading
                }
                //从元素赋值到实例
                super.elementToProp()
                //执行保存操作
                saveData(entity!!)
                showMessage("保存成功")
                super.clear()
            } catch (e: Exception) {
                e.printStackTrace()
                showErrMessage("保存失败:" + e.message)
            } finally {
                saveBtn.enable()
            }
        }
    }

    private fun updateFunc() {
        asyncTaskLoading(super.ROOT_PANE.scene.window, "更新中") {
            try {
                saveBtn.disable()
                //检查
                if (!checkElementValue(true)) {
                    return@asyncTaskLoading
                }
                //从元素赋值到实例
                elementToProp()
                //执行保存操作
                updateData(entity!!)
                showMessage("更新成功")
            } catch (e: Exception) {
                e.printStackTrace()
                showErrMessage("更新失败:" + e.message)
            } finally {
                saveBtn.enable()
            }
        }
    }
//
//    private fun saveFunc() {
//        asyncTaskLoading(super.ROOT_PANE.scene.window, "保存中") {
//            try {
//                saveBtn.disable()
//                //检查
//                if (!checkElementValue()) {
//                    return@asyncTaskLoading
//                }
//                //从元素赋值到实例
//                super.elementToProp()
//                //执行保存操作
//                DbManage.insert(table){
//                    table.columns.forEach { col ->
//                        for (element in elements()) {
//                            if (element.getPropName() == col.name) {
//                                val elementValue = element.getElementValue()
//                                println("name:${col.name} v:${elementValue}")
//                                elementValue?.let {
//                                    when (it::class.java) {
//                                        java.lang.Boolean::class.java -> {
//                                            set(col as Column<Boolean>, it as Boolean)
//                                        }
//                                        java.lang.String::class.java -> {
//                                            set(col as Column<String>, it as String)
//                                        }
//
//                                        java.lang.Integer::class.java -> {
//                                            set(col as Column<Integer>, it as Integer)
//                                        }
//                                        java.lang.Double::class.java -> {
//                                            set(col as Column<Double>, it as Double)
//                                        }
//
//                                        java.lang.Float::class.java -> {
//                                            set(col as Column<Float>, it as Float)
//                                        }
//                                        java.lang.Long::class.java -> {
//                                            set(col as Column<Long>, it as Long)
//                                        }
//                                        else -> {
//                                            println("not surport")
//                                            set(col as Column<String>, it.toString())
//                                        }
//                                    }
//                                }
//                                break
//                            }
//                        }
//                    }
//                }
//                showMessage("保存成功")
//                super.clear()
//            } catch (e: Exception) {
//                e.printStackTrace()
//                showErrMessage("保存失败:" + e.message)
//            } finally {
//                saveBtn.enable()
//            }
//        }
//    }
//
//    private fun updateFunc() {
//        asyncTaskLoading(super.ROOT_PANE.scene.window, "更新中") {
//            try {
//                saveBtn.disable()
//                //检查
//                if (!checkElementValue(true)) {
//                    return@asyncTaskLoading
//                }
//                //从元素赋值到实例
//                elementToProp()
//                //执行保存操作
//                DbManage.update(table){
//                    table.columns.forEach { col ->
//                        for (element in elements()) {
//                            if (element.getPropName() == col.name) {
//                                val elementValue = element.getElementValue()
//                                println("name:${col.name} v:${elementValue}")
//                                elementValue?.let {
//
//                                    when (it::class.java) {
//                                        java.lang.Boolean::class.java -> {
//                                            set(col as Column<Boolean> , it as Boolean)
//                                        }
//                                        java.lang.String::class.java -> {
//                                            set(col as Column<String> , it as String)
//                                            if (element.primary) {
//                                                where {
//                                                    col eq it
//                                                }
//                                            }
//                                        }
//
//                                        java.lang.Integer::class.java -> {
//                                            set(col as Column<Integer> , it as Integer)
//                                            if (element.primary) {
//                                                where {
//                                                    col eq it
//                                                }
//                                            }
//                                        }
//                                        java.lang.Double::class.java -> {
//                                            set(col as Column<Double> , it as Double)
//                                        }
//
//                                        java.lang.Float::class.java -> {
//                                            set(col as Column<Float> , it as Float)
//                                        }
//                                        java.lang.Long::class.java -> {
//                                            set(col as Column<Long> , it as Long)
//                                            if (element.primary) {
//                                                where {
//                                                    col eq it
//                                                }
//                                            }
//                                        }
//                                        else -> {
//                                            println("not surport")
//                                            set(col as Column<String> , it.toString())
//                                        }
//                                    }
//                                }
//                                break
//                            }
//                        }
//
//                    }
//                }
//                showMessage("更新成功")
//            } catch (e: Exception) {
//                e.printStackTrace()
//                showErrMessage("更新失败:" + e.message)
//            } finally {
//                saveBtn.enable()
//            }
//        }
//    }

}

