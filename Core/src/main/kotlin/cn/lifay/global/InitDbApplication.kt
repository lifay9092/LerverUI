package cn.lifay.global

import atlantafx.base.theme.PrimerLight
import atlantafx.base.theme.Theme
import cn.lifay.db.DbLoadView
import javafx.application.Platform
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.Stage

abstract class InitDbApplication(
    val appLogPrefix: String = "client",
    val appLogPath: String = GlobalResource.USER_DIR + "logs",
    override val appTheme: Theme = PrimerLight(),
    override val appConfigPath: String = GlobalResource.USER_DIR + "lerver.yml",
    val dbName: String = "db.db",
    val isShowStage: Boolean = true,
) : BaseApplication() {

    override fun start(dbLoadStage: Stage?) {
        GlobalConfig.InitLerverConfigPath(appConfigPath)
        GlobalResource.loadTheme(appTheme)

        val primaryStage = addPrimaryStage()
        val dbLoadView = DbLoadView(isShowStage, addPrimaryStage(), dbName)
        dbLoadStage!!.apply {
            title = "脚本更新程序"
            scene = Scene(dbLoadView)
            icons.add(
                Image("/data.png")
            )
            setOnCloseRequest {
                primaryStage.close()
                cancelAllJob()

                Platform.exit()
            }
            if (isShowStage) {
                show()
            } else {

            }
        }

    }

}
