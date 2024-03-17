package cn.lifay.chooser

import cn.lifay.global.GlobalConfig
import cn.lifay.global.GlobalResource
import javafx.stage.DirectoryChooser
import javafx.stage.FileChooser
import java.io.File

private val COMMON_CHOOSER_PATH_KEY = "COMMON_CHOOSER_PATH"
private var COMMON_CHOOSER_PATH: String = GlobalConfig.ReadProperties(COMMON_CHOOSER_PATH_KEY, GlobalResource.USER_DIR)

fun DirectoryChooser.getInitFile(key: String? = null): File {
    if (key != null) {
        if (GlobalConfig.ContainsKey(key)) {
            return File(GlobalConfig.ReadProperties(key))
        }
    }
    return File(COMMON_CHOOSER_PATH)
}

fun FileChooser.getInitFile(key: String? = null): File {
    if (key != null) {
        if (GlobalConfig.ContainsKey(key)) {
            return File(GlobalConfig.ReadProperties(key))
        }
    }
    return File(COMMON_CHOOSER_PATH)
}

object ChooserExtension {
    @Synchronized
    fun updateInitChooserPath(path: String, key: String? = null) {
        if (!File(path).exists()) {
            return
        }
        if (key != null) {
            GlobalConfig.WriteProperties(key, path)
        } else {
            COMMON_CHOOSER_PATH = path
            GlobalConfig.WriteProperties(COMMON_CHOOSER_PATH_KEY, path)
        }
    }

    fun getInitFile(key: String? = null): File {
        if (key != null) {
            if (GlobalConfig.ContainsKey(key)) {
                return File(GlobalConfig.ReadProperties(key))
            }
        }
        return File(COMMON_CHOOSER_PATH)
    }

}