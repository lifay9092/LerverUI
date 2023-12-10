package cn.lifay.chooser

import javafx.stage.DirectoryChooser
import javafx.stage.FileChooser
import java.io.File

private var COMMON_CHOOSER_PATH: String = System.getProperty("user.dir")
private val KEY_CHOOSER_PATH_MAP = HashMap<String, String>()

fun DirectoryChooser.getInitFile(key: String? = null): File {
    if (key != null) {
        if (KEY_CHOOSER_PATH_MAP.containsKey(key)) {
            return File(KEY_CHOOSER_PATH_MAP[key]!!)
        }
    }
    return File(COMMON_CHOOSER_PATH)
}

fun FileChooser.getInitFile(key: String? = null): File {
    if (key != null) {
        if (KEY_CHOOSER_PATH_MAP.containsKey(key)) {
            return File(KEY_CHOOSER_PATH_MAP[key]!!)
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
            KEY_CHOOSER_PATH_MAP[key] = path
        } else {
            COMMON_CHOOSER_PATH = path
        }
    }

    fun getInitFile(key: String? = null): File {
        if (key != null) {
            if (KEY_CHOOSER_PATH_MAP.containsKey(key)) {
                return File(KEY_CHOOSER_PATH_MAP[key]!!)
            }
        }
        return File(COMMON_CHOOSER_PATH)
    }
}