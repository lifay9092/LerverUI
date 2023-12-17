package cn.lifay.test

import cn.lifay.db.*
import cn.lifay.db.UserDatas.userDatas
import cn.lifay.exception.LerverUIException
import cn.lifay.extension.styleWarn
import cn.lifay.ui.form.BaseFormUI
import cn.lifay.ui.form.CurdUI
import cn.lifay.ui.form.btn.BaseButton
import cn.lifay.ui.form.check.CheckElement
import cn.lifay.ui.form.radio.RadioElement
import cn.lifay.ui.form.select.SelectElement
import cn.lifay.ui.form.text.TextElement
import javafx.scene.control.Button
import org.ktorm.dsl.like
import org.ktorm.entity.*
import org.ktorm.schema.ColumnDeclaring

/**
 *@ClassName UserNewForm
 *@Description TODO
 *@Author lifay
 *@Date 2023/2/4 18:24
 **/
class UserManage : CurdUI<UserData,UserDatas>("用户管理", buildElements = {
    val id = TextElement("ID:", UserData::id, true)
    val name = TextElement("名称:", UserData::name, isTextArea = true, primary = false, initValue = "初始值")
    val type = SelectElement("类型:", UserData::type, SelectTypeEnum.values().toList())
    val child = CheckElement("是否未成年:", UserData::child)
    val sex = RadioElement("性别:", UserData::sex, listOf("男", "女", "中间"))
    addElements(id, name, type, child, sex)

    addCustomButtons(BaseButton<BaseFormUI<UserData>>(Button("测试自定义按钮").styleWarn()) {
        println(it)
    })
}) {

    //分页实现,返回：1-数据总数量 2-根据页码和每页数量的查询逻辑
    override fun pageInit(keyword: String): Pair<EntitySequence<UserData, UserDatas>, ((UserDatas) -> ColumnDeclaring<Boolean>)?> {
        return Pair(DbManage.userDatas) {
            it.name like DbManage.formatLikeKeyword(keyword)
        }
    }


    /**
     * 分页查询函数
     *
     * @param keyword 关键字
     * @param pageIndex 页码
     * @param pageCount 每页数量
     * @return f-总数量 s-分页数据列表
     */
//    override fun  pageInit(): Pair<EntitySequence<UserData, UserDatas>, ((UserDatas) -> ColumnDeclaring<Boolean>)?> {
//        return Pair(DbManage.userDatas,null)
//    }

    //更新操作
    override fun updateDataFunc(entity: UserData): Boolean {
        return true
    }

    //保存操作
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

    //删除操作
    override fun delDataFunc(entity: UserData): Boolean {
        return true
    }


}