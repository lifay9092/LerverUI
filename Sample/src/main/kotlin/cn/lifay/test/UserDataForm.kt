package cn.lifay.test

import cn.lifay.db.DbManage.add
import cn.lifay.db.DbManage.update
import cn.lifay.db.UserData
import cn.lifay.db.UserDatas
import cn.lifay.exception.LerverUIException
import cn.lifay.extension.styleWarn
import cn.lifay.ui.form.DataFormUI
import cn.lifay.ui.form.btn.BaseButton
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
class UserDataForm(t: UserData? = null, isUpdate: Boolean = false) :
    DataFormUI<UserData>(_isUpdate = isUpdate, buildFormUI = {
        if (t != null) {
            defaultEntity(t)
        }
        val id = TextElement("ID:", UserData::id, true)
        id.fillValue = 666

        val name = TextElement("名称:", UserData::name, isTextArea = true, primary = false, initValue = "初始值")
        val type = SelectElement("类型:", UserData::type, SelectTypeEnum.values().toList())
        val child = CheckElement("是否未成年:", UserData::child)
        val sex = RadioElement("性别:", UserData::sex, listOf("男", "女", "中间"))
        addElements(id, name, type, child, sex)

        addCustomButtons(BaseButton(Button("测试自定义按钮").styleWarn()) {
            println(it)
        })
    }) {

    val dataList = mutableListOf<UserData>(
//        UserData(1, "111111", SelectTypeEnum.A, true, "男"),
//        UserData(2, "2222", SelectTypeEnum.B, false, "女"),
//        UserData(3, "33333", SelectTypeEnum.C, true, "男")
    )
    //保存操作
    override fun saveData(entity: UserData): Boolean {
        if (entity!!.name!!.isBlank()) {
            throw LerverUIException("名称不能为空!")
        }
        UserDatas.add(entity)
        return true
    }

    //更新操作
    override fun updateData(entity: UserData): Boolean {
        UserDatas.update(entity)
        return true
    }

}