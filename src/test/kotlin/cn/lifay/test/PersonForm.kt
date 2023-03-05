package cn.lifay.test

import cn.lifay.ui.DelegateProp
import cn.lifay.ui.form.FormElement
import cn.lifay.ui.form.FormUI
import cn.lifay.ui.form.check.CheckElement
import cn.lifay.ui.form.text.TextElement
import java.net.URL
import java.util.*


/**
 * PersonForm TODO
 * @author lifay
 * @date 2023/3/2 21:24
 **/
class PersonForm(t: Person? = null) : FormUI<Person>("人", t) {
    override fun buildElements(): List<FormElement<Person, *>> {

        val id = TextElement<Person, Int>("ID：", DelegateProp("id"), primary = true)
        val name = TextElement<Person, String>("姓名：", DelegateProp("name"))
        val child = CheckElement<Person, Boolean>("成年：", DelegateProp("child"))
        return listOf(id, name, child)
    }

    override fun datas(): List<Person> {
        return listOf(
            Person(111, "1111", true),
            Person(222, "2222", false)
        )
    }

    override fun delData(primaryValue: Any?) {
        println(entity)

    }

    override fun initialize(p0: URL?, p1: ResourceBundle?) {

    }

    override fun editData(t: Person?) {
        println(t)

    }

    override fun saveData(t: Person?) {
        println(t)
    }
}