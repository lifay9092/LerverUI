package cn.lifay.extension

import cn.lifay.global.GlobalResource
import cn.lifay.logutil.LerverLog
import cn.lifay.logutil.LogLevelEnum
import java.io.File

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

fun main() {
    val testFilePath = GlobalResource.USER_DIR + "pom.xml"
    val testFile = File(testFilePath)

    println("fileMainName: ${testFilePath.fileMainName()}")
    println("mainName: ${testFile.mainName()}")

    println("fileExtName: ${testFilePath.fileExtName()}")
    println("extName: ${testFile.extName()}")

    println(formatTime(4535345))
}