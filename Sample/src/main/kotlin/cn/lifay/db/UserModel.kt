package cn.lifay.db

import cn.lifay.test.SelectTypeEnum
import kotlinx.serialization.Serializable
import org.ktorm.dsl.QueryRowSet
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.BaseTable
import org.ktorm.schema.boolean
import org.ktorm.schema.int
import org.ktorm.schema.varchar
import java.util.UUID

@Serializable
data class UserData(
    var id: Int,
    var name: String,
    var type: SelectTypeEnum?,
    var child: Boolean,
    var sex: String,
) {

    override fun toString(): String {
        return this.name
    }
}

object UserDatas : BaseTable<UserData>("USER_INFO") {

    val id = int("id").primaryKey()
    val name = varchar("name")
    val type = varchar("type")
    val child = boolean("child")
    val sex = varchar("sex")

    override fun doCreateEntity(row: QueryRowSet, withReferences: Boolean) = UserData(
        id = row[id] ?: System.currentTimeMillis().toInt(),
        name = row[name]!!,
        type = SelectTypeEnum.valueOf(row[type]!!),
        child = true == row[child],
        sex = row[sex] ?: "",
        )

    val DbManage.userDatas get() = database.sequenceOf(UserDatas)

}