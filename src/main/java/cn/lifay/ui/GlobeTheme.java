package cn.lifay.ui;

import cn.lifay.StaticUtil;
import cn.lifay.exception.LerverUIException;
import javafx.application.Application;

import java.net.URL;

/**
 * GlobeTheme 主题全局类
 * @author lifay
 * @date  2022/10/9 20:26
 **/
public class GlobeTheme {

    /**
     * 设置白色
     * @author lifay
     */
    public static void setWhite() {
        URL resource = GlobeTheme.class.getResource(StaticUtil.whiteTheme());
        if (resource == null) {
            throw new LerverUIException("获取不到css文件:" + StaticUtil.whiteTheme());
        }
        Application.setUserAgentStylesheet(resource.toExternalForm());
    }

    /**
     * 设置暗色
     * @author lifay
     */
    public static void setDark() {
        URL resource = GlobeTheme.class.getResource(StaticUtil.darkTheme());
        if (resource == null) {
            throw new LerverUIException("获取不到css文件:" + StaticUtil.darkTheme());
        }
        Application.setUserAgentStylesheet(resource.toExternalForm());
    }


}
