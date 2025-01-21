package cn.lifay.application

import atlantafx.base.theme.PrimerDark
import atlantafx.base.theme.PrimerLight
import cn.lifay.global.LerverConfig
import cn.lifay.global.LerverResource
import cn.lifay.logutil.LerverLog
import cn.lifay.mq.EventBus
import cn.lifay.mq.event.DefaultEvent
import javafx.application.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.CoroutineContext
import kotlin.system.exitProcess

/**
 * javaFX启动类的基类
 * 定义一些应用配置、应用协程
 */
abstract class BaseApplication() : Application(), CoroutineScope {
    override val coroutineContext: CoroutineContext = Dispatchers.Main + SupervisorJob()

    init {
//        println("init")
        //加载配置信息
        try {
            LerverConfig.InitConfigPath()
        } catch (e: Exception) {
            println("加载配置文件失败:${e.stackTraceToString()}")
            exitProcess(1)
        }
        //加载日志组件
        val logPrefix = LerverConfig.ReadProperties<String>("log.prefix", "client")
        val logDir = LerverConfig.ReadProperties<String>("log.dir", LerverResource.USER_DIR + "logs")
        LerverLog.SetLogPrefix(logPrefix!!)
        LerverLog.SetLogsDirPath(logDir!!)
        try {
            LerverLog.InitConfig()
        } catch (e: Exception) {
            println("加载日志组件失败:${e.stackTraceToString()}")
            exitProcess(1)
        }
        //加载主题
        LerverConfig.ReadProperties("theme", PrimerLight().name)!!.let {
            if ("THEME_DARK" == it) {
                LerverResource.loadTheme(PrimerDark())
            } else {
                LerverResource.loadTheme()
            }
        }
        //注册退出任务
        EventBus.subscribe<DefaultEvent>(EventBusId.CANCEL_JOB) {
            cancelAllJob()
        }
    }

    /*
      设置首页容器
     */
//    abstract fun addIndexStage(): Stage

//    /*
//      设置窗口关闭后回调函数
//     */
//    abstract fun addOnCloseRequest(): EventHandler<WindowEvent?>?

//    override fun start(primaryStage: Stage) {
//
//        val primaryStage = addIndexStage()
//        primaryStage.show()
//
//    }

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

