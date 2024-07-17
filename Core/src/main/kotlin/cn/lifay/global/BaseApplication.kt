package cn.lifay.global

import atlantafx.base.theme.PrimerLight
import atlantafx.base.theme.Theme
import cn.lifay.logutil.LerverLog
import javafx.application.Application
import javafx.stage.Stage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.CoroutineContext

abstract class BaseApplication(
    appLogPrefix: String = "client",
    appLogPath: String = GlobalResource.USER_DIR + "logs",
    open val appTheme: Theme = PrimerLight(),
    open val appConfigPath: String = GlobalResource.USER_DIR + "lerver.yml",
) : Application(), CoroutineScope {
    override val coroutineContext: CoroutineContext = Dispatchers.Main + SupervisorJob()

    init {
        LerverLog.InitLog(appLogPrefix, appLogPath)
    }

    /*
      设置首页容器
     */
    abstract fun addPrimaryStage(): Stage

//    /*
//      设置窗口关闭后回调函数
//     */
//    abstract fun addOnCloseRequest(): EventHandler<WindowEvent?>?

    override fun start(primaryStage: Stage?) {
        GlobalConfig.InitLerverConfigPath(appConfigPath)
        GlobalResource.loadTheme(appTheme)
        test()

        val primaryStage = addPrimaryStage()
        primaryStage.show()

    }

    fun test() {
//        GlobalConfig.WritePropertiesForKey(
//            arrayOf("r", "s", "t"), mapOf(
//                "k" to true,
//                "l" to "m",
//            )
//        )
//        println(GlobalConfig.ReadProperties("a"))
//        println(GlobalConfig.ReadProperties("b"))
//        println(GlobalConfig.ReadProperties("c"))
//        println(GlobalConfig.ReadProperties("c.d"))
//        println(GlobalConfig.ReadProperties("c.e.f"))
    }

    /*
        取消所有子协程
     */
    fun cancelAllJob() {
        coroutineContext[Job]?.cancel()
    }
}

