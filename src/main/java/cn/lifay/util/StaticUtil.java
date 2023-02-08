package cn.lifay.util;

/**
 * StaticUtil 静态工具类
 * @author lifay
 * @date  2022/10/9 20:26
 **/
public class StaticUtil {


    public static final String ROOT_PATH_UI = "/cn/lifay/ui/";
    public static final String ROOT_PATH_CSS = "/cn/lifay/css/";

    public static final String WHITE_CSS = "modena.css";
    public static final String DARK_CSS = "modena_dark.css";

    public static String loading() {
        return ROOT_PATH_UI + "loading.gif";
    }

    public static String whiteTheme() {
        return ROOT_PATH_CSS + WHITE_CSS;
    }

    public static String darkTheme() {
        return ROOT_PATH_CSS + DARK_CSS;
    }

}
