package cn.lifay.test

import cn.lifay.db.DbManage
import cn.lifay.db.Department
import cn.lifay.db.Departments
import cn.lifay.db.UserData
import cn.lifay.db.UserDatas.userDatas
import cn.lifay.exception.LerverUIException
import cn.lifay.extension.styleWarn
import cn.lifay.ui.form.CurdUI
import cn.lifay.ui.form.DataFormUI
import cn.lifay.ui.form.btn.BaseButton
import cn.lifay.ui.form.check.CheckElement
import cn.lifay.ui.form.radio.RadioElement
import cn.lifay.ui.form.select.SelectElement
import cn.lifay.ui.form.text.TextElement
import javafx.scene.control.Button
import org.ktorm.entity.*

/**
 *@ClassName UserNewForm
 *@Description TODO
 *@Author lifay
 *@Date 2023/2/4 18:24
 **/
class UserManage() : CurdUI<UserData>("用户管理", buildElements = {
    val id = TextElement("ID:", UserData::id, true)
    val name = TextElement("名称:", UserData::name, isTextArea = true, primary = false, initValue = "初始值")
    val type = SelectElement("类型:", UserData::type, SelectTypeEnum.values().toList())
    val child = CheckElement("是否未成年:", UserData::child)
    val sex = RadioElement("性别:", UserData::sex, listOf("男", "女", "中间"))
    addElements(id, name, type, child, sex)

    addCustomButtons(BaseButton<DataFormUI<UserData>>(Button("测试自定义按钮").styleWarn()) {
        println(it)
    })
}) {


    override fun pageDataFunc(pageIndex: Int, pageCount: Int): Pair<Int, Collection<UserData>> {
        return Pair(
            DbManage.userDatas.totalRecordsInAllPages, DbManage.userDatas.drop(pageIndex * pageCount)
                .take(pageCount).toList()
        )
    }

    override fun updateDataFunc(entity: UserData): Boolean {
        return true
    }

    override fun saveDataFunc(entity: UserData): Boolean {
        val department = Department {}
        DbManage.database.sequenceOf(Departments).add(department)
        if (entity!!.name!!.isBlank()) {
            throw LerverUIException("名称不能为空!")
        }
        Thread.sleep(1000)
        println("保存数据操作:$entity")
//        showMessage("保存数据操作:$data",1000)
        return true
    }


    override fun delDataFunc(entity: UserData): Boolean {
        return true
    }

//    override fun add():  List<UserData> {
//        return listOf(
//            UserData(1, "111111", SelectTypeEnum.A, true, "男"),
//            UserData(2, "2222", SelectTypeEnum.B, false, "女"),
//            UserData(3, "33333", SelectTypeEnum.C, true, "男")
//        )
//    }


}