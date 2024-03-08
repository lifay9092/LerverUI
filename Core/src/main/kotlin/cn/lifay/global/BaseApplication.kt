package cn.lifay.global

import atlantafx.base.theme.PrimerLight
import atlantafx.base.theme.Theme
import cn.lifay.logutil.LerverLog
import javafx.application.Application

abstract class BaseApplication(
    theme: Theme = PrimerLight(),
    configPath: String = GlobalResource.USER_DIR + "lerver.properties",
    logPrefix: String = "client",
    logPath: String = GlobalResource.USER_DIR + "logs"
) : Application() {

    init {
        println("初始化")
        GlobalConfig.InitLerverConfigPath(configPath)
        LerverLog.InitLog(logPrefix, logPath)
        GlobalResource.loadTheme(theme)
    }

}