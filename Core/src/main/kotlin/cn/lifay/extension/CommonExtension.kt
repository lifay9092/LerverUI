package cn.lifay.extension

import java.io.File

//转小写驼峰
fun String.toCamelCase(symbol:Char = '_'):String{

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
    return srcFileName.substring(srcFileName.lastIndexOf(".") + 1,srcFileName.length)
}

fun File.mainName(): String {
    val srcFileName = absolutePath.substring(absolutePath.lastIndexOf(File.separator) + 1)
    return srcFileName.substring(0, srcFileName.lastIndexOf("."))
}

fun File.extName(): String {
    val srcFileName = absolutePath.substring(absolutePath.lastIndexOf(File.separator))
    return srcFileName.substring(srcFileName.lastIndexOf(".") + 1,srcFileName.length)
}

fun File.notExistCreate(): File {
    if (!this.exists()) {
        this.parentFile.mkdirs()
        this.createNewFile()
    }
    return this
}

fun main() {
    val testFilePath = System.getProperty("user.dir") + File.separator + "pom.xml"
    val testFile = File(testFilePath)

    println("fileMainName: ${testFilePath.fileMainName()}")
    println("mainName: ${testFile.mainName()}")

    println("fileExtName: ${testFilePath.fileExtName()}")
    println("extName: ${testFile.extName()}")


}