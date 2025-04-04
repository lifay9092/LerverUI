package cn.lifay.extension

import cn.lifay.global.LerverResource
import cn.lifay.logutil.LerverLog
import cn.lifay.logutil.LogLevelEnum
import java.io.File
import kotlin.reflect.KMutableProperty1

//转小写驼峰
fun String.toCamelCase(symbol: Char = '_'): String {

    return if (this.contains(symbol)) {
        val length: Int = this.length
        val sb = StringBuilder(length)
        var upperCase = false
        for (i in 0 until length) {
            val c: Char = this.get(i)
            if (c == symbol) {
                upperCase = true
            } else if (upperCase) {
                sb.append(c.uppercaseChar())
                upperCase = false
            } else {
                sb.append(c.lowercaseChar())
            }
        }
        sb.toString()
    } else {
        this.lowercase()
    }
}


fun String.fileMainName(): String {
    val srcFileName = this.substring(this.lastIndexOf(File.separator) + 1)
    return srcFileName.substring(0, srcFileName.lastIndexOf("."))
}

fun String.fileExtName(): String {
    val srcFileName = this.substring(this.lastIndexOf(File.separator))
    return srcFileName.substring(srcFileName.lastIndexOf(".") + 1, srcFileName.length)
}

fun File.mainName(): String {
    val srcFileName = absolutePath.substring(absolutePath.lastIndexOf(File.separator) + 1)
    return srcFileName.substring(0, srcFileName.lastIndexOf("."))
}

fun File.extName(): String {
    val srcFileName = absolutePath.substring(absolutePath.lastIndexOf(File.separator))
    return srcFileName.substring(srcFileName.lastIndexOf(".") + 1, srcFileName.length)
}

fun File.notExistCreate(): File {
    if (!this.exists()) {
        this.parentFile.mkdirs()
        this.createNewFile()
    }
    return this
}

fun formatTime(ms: Long): String {
    if (ms == 0.toLong()) {
        return "0毫秒"
    }
    val ss = 1000
    val mi = ss * 60
    val hh = mi * 60
    val dd = hh * 24
    val day = ms / dd
    val hour = (ms - day * dd) / hh
    val minute = (ms - day * dd - hour * hh) / mi
    val second = (ms - day * dd - hour * hh - minute * mi) / ss
    val milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss
    val sb = StringBuffer()
    if (day > 0) {
        sb.append(day.toString() + "天")
    }
    if (hour > 0) {
        sb.append(hour.toString() + "小时")
    }
    if (minute > 0) {
        sb.append(minute.toString() + "分")
    }
    if (second > 0) {
        sb.append(second.toString() + "秒")
    }
    if (milliSecond > 0) {
        sb.append(milliSecond.toString() + "毫秒")
    }
    return sb.toString()
}

fun <T : Any> execTimeLogNotNull(head: String, logLevelEnum: LogLevelEnum = LogLevelEnum.DEBUG, block: () -> T): T {
    val old = System.currentTimeMillis()
    val result = block()

    LerverLog.log("[${head}] 耗时: ${formatTime(System.currentTimeMillis() - old)}", logLevelEnum)
    return result
}

fun <T : Any> execTimeLog(head: String, logLevelEnum: LogLevelEnum = LogLevelEnum.DEBUG, block: () -> T?): T? {
    val old = System.currentTimeMillis()
    val result = block()

    LerverLog.log("[${head}] 耗时: ${formatTime(System.currentTimeMillis() - old)}", logLevelEnum)
    return result
}

/**
 * 递归地获取树状集合中所有子节点（children）中符合某个条件的所有数据，使用深度优先搜索（DFS）遍历整个树
 */
fun <T : Any,V : List<T>> findMatchingTreeChildren(node: T, childrenProp: KMutableProperty1<T, V?>, condition: (T) -> Boolean): List<T> {
    // 基准情况：如果节点没有子节点，返回一个空列表
    val children = childrenProp(node)
    if (children.isNullOrEmpty()) return emptyList()

    // 递归情况：先查找当前节点的子节点中符合条件的，然后再递归查找所有子节点的子节点
    return children.filter { condition.invoke(it) } +
            children.flatMap { findMatchingTreeChildren(it,childrenProp, condition) }
}
//
///**
// * 递归地获取树状集合中所有子节点（children）中符合某个条件的所有数据，使用深度优先搜索（DFS）遍历整个树
// */
//fun <T : Any,V : Collection<T>> findMatchingTreeChildren11(node: T, childrenProp: KMutableProperty1<T, V?>, condition: (T) -> Boolean): List<T> {
//    // 基准情况：如果节点没有子节点，返回一个空列表
//    val children = childrenProp(node)
//    if (children.isNullOrEmpty()) return emptyList()
//
//    // 递归情况：先查找当前节点的子节点中符合条件的，然后再递归查找所有子节点的子节点
//    return children.filter { condition.invoke(it) } +
//            children.flatMap { findMatchingTreeChildren11(it,childrenProp, condition) }
//}
//fun <T : Any> findMatchingTreeChildren22(node: T, childrenProp: KMutableProperty1<T, ArrayList<T>?>, condition: (T) -> Boolean): List<T> {
//    // 基准情况：如果节点没有子节点，返回一个空列表
//    val children = childrenProp(node)
//    if (children.isNullOrEmpty()) return arrayListOf()
//
//    // 递归情况：先查找当前节点的子节点中符合条件的，然后再递归查找所有子节点的子节点
//    return children.filter { condition.invoke(it) } +
//            children.flatMap { findMatchingTreeChildren22(it,childrenProp, condition) }
//}



fun main() {
    val testFilePath = LerverResource.USER_DIR + "pom.xml"
    val testFile = File(testFilePath)

    println("fileMainName: ${testFilePath.fileMainName()}")
    println("mainName: ${testFile.mainName()}")

    println("fileExtName: ${testFilePath.fileExtName()}")
    println("extName: ${testFile.extName()}")

    println(formatTime(4535345))

}