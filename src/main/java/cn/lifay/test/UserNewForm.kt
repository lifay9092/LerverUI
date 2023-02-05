package cn.lifay.test

import cn.lifay.ui.form.FormElement
import cn.lifay.ui.form.FormNewUI

/**
 *@ClassName UserNewForm
 *@Description TODO
 *@Author lifay
 *@Date 2023/2/4 18:24
 **/
class UserNewForm(title: String, t: UserData? = null) : FormNewUI<UserData>(title, t) {
    //    constructor(title: String,t: UserData?):this(title){
//        if (t != null) {
//            this.t = t
//        }
//    }
    override fun buildElements(): List<FormElement<UserData, *>> {
        val id = newTextElement("ID:", UserData::id, true)
        val name = newTextElement("名称:", UserData::name, isTextArea = true)
        val type = newSelectElement("类型:", UserData::type, SelectTypeEnum.values())
        val child = newCheckElement("是否未成年:", UserData::child)

        return listOf(id, name, type, child);
    }


    override fun saveData(data: UserData?) {
        if (data!!.name.isBlank()) {
            throw RuntimeException("名称不能为空!")
        }
        println("保存数据操作:" + data.id + "  " + data.name + "  " + data.type)
    }

    override fun editData(data: UserData?) {
        if (data!!.id == null) {
            throw RuntimeException("主键值不能为空!")
        }
        println("修改数据操作:" + data.id + "  " + data.name + "  " + data.type)
    }

    override fun delData(primaryValue: Any?) {
        checkId(primaryValue)
        println("删除数据操作:$primaryValue")
    }

    override fun datas(list: List<UserData?>?) {
        TODO("Not yet implemented")
    }


}