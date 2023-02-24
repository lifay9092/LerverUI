package cn.lifay.util;

import javafx.stage.Screen;

/**
 * StaticUtil 静态工具类
 * @author lifay
 * @date 2022/10/9 20:26
 **/
public class StaticUtil {

    /**
     * 布局参数
     */
    public static final Double SCREEN_WIDTH = Screen.getPrimary().getBounds().getWidth();
    public static final Double SCREEN_HEIGHT = Screen.getPrimary().getBounds().getHeight();

    /**
     * 资源文件
     */
    public static final String ROOT_PATH_UI = "/cn/lifay/ui/";
    public static final String ROOT_PATH_CSS = "/cn/lifay/css/";

    public static final String ROOT_PATH_IMG = "/cn/lifay/img/";

    public static final String WHITE_CSS = "modena.css";
    public static final String DARK_CSS = "modena_dark.css";

    public static String loading() {
        return ROOT_PATH_UI + "loading.gif";
    }

    public static String loadingGray() {
        return ROOT_PATH_UI + "loading-gray.gif";
    }

    public static String whiteTheme() {
        return ROOT_PATH_CSS + WHITE_CSS;
    }

    public static String darkTheme() {
        return ROOT_PATH_CSS + DARK_CSS;
    }

}
