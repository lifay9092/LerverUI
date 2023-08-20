package cn.lifay.test

import cn.lifay.db.DbManage
import cn.lifay.db.UserData
import cn.lifay.db.UserDatas
import cn.lifay.db.UserDatas.userDatas
import cn.lifay.extension.styleWarn
import cn.lifay.ui.form.FormUINew
import cn.lifay.ui.form.btn.CustomButtonNew
import cn.lifay.ui.form.check.CheckElement
import cn.lifay.ui.form.radio.RadioElement
import cn.lifay.ui.form.select.SelectElement
import cn.lifay.ui.form.text.TextElement
import javafx.scene.control.Button
import org.ktorm.entity.EntitySequence
import org.ktorm.schema.BaseTable

/**
 *@ClassName UserNewForm
 *@Description TODO
 *@Author lifay
 *@Date 2023/2/4 18:24
 **/
class UserFormNEW(t: UserData? = null) : FormUINew<UserData>("用户管理", t, buildElements = {
    val id = TextElement("ID:", UserData::id, true)
    val name = TextElement("名称:", UserData::name, isTextArea = true, primary = false, initValue = "初始值")
    val type = SelectElement("类型:", UserData::type, SelectTypeEnum.values().toList())
    val child = CheckElement("是否未成年:", UserData::child)
    val sex = RadioElement("性别:", UserData::sex, listOf("男", "女", "中间"))
    addElement(id, name, type, child, sex)

    addCustomBtn(CustomButtonNew(Button("测试自定义按钮").styleWarn()) {
        println(it)
    })
}) {
    //    constructor(title: String,t: UserData?):this(title){
//        if (t != null) {
//            this.t = t
//        }
//    }
    override  fun dbObject(): BaseTable<UserData> {
        return UserDatas
    }

    override fun add():  List<UserData> {
        return listOf(
            UserData(1, "111111", SelectTypeEnum.A, true, "男"),
            UserData(2, "2222", SelectTypeEnum.B, false, "女"),
            UserData(3, "33333", SelectTypeEnum.C, true, "男")
        )
    }


}