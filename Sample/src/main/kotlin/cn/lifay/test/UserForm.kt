package cn.lifay.test

import cn.lifay.db.UserData
import cn.lifay.exception.LerverUIException
import cn.lifay.extension.styleWarn
import cn.lifay.ui.form.FormUI
import cn.lifay.ui.form.btn.CustomButton
import cn.lifay.ui.form.check.CheckElement
import cn.lifay.ui.form.radio.RadioElement
import cn.lifay.ui.form.select.SelectElement
import cn.lifay.ui.form.text.TextElement
import javafx.scene.control.Button

/**
 *@ClassName UserNewForm
 *@Description TODO
 *@Author lifay
 *@Date 2023/2/4 18:24
 **/
class UserForm(t: UserData? = null) : FormUI<UserData>("用户管理", t, buildElements = {

    val id = TextElement("ID:", UserData::id, true)
    val name = TextElement("名称:", UserData::name, isTextArea = true, primary = false, initValue = "初始值")
    val type = SelectElement("类型:", UserData::type, SelectTypeEnum.values().toList())
    val child = CheckElement("是否未成年:", UserData::child)
    val sex = RadioElement("性别:", UserData::sex, listOf("男", "女", "中间"))
    addElement(id, name, type, child, sex)

    addCustomBtn(CustomButton(Button("测试自定义按钮").styleWarn()) {
        println(it)
        this.refreshTable()
    })
}) {
    //    constructor(title: String,t: UserData?):this(title){
//        if (t != null) {
//            this.t = t
//        }
//    }

    val dataList = mutableListOf<UserData>(
//        UserData(1, "111111", SelectTypeEnum.A, true, "男"),
//        UserData(2, "2222", SelectTypeEnum.B, false, "女"),
//        UserData(3, "33333", SelectTypeEnum.C, true, "男")
    )
    override fun saveData(data: UserData?) {
        if (data!!.name!!.isBlank()) {
            throw LerverUIException("名称不能为空!")
        }
        Thread.sleep(1000)
        println("保存数据操作:$data")
        dataList.add(UserData(4,"4444",SelectTypeEnum.A,true,"女"))
//        showMessage("保存数据操作:$data",1000)
    }

    override fun delData(primaryValue: Any?) {
        println("删除数据操作:$primaryValue")
        showNotification("保存数据操作:$primaryValue")
    }


    override fun datas(): List<UserData> {

        return dataList
    }


}