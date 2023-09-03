package cn.lifay.global

import atlantafx.base.theme.PrimerLight
import atlantafx.base.theme.Theme
import javafx.application.Application
import javafx.scene.image.Image
import javafx.stage.Screen
import javafx.stage.Stage

/**
 * GlobalResource 全局资源类
 *
 * @author lifay
 * @date 2022/10/9 20:26
 */
object GlobalResource {
    /**
     * 布局参数
     */
    val SCREEN_WIDTH = Screen.getPrimary().bounds.width
    val SCREEN_HEIGHT = Screen.getPrimary().bounds.height
    val MSG_WIDTH = Screen.getPrimary().bounds.width * 0.15

    /**
     * 资源文件
     */
    const val ROOT_PATH_UI = "/cn/lifay/ui/"
    const val ROOT_PATH_CSS = "/cn/lifay/css/"
    const val ROOT_PATH_IMG = "/cn/lifay/img/"

    /**
     * 全局窗口图标资源
     */
    var ICON_PNG = GlobalResource.javaClass.getResource("/icon.png")?.toExternalForm()
    lateinit var ICON_IMG: Image

    /**
     * 主题
     */
    private var THEME_STYLE: Theme = PrimerLight()

    /**
     * 表单宽度
     */
    fun FormWidth(): Double {
        println(SCREEN_WIDTH)
        val default = SCREEN_WIDTH * 0.35
        return if (default < 1050) 850.0 else default
//        return SCREEN_WIDTH * 0.25
    }

    /**
     * 表单高度
     */
    fun FormHeight(): Double {
//        val default = SCREEN_HEIGHT * 0.45
//       return if (default<1022) 1022.0 else default
        return 860.0
    }

    /**
     * 为窗体设置图标,否则默认：/icon.png
     */
    fun setGlobalIconImage(imgClassPath: String) {
        ICON_PNG = imgClassPath
        ICON_IMG = Image(ICON_PNG)
    }

    /**
     * 为窗体加载图标,默认：/icon.png
     */
    fun loadIcon(stage: Stage) {
        if (!this::ICON_IMG.isInitialized) {
            ICON_PNG?.let {
                ICON_IMG = Image(it)
                stage.icons.add(ICON_IMG)
            }
        } else {
            stage.icons.add(ICON_IMG)
        }
    }

    /**
     * loading图
     */
    fun loading(): String {
        return ROOT_PATH_UI + "loading.gif"
    }

    /**
     * loading图
     */
    fun loadingGray(): String {
        return ROOT_PATH_UI + "loading-gray.gif"
    }

    /**
     * 设置主题
     * @author lifay
     */
    @JvmStatic
    fun loadTheme(theme: Theme = PrimerLight()) {
        THEME_STYLE = theme
        Application.setUserAgentStylesheet(THEME_STYLE.userAgentStylesheet)
    }

    /**
     * 获取主题
     * @author lifay
     */
    fun theme(): Theme {
        return THEME_STYLE
    }

}