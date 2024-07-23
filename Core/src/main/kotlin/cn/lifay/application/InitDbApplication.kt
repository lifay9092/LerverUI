package cn.lifay.application

import atlantafx.base.theme.PrimerLight
import atlantafx.base.theme.Theme
import cn.lifay.db.DbLoadView
import cn.lifay.extension.loadIcon
import cn.lifay.global.GlobalResource
import javafx.application.Platform
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.Stage

/**
 * 使用sqlite的javaFX启动类的基类
 * 可代理Stage窗口，通过继承当前类获取db功能的场景
 */
abstract class InitDbApplication(
    val appLogPrefix: String = "client",
    val appLogPath: String = GlobalResource.USER_DIR + "logs",
    override val appTheme: Theme = PrimerLight(),
    override val appConfigPath: String = GlobalResource.USER_DIR + "lerver.yml",
    val dbName: String = "db.db",
    val isShowStage: Boolean = true,
) : BaseApplication(appLogPrefix, appLogPath, appTheme, appConfigPath) {

    override fun start(dbLoadStage: Stage) {
        loadAppConfig()
        //执行关闭程序时的操作-回调函数
        val closeAppFunc = {
            cancelAllJob()
        }
        //切换到首页的视图-回调函数
        val targetIndexFunc = {
            val indexStage = addIndexStage()
            dbLoadStage.title = indexStage.title
            dbLoadStage.scene = indexStage.scene
            dbLoadStage.isFullScreen = indexStage.isFullScreen
            dbLoadStage.isAlwaysOnTop = indexStage.isAlwaysOnTop
            dbLoadStage.isMaximized = indexStage.isMaximized
            dbLoadStage.isIconified = indexStage.isIconified
            dbLoadStage.isResizable = indexStage.isResizable
            dbLoadStage.loadIcon()
            dbLoadStage.setOnCloseRequest {
                indexStage.onCloseRequestProperty().get().handle(it)
                indexStage.close()
                closeAppFunc()
                Platform.exit()
            }
            Unit
        }

        val dbLoadView = DbLoadView(isShowStage, targetIndexFunc, closeAppFunc, dbName)
        dbLoadStage.apply {
            title = "脚本更新程序"
            scene = Scene(dbLoadView)
            icons.add(
                Image("/data.png")
            )
            setOnCloseRequest {
                closeAppFunc()
                Platform.exit()
            }
            if (isShowStage) {
                show()
            }
        }

    }

}
