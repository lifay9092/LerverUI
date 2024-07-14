package cn.lifay.global

import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.LoaderOptions
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor
import org.yaml.snakeyaml.representer.Representer
import java.io.File

data class DbEntity(

    val url: String,
    val user: String,
    val password: String
)

object GlobalConfig {

    private lateinit var LERVER_CONFIG_PATH: String

    private val CONFIG_MAP = HashMap<String, Any>()
    private val LOADER_OPTIONS = LoaderOptions().apply {
        this.isAllowDuplicateKeys = true
        this.allowRecursiveKeys = true
        this.isProcessComments = true
    }
    private val DUMPER_OPTIONS = DumperOptions().apply {
        this.isProcessComments = true
    }
    private val YAML = Yaml(
        Constructor(LOADER_OPTIONS),
        Representer(DUMPER_OPTIONS),
        DUMPER_OPTIONS,
        LOADER_OPTIONS
    )

    /**
     * 初始化配置文件
     */
    fun InitLerverConfigPath(configPath: String) {
        LERVER_CONFIG_PATH = configPath
        val file = File(LERVER_CONFIG_PATH)
        if (!file.exists()) {
            file.parentFile.mkdirs()
            file.createNewFile()
        }
        LoadToConfigMap()
    }

    fun ContainsKey(key: String): Boolean {
        val keys = splitKeys(key)
        var current = HashMap<String, Any>()
        current.putAll(CONFIG_MAP)
        for ((index, key) in keys.withIndex()) {
            if (index == keys.size - 1) {
                return current.containsKey(key)
            }
            val newMap = current[key]
            if (newMap is HashMap<*, *>) {
                current = newMap as HashMap<String, Any>
            } else {
                //取不到值
                return false
            }
        }
        return CONFIG_MAP.containsKey(key)
    }

    private fun splitKeys(key: String): Array<String> {
        return key.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    }

    fun <T> ReadProperties(key: String, defaultValue: T? = null): T? {
        val keys = splitKeys(key)
        var current = HashMap<String, Any>()
        current.putAll(CONFIG_MAP)
        for ((index, key) in keys.withIndex()) {
            if (index == keys.size - 1) {
                val v = current[key] ?: return defaultValue
                return v as T
            }
            val newMap = current[key]
            if (newMap is HashMap<*, *>) {
                current = newMap as HashMap<String, Any>
            } else {
                //取不到值
                return defaultValue
            }
        }
        return defaultValue
    }

    @Synchronized
    fun WriteProperties(key: String, value: Any) {
        CONFIG_MAP[key] = value
        SaveToYaml()
    }

    @Synchronized
    fun WriteProperties(data: Map<String, Any>) {
        data.forEach { k, v ->
            CONFIG_MAP[k] = v
        }
        SaveToYaml()
    }

    /**
     * 向嵌套的 Map 添加键值对。
     * @param key 用于导航到最内层 Map 的键列表,用逗号分隔
     * @param data 键值对

     */
    @Synchronized
    fun WritePropertiesForKey(key: String, data: Map<String, Any>) {
        val keys = splitKeys(key)
        WritePropertiesForKey(keys, data)
    }

    /**
     * 向嵌套的 Map 添加键值对。
     * @param keys 用于导航到最内层 Map 的键列表
     * @param data 键值对
     */
    @Synchronized
    fun WritePropertiesForKey(keys: Array<String>, data: Map<String, Any>) {
        var currentMap = CONFIG_MAP
        keys.dropLast(1).forEach { key ->
            currentMap = currentMap.getOrPut(key) {
                HashMap<String, Any>()
            } as HashMap<String, Any>
        }
        currentMap[keys.last()] = (currentMap.getOrPut(keys.last()) {
            HashMap<String, Any>()
        } as HashMap<String, Any>).apply {
            putAll(data)
        }
        SaveToYaml()
    }

    fun ReadDbConfig(
        DB_NAME: String,
        urlKey: String = "db.url",
        userKey: String = "db.user",
        passwordKey: String = "db.password"
    ): DbEntity {
        var url = CONFIG_MAP.getOrDefault(urlKey, "") as String
        val user = CONFIG_MAP.getOrDefault(userKey, "") as String
        val password = CONFIG_MAP.getOrDefault(passwordKey, "") as String
        if (url.isBlank()) {
            //已有配置文件中写入db配置
            url = "jdbc:sqlite:${(GlobalResource.USER_DIR).replace("\\", "/") + DB_NAME}"
            WritePropertiesForKey(
                "db",
                mapOf(
                    "url" to url,
                    "user" to user,
                    "password" to password,
                )
            )
        }
        return DbEntity(url, user, password)
    }

    private fun getLerverConfigFile(): File {
        return File(LERVER_CONFIG_PATH)
    }

    /**
     * 加载yaml文件配置信息到ConfigMap
     */
    fun LoadToConfigMap() {
        val lerverConfigFile = getLerverConfigFile()
        lerverConfigFile.inputStream().use {
            val map = YAML.load<Map<String, Any>>(it)
            map.forEach { p ->
                CONFIG_MAP[p.key] = p.value
            }
        }
        val s = 1
    }

    /**
     * 写出ConfigMap配置到yaml文件
     */
    fun SaveToYaml() {
        val lerverConfigFile = getLerverConfigFile()
        YAML.dumpAsMap(CONFIG_MAP)
        lerverConfigFile.writeText(YAML.dumpAsMap(CONFIG_MAP))
    }

}