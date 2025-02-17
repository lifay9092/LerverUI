package cn.lifay.chooser

import cn.lifay.global.LerverConfig
import cn.lifay.global.LerverResource
import javafx.stage.DirectoryChooser
import javafx.stage.FileChooser
import java.io.File

/*
  默认公共路径-KEY
 */
private val COMMON_CHOOSER_PATH_KEY = "COMMON_CHOOSER_PATH"

/*
  默认公共路径-VALUE
 */
private var COMMON_CHOOSER_PATH: String =
    LerverConfig.ReadProperties(COMMON_CHOOSER_PATH_KEY, LerverResource.USER_DIR)!!

fun DirectoryChooser.getInitFile(key: String? = null): File {
    if (key != null) {
        if (LerverConfig.ContainsKey(key)) {
            return File(LerverConfig.ReadProperties(key, COMMON_CHOOSER_PATH)!!)
        }
    }
    return File(COMMON_CHOOSER_PATH)
}

fun FileChooser.getInitFile(key: String? = null): File {
    if (key != null) {
        if (LerverConfig.ContainsKey(key)) {
            return File(LerverConfig.ReadProperties(key, COMMON_CHOOSER_PATH)!!)
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
            LerverConfig.WriteProperties(key, path)
        } else {
            COMMON_CHOOSER_PATH = path
            LerverConfig.WriteProperties(COMMON_CHOOSER_PATH_KEY, path)
        }
    }

    fun getInitFile(key: String? = null): File {
        if (key != null) {
            if (LerverConfig.ContainsKey(key)) {
                return File(LerverConfig.ReadProperties(key, COMMON_CHOOSER_PATH)!!)
            }
        }
        return File(COMMON_CHOOSER_PATH)
    }

}