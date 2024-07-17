package cn.lifay.global

import cn.lifay.GlobeStartUp
import javafx.application.Platform
import javafx.scene.image.Image
import javafx.stage.Stage

class DefaultApplication() : BaseApplication() {
    override fun addPrimaryStage(): Stage {
        return GlobeStartUp.STAGE_GET.get()
    }

    override fun start(dbLoadStage: Stage?) {
        GlobalConfig.InitLerverConfigPath(appConfigPath)
        GlobalResource.loadTheme(appTheme)

        val primaryStage = addPrimaryStage()
        dbLoadStage!!.apply {
            title = primaryStage.title
//            scene = primaryStage.scene
            icons.add(
                Image("/data.png")
            )
            setOnCloseRequest {
                println("close window...")
                primaryStage.close()
                cancelAllJob()

                Platform.exit()
            }
//            hide()
            primaryStage.show()
        }

    }

}
