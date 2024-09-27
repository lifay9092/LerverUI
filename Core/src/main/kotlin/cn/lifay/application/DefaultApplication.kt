//package cn.lifay.application
//
//import cn.lifay.extension.loadIcon
//import javafx.application.Application
//import javafx.application.Platform
//import javafx.stage.Stage
//import java.util.function.Supplier
//
///**
// * javaFX启动类的默认实现类
// * 可代理Stage窗口，应用于通过闭包函数获取Stage的场景
// */
//class DefaultApplication() : BaseApplication() {
//    override fun addIndexStage(): Stage {
//        return GlobeStartUp.STAGE_GET.get()
//    }
//
//    override fun start(primaryStage: Stage) {
//        loadAppConfig()
//
//        val indexStage = addIndexStage()
//        val indexCloseReq = indexStage.onCloseRequestProperty().get()
//        primaryStage!!.apply {
//            title = indexStage.title
//            scene = indexStage.scene
//            loadIcon()
//            setOnCloseRequest {
//                println("close window...")
//                indexCloseReq?.let { h ->
//                    indexCloseReq.handle(it)
//                }
//                indexStage.close()
//                cancelAllJob()
//
//                Platform.exit()
//            }
//            show()
////            indexStage.show()
//        }
//
//    }
//
//}
//
//object GlobeStartUp {
//
//    lateinit var STAGE_GET: Supplier<Stage>
//
//    /**
//     * 指定窗口启动、指定初始化db名称
//     *
//     * @param stageGet 首页窗口
//     */
//    @JvmStatic
//    fun launch(stageGet: Supplier<Stage>?) {
//        STAGE_GET = stageGet!!
//        //        launch("db.db", true, stageGet);
////        Stage stage = stageGet.get();
////        BaseApplication baseApplication = new BaseApplication() {
////            @NotNull
////            @Override
////            public Stage addPrimaryStage() {
////                return stage;
////            }
////        };
////        BaseApplication.launch();
//        Application.launch(DefaultApplication::class.java)
//    }
//
//}