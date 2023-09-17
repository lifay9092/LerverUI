package cn.lifay.logutil

import cn.lifay.extension.notExistCreate
import java.io.File
import java.nio.charset.Charset
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

enum class LogLevelEnum {
    DEBUG,
    INFO,
    WARN,
    ERROR,
}

object LerverLog {

    val DATE_TIME_FORMAT_STR = DateTimeFormatter.ofPattern("YYYY-MM-DD HH:mm:ss")

    var LOGS_DIR = System.getProperty("user.dir") + File.separator

    fun isWindows(): Boolean = File.separator == "\\"

    var PREFIX = "server"
    val LOG_PATH = joinPath(LOGS_DIR, "logs")

    private val LOG_DEBUG_PATH = joinPath(LOG_PATH, "${PREFIX}-debug.log")
    private val LOG_INFO_PATH = joinPath(LOG_PATH, "${PREFIX}-info.log")
    private val LOG_WARN_PATH = joinPath(LOG_PATH, "${PREFIX}-warn.log")
    private val LOG_ERROR_PATH = joinPath(LOG_PATH, "${PREFIX}-error.log")

    private var DEBUG_FILE = File(LOG_DEBUG_PATH).notExistCreate()
    private var INFO_FILE = File(LOG_INFO_PATH).notExistCreate()
    private var WARN_FILE = File(LOG_WARN_PATH).notExistCreate()
    private var ERROR_FILE = File(LOG_ERROR_PATH).notExistCreate()

    fun initLogFile() {
         DEBUG_FILE = File(LOG_DEBUG_PATH).notExistCreate()
       INFO_FILE = File(LOG_INFO_PATH).notExistCreate()
          WARN_FILE = File(LOG_WARN_PATH).notExistCreate()
          ERROR_FILE = File(LOG_ERROR_PATH).notExistCreate()
    }
    fun SetLogsDirPath(path : String) {
        LOGS_DIR = path
        initLogFile()
    }

    fun joinPath(basePath: String, vararg paths: String): String {
        return Paths.get(basePath, *paths).toString()
    }
    fun log(text: String, level: LogLevelEnum = LogLevelEnum.DEBUG) {
        when (level) {
            LogLevelEnum.DEBUG -> writeText(level.name, text, DEBUG_FILE)
            LogLevelEnum.INFO -> writeText(level.name, text, INFO_FILE)
            LogLevelEnum.WARN -> writeText(level.name, text, WARN_FILE)
            LogLevelEnum.ERROR -> writeText(level.name, text, ERROR_FILE,DEBUG_FILE, INFO_FILE)
        }
    }

    fun debug(text: String) {
        log(text, LogLevelEnum.DEBUG)
    }

    fun info(text: String) {
        log(text, LogLevelEnum.INFO)
    }

    fun warn(text: String) {
        log(text, LogLevelEnum.WARN)
    }

    fun error(text: String) {
        log(text, LogLevelEnum.ERROR)
    }

    fun error(e: Exception) {
        error(e.stackTraceToString())
        error(e.message!!)
    }
    @Synchronized
    private fun writeText(level: String, text: String, vararg logFile: File) {
        val stack = getStackTraceName(Thread.currentThread().stackTrace[4])
        val s = "${
            LocalDateTime.now().format(DATE_TIME_FORMAT_STR)
        } <${stack}> [${level}]:  ${text}\n"
        for (file in logFile) {
            file.appendText(s, Charset.defaultCharset())
        }
        if (level.equals("DEBUG", true) || level.equals("ERROR", true)) {
            println(s)
        }
    }

    private fun getStackTraceName(stackTraceElement: StackTraceElement): String {
        return stackTraceElement.fileName!!.split(".")[0] + "." + stackTraceElement.methodName + "(${stackTraceElement.lineNumber})"
    }

}