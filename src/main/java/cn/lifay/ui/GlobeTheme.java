package cn.lifay.ui;

import cn.lifay.StaticUtil;
import cn.lifay.exception.LerverUIException;
import javafx.application.Application;

import java.net.URL;

/**
 *@ClassName GlobeTheme
 *@Description 主题全局类
 *@Author lifay
 *@Date 2022/10/9 20:26
 **/
public class GlobeTheme {


    public static void setWhite() {
        URL resource = GlobeTheme.class.getResource(StaticUtil.whiteTheme());
        if (resource == null) {
            throw new LerverUIException("获取不到css文件:" + StaticUtil.whiteTheme());
        }
        Application.setUserAgentStylesheet(resource.toExternalForm());
    }

    public static void setDark() {
        URL resource = GlobeTheme.class.getResource(StaticUtil.darkTheme());
        if (resource == null) {
            throw new LerverUIException("获取不到css文件:" + StaticUtil.darkTheme());
        }
        Application.setUserAgentStylesheet(resource.toExternalForm());
    }


}
