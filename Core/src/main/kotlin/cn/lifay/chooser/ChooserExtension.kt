package cn.lifay.chooser

import cn.lifay.chooser.ChooserExtension.getInitFileCommon
import cn.lifay.global.GlobalConfig
import cn.lifay.global.GlobalResource
import javafx.stage.DirectoryChooser
import javafx.stage.FileChooser
import java.io.File

private val COMMON_CHOOSER_PATH_KEY = "COMMON_CHOOSER_PATH"
private var COMMON_CHOOSER_PATH: String =
    GlobalConfig.ReadProperties(COMMON_CHOOSER_PATH_KEY, GlobalResource.USER_DIR)!!

/**
 * 获取目录选择器的初始化目录，默认为当前工作目录
 */
fun DirectoryChooser.getInitFile(key: String? = null, defaultFile: File = File(GlobalResource.USER_DIR)): File {
    return getInitFileCommon(key, defaultFile)
}

fun FileChooser.getInitFile(key: String? = null, defaultFile: File = File(GlobalResource.USER_DIR)): File {
    return getInitFileCommon(key, defaultFile)

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

    fun getInitFile(key: String? = null, defaultFile: File = File(GlobalResource.USER_DIR)): File {
        return getInitFileCommon(key)
    }

    fun getInitFileCommon(key: String? = null, defaultFile: File = File(GlobalResource.USER_DIR)): File {
        var file: File? = null
        if (key != null) {
            if (GlobalConfig.ContainsKey(key)) {
                file = File(GlobalConfig.ReadProperties(key, COMMON_CHOOSER_PATH)!!)
            }
        } else {
            file = File(COMMON_CHOOSER_PATH)
        }
        if (file == null || !file.exists()) {
            file = defaultFile
        }
        return file
    }
}