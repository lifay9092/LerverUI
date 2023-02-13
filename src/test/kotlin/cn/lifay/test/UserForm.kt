package cn.lifay.test

import cn.lifay.ui.form.FormElement
import cn.lifay.ui.form.FormUI
import cn.lifay.ui.form.check.CheckElement
import cn.lifay.ui.form.radio.RadioElement
import cn.lifay.ui.form.select.SelectElement
import cn.lifay.ui.form.text.TextElement

/**
 *@ClassName UserNewForm
 *@Description TODO
 *@Author lifay
 *@Date 2023/2/4 18:24
 **/
class UserForm(title: String, t: UserData? = null) : FormUI<UserData>(title, t) {
    //    constructor(title: String,t: UserData?):this(title){
//        if (t != null) {
//            this.t = t
//        }
//    }
    override fun buildElements(): List<FormElement<UserData, *>> {
        val id = TextElement("ID:", UserData::id, true)
        val name = TextElement("名称:", UserData::name, isTextArea = true, required = true)
        val type = SelectElement("类型:", UserData::type, SelectTypeEnum.values().toList())
        val child = CheckElement("是否未成年:", UserData::child)
        val sex = RadioElement("性别:", UserData::sex, listOf("男", "女"))

        /*        val name = newTextElement("名称:", UserData::name, isTextArea = true, required = true)
        val type = newSelectElement("类型:", UserData::type, SelectTypeEnum.values())
        val child = newCheckElement("是否未成年:", UserData::child)
        val sex = newRadioElement("性别:", UserData::sex, listOf("男","女"))
*/
        return listOf(id, name, type, child, sex);
    }

    override fun saveData(data: UserData?) {
        if (data!!.name!!.isBlank()) {
            throw RuntimeException("名称不能为空!")
        }
        Thread.sleep(3000)
        println("保存数据操作:$data")
    }

    override fun editData(data: UserData?) {
        if (data!!.id == null) {
            throw RuntimeException("主键值不能为空!")
        }
        println("修改数据操作:$data")
    }

    override fun delData(primaryValue: Any?) {
        checkId(primaryValue)
        println("删除数据操作:$primaryValue")
    }

    override fun datas(): List<UserData> {
        return listOf(
            UserData(1, "111111", SelectTypeEnum.A, true, "男"),
            UserData(2, "2222", SelectTypeEnum.B, false, "女"),
            UserData(3, "33333", SelectTypeEnum.C, true, "男")
        )
    }


}