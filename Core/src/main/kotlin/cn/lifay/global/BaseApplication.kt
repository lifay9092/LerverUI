package cn.lifay.global

import atlantafx.base.theme.PrimerLight
import atlantafx.base.theme.Theme
import cn.lifay.logutil.LerverLog
import javafx.application.Application
import java.io.File

abstract class BaseApplication(
    theme: Theme = PrimerLight(),
    logPrefix: String = "client",
    logPath: String = System.getProperty("user.dir") + File.separator + "logs"
) : Application() {

    init {
        println("初始化")
        LerverLog.SetLogPrefix(logPrefix)
        LerverLog.SetLogsDirPath(logPath)
        GlobalResource.loadTheme(theme)
    }

}