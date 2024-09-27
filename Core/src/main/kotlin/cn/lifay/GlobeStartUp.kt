import cn.lifay.global.GlobalResource

//package cn.lifay;
//
//import cn.lifay.application.BaseApplication;
//import cn.lifay.application.DefaultApplication;
//import cn.lifay.application.InitDbApplication;
//import javafx.application.Application;
//import javafx.stage.Stage;
//
//import java.util.function.Supplier;
//
object GlobeStartUp {
    //
    //    /**
    //     * 指定启动类
    //     *
    //     * @param clazz 启动类
    //     */
    //    public static <T extends BaseApplication> void launch(Class<T> clazz) {
    //        Application.launch(clazz);
    //    }
    //
    //    /**
    //     * 指定窗口启动、指定初始化db名称
    //     *
    //     * @param stageGet 首页窗口
    //     */
    //    public static void launch(Supplier<Stage> stageGet) {
    //        STAGE_GET = stageGet;
    ////        launch("db.db", true, stageGet);
    ////        Stage stage = stageGet.get();
    ////        BaseApplication baseApplication = new BaseApplication() {
    ////            @NotNull
    ////            @Override
    ////            public Stage addPrimaryStage() {
    ////                return stage;
    ////            }
    ////        };
    ////        BaseApplication.launch();
    //        Application.launch(DefaultApplication.class);
    //    }
    //
    //    public static Supplier<Stage> STAGE_GET = null;
    //
    //    /**
    //     * 指定窗口启动、指定初始化db名称
    //     *
    //     * @param stageGet 首页窗口
    //     * @param dbName   自定义db名字
    //     */
    //    public static void launch(String dbName, Supplier<Stage> stageGet) {
    //        launch(dbName, true, stageGet);
    //    }
    //
    //    /**
    //     * 指定窗口启动、指定初始化db名称
    //     *
    //     * @param stageGet    首页窗口
    //     * @param isShowStage 是否显示db加载界面
    //     */
    //    public static void launch(boolean isShowStage, Supplier<Stage> stageGet) {
    //        launch("db.db", isShowStage, stageGet);
    //    }
    //
    //    /**
    //     * 指定窗口启动、指定初始化db名称
    //     *
    //     * @param stageGet    首页窗口
    //     * @param dbName      自定义db名字
    //     * @param isShowStage 是否显示db加载界面
    //     */
    //    public static void launch(String dbName, boolean isShowStage, Supplier<Stage> stageGet) {
    //        INDEX_STAGE_GET = stageGet;
    //        DB_NAME = dbName;
    //        IS_SHOW_STAGE = isShowStage;
    //
    //        Application.launch(InitDbApplication.class);
    //    }
    //
    //    public static Supplier<Stage> INDEX_STAGE_GET = null;
    //    public static String DB_NAME = "db.db";
    //    public static boolean IS_SHOW_STAGE = true;
    //
    fun loadAppConfig(
        appLogPrefix: String = "client",
        appLogPath: String = GlobalResource.USER_DIR + "logs",
        appConfigPath: String = GlobalResource.USER_DIR + "lerver.yml",
    ) {

    }
}
