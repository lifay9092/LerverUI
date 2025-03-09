package cn.lifay.application

import cn.lifay.db.DbLoadView
import cn.lifay.mq.EventBus
import cn.lifay.mq.event.DefaultEvent
import javafx.application.Platform
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.Stage

/**
 * 使用sqlite的javaFX启动类的基类
 * 可代理Stage窗口，通过继承当前类获取db功能的场景
 */
abstract class InitDbApplication(
    val dbName: String = "db.db",
    val isShowStage: Boolean = true,
) : BaseApplication() {
    /*
      设置首页容器
     */
    abstract fun addAppStage(): Stage
    override fun start(dbLoadStage: Stage) {
//        AppManage.loadAppConfig(AppManage())

        //切换到首页的视图-回调函数
        val targetIndexFunc = {
            val appStage = addAppStage()
            dbLoadStage.hide()
            val eventHandler = appStage.onCloseRequestProperty().get()
            appStage.apply {
                setOnCloseRequest {
                    eventHandler?.handle(it)
                    dbLoadStage.close()
                }
                show()
            }
            afterShow()
//            dbLoadStage.title = indexStage.title
//            dbLoadStage.scene = indexStage.scene
//            dbLoadStage.isFullScreen = indexStage.isFullScreen
//            dbLoadStage.isAlwaysOnTop = indexStage.isAlwaysOnTop
//            dbLoadStage.isMaximized = indexStage.isMaximized
//            dbLoadStage.isIconified = indexStage.isIconified
//            dbLoadStage.isResizable = indexStage.isResizable
//            dbLoadStage.loadIcon()
//            dbLoadStage.setOnCloseRequest {
//                indexStage.onCloseRequestProperty().get().handle(it)
//                indexStage.close()
//                closeAppFunc()
//                Platform.exit()
//            }
            Unit
        }

        val dbLoadView = DbLoadView(isShowStage, targetIndexFunc, dbName)
        dbLoadStage.apply {
            title = "脚本更新程序"
            scene = Scene(dbLoadView)
            icons.add(
                Image("/data.png")
            )
            setOnCloseRequest {
                EventBus.publish(DefaultEvent(EventBusId.CANCEL_JOB.name))
                Platform.exit()
            }
            if (isShowStage) {
                show()
            }
        }

    }

    open fun afterShow() {

    }
}
