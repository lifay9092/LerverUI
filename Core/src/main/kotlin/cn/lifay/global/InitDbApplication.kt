package cn.lifay.global

import cn.lifay.GlobeStartUp
import cn.lifay.db.DbLoadView
import javafx.application.Platform
import javafx.scene.Scene
import javafx.stage.Stage

class InitDbApplication() : BaseApplication() {


    override fun start(primaryStage: Stage?) {
        val indexStage = GlobeStartUp.INDEX_STAGE_GET.get();
        val dbName = GlobeStartUp.DB_NAME;
        val dbLoadView = DbLoadView(indexStage, dbName)
        primaryStage!!.apply {
            title = indexStage.title
            scene = Scene(dbLoadView)
            if (indexStage.icons.size > 0) {
                icons.add(indexStage.icons[0])
            }
            setOnCloseRequest {
                Platform.exit()
            }
            show()
        }

    }

}
