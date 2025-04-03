package cn.lifay.ui.form

import cn.lifay.db.DbManage.add
import org.ktorm.schema.BaseTable

class Test {
    fun dbObject(): BaseTable<Unit>? {

        return null
    }

    fun test(entity: Unit) {
        dbObject()?.let {
            it.add(entity)
        }
    }
}