package cn.lifay.ui

import cn.lifay.exception.LerverUIException
import cn.lifay.util.StaticUtil
import javafx.application.Application

/**
 * GlobeTheme 主题全局类
 * @author lifay
 * @date 2022/10/9 20:26
 */
object GlobeTheme {
    /**
     * 设置白色
     * @author lifay
     */
    fun setWhite() {
        val resource = GlobeTheme::class.java.getResource(StaticUtil.whiteTheme())
            ?: throw LerverUIException("获取不到css文件:" + StaticUtil.whiteTheme())
        Application.setUserAgentStylesheet(resource.toExternalForm())
    }

    /**
     * 设置暗色
     * @author lifay
     */
    fun setDark() {
        val resource = GlobeTheme::class.java.getResource(StaticUtil.darkTheme())
            ?: throw LerverUIException("获取不到css文件:" + StaticUtil.darkTheme())
        Application.setUserAgentStylesheet(resource.toExternalForm())
    }
}