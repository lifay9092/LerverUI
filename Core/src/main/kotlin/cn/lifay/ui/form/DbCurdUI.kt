package cn.lifay.ui.form

import org.ktorm.entity.*
import org.ktorm.schema.BaseTable
import org.ktorm.schema.ColumnDeclaring


/**
 *@ClassName DbCurdUI
 *@Description 适用于从数据库快速封装CURD
 *@Author lifay
 *@Date 2023/8/19 22:50
 **/
abstract class DbCurdUI<T : Any, E : BaseTable<T>>(
    title: String,
    isInitAdd: Boolean = true,
    isInitEdit: Boolean = true,
    isInitDel: Boolean = true,
    buildElements: CurdUI<T>.() -> Unit,
) : CurdUI<T>(title, isInitAdd, isInitEdit, isInitDel, buildElements) {

    /**
     * 分页查询函数
     *
     * @param keyword 关键字
     * @return f-ObjectBaseTable s-关键字匹配逻辑
     */
    abstract fun dbPageInit(keyword: String): Pair<EntitySequence<T, E>, ((E) -> ColumnDeclaring<Boolean>)?>

    override fun pageInit(keyword: String, index: Int, count: Int): Pair<Int, Collection<T>> {
        val pageInit = dbPageInit(keyword)
        val entitySequence = pageInit.first
        val filterFunc = pageInit.second
        if (filterFunc != null) {
            return Pair(
                entitySequence.totalRecordsInAllPages, entitySequence.filter {
                    return@filter filterFunc(it)
                }.drop(index * count)
                    .take(count).toList()
            )
        } else {
            totalCountText.text = "共 ${entitySequence.totalRecordsInAllPages} 条"
            dataTable.items.addAll(
                entitySequence.drop(index * count)
                    .take(count).toList()
            )
            return Pair(
                entitySequence.totalRecordsInAllPages, entitySequence.drop(index * count)
                    .take(count).toList()
            )
        }
    }

}