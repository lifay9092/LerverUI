package cn.lifay;

import cn.lifay.global.BaseApplication;
import cn.lifay.global.InitDbApplication;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.function.Supplier;

public class GlobeStartUp {

    /**
     * 指定启动类
     *
     * @param clazz 启动类
     */
    public static <T extends BaseApplication> void launch(Class<T> clazz) {
        Application.launch(clazz);
    }

    /**
     * 指定窗口启动、指定初始化db名称
     *
     * @param stageGet 首页窗口
     */
    public static void launch(Supplier<Stage> stageGet) {
        launch(stageGet, "db.db");
    }

    /**
     * 指定窗口启动、指定初始化db名称
     *
     * @param stageGet 首页窗口
     * @param dbName   自定义db名字
     */
    public static void launch(Supplier<Stage> stageGet, String dbName) {
        INDEX_STAGE_GET = stageGet;
        DB_NAME = dbName;

        Application.launch(InitDbApplication.class);


    }

    public static Supplier<Stage> INDEX_STAGE_GET = null;
    public static String DB_NAME = "db.db";

}
