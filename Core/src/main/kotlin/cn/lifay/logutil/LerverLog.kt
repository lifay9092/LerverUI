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

class GlobeLog(
    val LOG_PREFIX: String,
    val LOG_PATH: String
) {


//    var LOGS_DIR = System.getProperty("user.dir") + File.separator


    init {
        println("LerverLog init")
    }


    private var LOG_DEBUG_PATH = joinPath(LOG_PATH, "${LOG_PREFIX}-debug.log")
    private var LOG_INFO_PATH = joinPath(LOG_PATH, "${LOG_PREFIX}-info.log")
    private var LOG_WARN_PATH = joinPath(LOG_PATH, "${LOG_PREFIX}-warn.log")
    private var LOG_ERROR_PATH = joinPath(LOG_PATH, "${LOG_PREFIX}-error.log")

    private var DEBUG_FILE = File(LOG_DEBUG_PATH).notExistCreate()
    private var INFO_FILE = File(LOG_INFO_PATH).notExistCreate()
    private var WARN_FILE = File(LOG_WARN_PATH).notExistCreate()
    private var ERROR_FILE = File(LOG_ERROR_PATH).notExistCreate()

    fun initLogFile() {

        LOG_DEBUG_PATH = joinPath(LOG_PATH, "${LOG_PREFIX}-debug.log")
        LOG_INFO_PATH = joinPath(LOG_PATH, "${LOG_PREFIX}-info.log")
        LOG_WARN_PATH = joinPath(LOG_PATH, "${LOG_PREFIX}-warn.log")
        LOG_ERROR_PATH = joinPath(LOG_PATH, "${LOG_PREFIX}-error.log")

        DEBUG_FILE = File(LOG_DEBUG_PATH).notExistCreate()
        INFO_FILE = File(LOG_INFO_PATH).notExistCreate()
        WARN_FILE = File(LOG_WARN_PATH).notExistCreate()
        ERROR_FILE = File(LOG_ERROR_PATH).notExistCreate()
    }

    fun SetLogPrefix(prefix: String) {
        println("SetLogPrefix init")
        LOG_PREFIX = prefix
    }

    fun SetLogsDirPath(path: String) {
        println("SetLogsDirPath init")
        LOG_PATH = path
        initLogFile()
    }

    fun joinPath(basePath: String, vararg paths: String): String {
        return Paths.get(basePath, *paths).toString()
    }


}
object LerverLog {
    val DATE_TIME_FORMAT_STR = DateTimeFormatter.ofPattern("YYYY-MM-DD HH:mm:ss")
    lateinit var globeLog: GlobeLog
    
    fun isWindows(): Boolean = File.separator == "\\"

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

    fun log(text: String, level: LogLevelEnum = LogLevelEnum.DEBUG) {
        when (level) {
            LogLevelEnum.DEBUG -> LerverLog.writeText(level.name, text, DEBUG_FILE)
            LogLevelEnum.INFO -> LerverLog.writeText(level.name, text, INFO_FILE)
            LogLevelEnum.WARN -> LerverLog.writeText(level.name, text, WARN_FILE)
            LogLevelEnum.ERROR -> LerverLog.writeText(level.name, text, ERROR_FILE, DEBUG_FILE, INFO_FILE)
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
    private fun getStackTraceName(stackTraceElement: StackTraceElement): String {
        return stackTraceElement.fileName!!.split(".")[0] + "." + stackTraceElement.methodName + "(${stackTraceElement.lineNumber})"
    }

}