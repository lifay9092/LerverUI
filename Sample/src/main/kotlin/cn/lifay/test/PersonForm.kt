package cn.lifay.test

import cn.lifay.ui.DelegateProp
import cn.lifay.ui.form.DataFormUI
import cn.lifay.ui.form.check.CheckElement
import cn.lifay.ui.form.text.TextElement


/**
 * PersonForm TODO
 * @author lifay
 * @date 2023/3/2 21:24
 **/
class PersonForm(t: Person? = null) : DataFormUI<Person>(_title = "人", buildFormUI = {

    t?.let { defaultEntity(it) }

    val id = TextElement<Person, Int>("ID：", DelegateProp("id"), primary = true)
    val name = TextElement<Person, String>("姓名：", DelegateProp("name"))
    val child = CheckElement<Person, Boolean>("成年：", DelegateProp("child"))

    addElements(id, name, child)
}) {


    override fun saveData(entity: Person): Boolean {
        println(_title)
        return true
    }

    /**
     * 更新数据函数
     *
     * @param entity 数据
     * @return 执行结果
     */
    override fun updateData(entity: Person): Boolean {
        return true
    }
}