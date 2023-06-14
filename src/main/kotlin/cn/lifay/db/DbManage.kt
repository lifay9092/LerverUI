package cn.lifay.db

import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.*
import org.ktorm.logging.Logger
import org.ktorm.schema.ColumnDeclaring
import org.ktorm.schema.Table

/**
 *@ClassName DbManage
 *@Description TODO
 *@Author lifay
 *@Date 2023/6/14 20:44
 **/
class DbManage<E : Entity<E>>(table: Table<E>) {

    var SEQUENCE: EntitySequence<E, Table<E>>

    init {
        SEQUENCE = database.sequenceOf(table)
    }

    companion object {

        lateinit var database: Database

        fun Config(url: String, user: String, password: String, logger: Logger) {
            database = Database.connect(
                url = url,
                user = user,
                password = password,
                logger = logger
            )
        }

        fun VerifyConfig() {
            if (!this::database.isInitialized) {
                throw Exception("请先初始化数据库连接:Config()")
            }
        }

        inline fun <reified T : Table<*>> from(t: T): QuerySource {
            return database.from(t)
        }

        inline fun <reified T : Table<*>> insert(t: T, noinline block: AssignmentsBuilder.(T) -> Unit): Int {
            return database.insert(t, block)
        }

        inline fun <reified T : Table<*>> update(t: T, noinline block: UpdateStatementBuilder.(T) -> Unit): Int {
            return database.update(t, block)
        }

        inline fun <reified T : Table<*>> delete(t: T, noinline predicate: (T) -> ColumnDeclaring<Boolean>): Int {
            return database.delete(t, predicate)
        }

    }

    fun find(predicate: (Table<E>) -> ColumnDeclaring<Boolean>): E? {
        return SEQUENCE.find(predicate)
    }

    fun filter(predicate: (Table<E>) -> ColumnDeclaring<Boolean>): EntitySequence<E, Table<E>> {
        return SEQUENCE.filter(predicate)
    }

    fun add(entity: E): Int {
        return SEQUENCE.add(entity)
    }

    fun update(entity: E): Int {
        return SEQUENCE.update(entity)
    }

    fun delete(predicate: (Table<E>) -> ColumnDeclaring<Boolean>): Boolean {
        val entity = find(predicate) ?: return false
        entity.delete()
        return true
    }

}