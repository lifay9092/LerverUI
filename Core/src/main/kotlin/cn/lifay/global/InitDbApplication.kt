package cn.lifay.global

import cn.lifay.GlobeStartUp
import cn.lifay.db.DbLoadView
import javafx.application.Platform
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.Stage

class InitDbApplication() : BaseApplication() {

    override fun start(primaryStage: Stage?) {

        val dbName = GlobeStartUp.DB_NAME;
        val dbLoadView = DbLoadView(GlobeStartUp.INDEX_STAGE_GET, dbName)
        primaryStage!!.apply {
            title = "脚本更新程序"
            scene = Scene(dbLoadView)
            icons.add(
                Image("/data.png")
            )
            setOnCloseRequest {
                Platform.exit()
            }
            if (GlobeStartUp.IS_SHOW_STAGE) {
                show()
            } else {

            }
        }

    }

}
