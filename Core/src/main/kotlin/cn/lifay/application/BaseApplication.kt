package cn.lifay.application

import cn.lifay.mq.EventBus
import cn.lifay.mq.event.DefaultEvent
import javafx.application.Application
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

) : Application(), CoroutineScope {
    override val coroutineContext: CoroutineContext = Dispatchers.Main + SupervisorJob()

    init {
        println("init")
        EventBus.subscribe(EventBusId.CANCEL_JOB, DefaultEvent::class) {
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

