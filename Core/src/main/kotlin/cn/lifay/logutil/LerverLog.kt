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
    var LOG_PREFIX: String,
    var LOG_PATH: String
) {


//    var LOGS_DIR = System.getProperty("user.dir") + File.separator


    init {
        initLogFile()
    }


    private lateinit var LOG_DEBUG_PATH: String
    private lateinit var LOG_INFO_PATH: String
    private lateinit var LOG_WARN_PATH: String
    private lateinit var LOG_ERROR_PATH: String

    private lateinit var DEBUG_FILE: File
    private lateinit var INFO_FILE: File
    private lateinit var WARN_FILE: File
    private lateinit var ERROR_FILE: File

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
        initLogFile()
    }

    fun SetLogsDirPath(path: String) {
        println("SetLogsDirPath init")
        LOG_PATH = path
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
            LogLevelEnum.ERROR -> writeText(level.name, text, ERROR_FILE, DEBUG_FILE, INFO_FILE)
        }
    }

    @Synchronized
    private fun writeText(level: String, text: String, vararg logFile: File) {
        val stack = getStackTraceName(Thread.currentThread().stackTrace[4])
        val s = "${
            LocalDateTime.now().format(LerverLog.DATE_TIME_FORMAT_STR)
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

object LerverLog {

    private lateinit var GLOBE_LOG: GlobeLog

    val DATE_TIME_FORMAT_STR = DateTimeFormatter.ofPattern("YYYY-MM-DD HH:mm:ss")

    fun isWindows(): Boolean = File.separator == "\\"

    fun InitLog(logPrefix: String, logPath: String) {
        GLOBE_LOG = GlobeLog(logPrefix, logPath)
    }

    fun SetLogPrefix(prefix: String) {
        GLOBE_LOG.SetLogPrefix(prefix)
    }

    fun SetLogsDirPath(path: String) {
        GLOBE_LOG.SetLogsDirPath(path)
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

    fun log(text: String, level: LogLevelEnum = LogLevelEnum.DEBUG) {
        GLOBE_LOG.log(text, level)
    }
}