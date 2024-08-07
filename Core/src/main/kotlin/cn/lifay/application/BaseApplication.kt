package cn.lifay.application

import atlantafx.base.theme.PrimerDark
import atlantafx.base.theme.PrimerLight
import atlantafx.base.theme.Theme
import cn.lifay.global.GlobalConfig
import cn.lifay.global.GlobalResource
import cn.lifay.logutil.LerverLog
import javafx.application.Application
import javafx.stage.Stage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.CoroutineContext

/**
 * javaFX启动类的基类
 * 定义一些应用配置、应用协程
 */
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
    abstract fun addIndexStage(): Stage

//    /*
//      设置窗口关闭后回调函数
//     */
//    abstract fun addOnCloseRequest(): EventHandler<WindowEvent?>?

    override fun start(primaryStage: Stage) {
        loadAppConfig()

        val primaryStage = addIndexStage()
        primaryStage.show()

    }

    protected fun loadAppConfig() {
        GlobalConfig.InitLerverConfigPath(appConfigPath)
        //loadTheme
        GlobalConfig.ReadProperties("theme", PrimerLight().name)!!.let {
            if ("THEME_DARK" == it) {
                GlobalResource.loadTheme(PrimerDark())
            } else {
                GlobalResource.loadTheme()
            }
        }
        test()
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

