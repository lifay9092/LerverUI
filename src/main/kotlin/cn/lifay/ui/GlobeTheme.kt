package cn.lifay.ui

import atlantafx.base.theme.PrimerDark
import atlantafx.base.theme.PrimerLight
import javafx.application.Application
import javafx.scene.image.Image
import javafx.stage.Stage

/**
 * GlobeTheme 主题全局类
 * @author lifay
 * @date 2022/10/9 20:26
 */
object GlobeTheme {

    var ELEMENT_STYLE = false

    //    val CSS_RESOURCE = this.javaClass.getResource("/css/element-ui.css")?.toExternalForm()
    var ICON = GlobeTheme.javaClass.getResource("/icon.png")?.toExternalForm()
    lateinit var ICON_IMG: Image

    fun enableElement(b: Boolean) {
        ELEMENT_STYLE = b
    }

    /**
     * 设置白色
     * @author lifay
     */
    fun setWhite() {
//        val resource = GlobeTheme::class.java.getResource(StaticUtil.whiteTheme())
//            ?: throw LerverUIException("获取不到css文件:" + StaticUtil.whiteTheme())
//        Application.setUserAgentStylesheet(resource.toExternalForm())
        Application.setUserAgentStylesheet(PrimerLight().userAgentStylesheet)

    }

    /**
     * 设置暗色
     * @author lifay
     */
    fun setDark() {
//        val resource = GlobeTheme::class.java.getResource(StaticUtil.darkTheme())
//            ?: throw LerverUIException("获取不到css文件:" + StaticUtil.darkTheme())
//        Application.setUserAgentStylesheet(resource.toExternalForm())
        Application.setUserAgentStylesheet(PrimerDark().userAgentStylesheet)
    }

    /**
     * 为窗体设置图标,否则默认：/icon.png
     */
    fun setIconImage(imgClassPath: String) {
        GlobeTheme.javaClass.getResource("/icon.png")?.let {
            ICON = it.toExternalForm()
            ICON_IMG = Image(ICON)
        }
    }

    /**
     * 为窗体加载图标,默认：/icon.png
     */
    fun loadIcon(stage: Stage) {
        if (!this::ICON_IMG.isInitialized) {
            ICON?.let {
                ICON_IMG = Image(it)
                stage.icons.add(ICON_IMG)
            }
        } else {
            stage.icons.add(ICON_IMG)
        }
    }
}