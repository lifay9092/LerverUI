package cn.lifay.global

import atlantafx.base.theme.PrimerLight
import atlantafx.base.theme.Theme
import cn.lifay.logutil.LerverLog
import javafx.application.Application

abstract class BaseApplication(
    theme: Theme = PrimerLight(),
    configPath: String = GlobalResource.USER_DIR + "lerver.yml",
    logPrefix: String = "client",
    logPath: String = GlobalResource.USER_DIR + "logs"
) : Application() {

    init {

        GlobalConfig.InitLerverConfigPath(configPath)
        println(GlobalConfig.ReadProperties("a"))
        println(GlobalConfig.ReadProperties("b"))
        println(GlobalConfig.ReadProperties("c"))

        LerverLog.InitLog(logPrefix, logPath)
        GlobalResource.loadTheme(theme)
    }

}