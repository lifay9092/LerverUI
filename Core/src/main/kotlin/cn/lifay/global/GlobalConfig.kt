package cn.lifay.global

import java.io.File
import java.nio.charset.Charset
import java.util.*

data class DbEntity(

    val url: String,
    val user: String,
    val password: String
)

object GlobalConfig {

    private lateinit var LERVER_CONFIG_PATH: String

    private val CONFIG_MAP = HashMap<String, String>()

    fun InitLerverConfigPath(configPath: String) {
        LERVER_CONFIG_PATH = configPath
        val file = File(LERVER_CONFIG_PATH)
        if (!file.exists()) {
            file.parentFile.mkdirs()
            file.createNewFile()
        }
        ReloadConfigMap()
    }

    fun ContainsKey(key: String): Boolean {
        return CONFIG_MAP.containsKey(key)
    }

    fun ReadProperties(key: String, defaultValue: String = ""): String {
        return if (CONFIG_MAP.containsKey(key)) {
            CONFIG_MAP[key]!!
        } else {
            if (defaultValue.isNotBlank()) {
                WriteProperties(key, defaultValue)
            }
            defaultValue
        }
    }

    @Synchronized
    fun WriteProperties(key: String, value: String) {
        val lerverConfigFile = getLerverConfigFile()
        if (lerverConfigFile.exists()) {
            val properties = Properties()
            lerverConfigFile.inputStream().use {
                properties.load(it)
                properties.setProperty(key, value)
            }
            lerverConfigFile.outputStream().use {
                properties.store(it, "")
            }
        } else {
            lerverConfigFile.writeText(
                """
                ${key}=$value
                
            """.trimIndent(), Charset.forName("utf-8")
            )
        }
        ReloadConfigMap()
    }

    @Synchronized
    fun WriteProperties(data: Map<String, String>) {
        val lerverConfigFile = getLerverConfigFile()
        if (lerverConfigFile.exists()) {
            val properties = Properties()
            lerverConfigFile.inputStream().use {
                properties.load(it)
                data.forEach { key, value ->
                    properties.setProperty(key, value)
                }
            }
            lerverConfigFile.outputStream().use {
                properties.store(it, "")
            }
        } else {
            val text = data.map {
                "${it.key}=${it.value}"
            }.joinToString("\n")
            lerverConfigFile.writeText(text, Charset.forName("utf-8"))
        }
        ReloadConfigMap()
    }

    fun ReadDbConfig(
        DB_NAME: String,
        urlKey: String = "db.url",
        userKey: String = "db.user",
        passwordKey: String = "db.password"
    ): DbEntity {
        var url = CONFIG_MAP.getOrDefault(urlKey, "")
        val user = CONFIG_MAP.getOrDefault(userKey, "")
        val password = CONFIG_MAP.getOrDefault(passwordKey, "")
        if (url.isBlank()) {
            //已有配置文件中写入db配置
            url = "jdbc:sqlite:${(GlobalResource.USER_DIR).replace("\\", "/") + DB_NAME}"
            WriteProperties(
                mapOf(
                    "db.url" to url,
                    "db.user" to user,
                    "db.password" to password,
                )
            )
        }
        return DbEntity(url, user, password)
    }

    @Synchronized
    fun WriteDbConfig(dbEntity: DbEntity) {
        getLerverConfigFile().writeText(
            """
                        db.url = ${dbEntity.url}
                        db.user = ${dbEntity.user}
                        db.password = ${dbEntity.password}
                        
                    """.trimIndent(), Charset.forName("utf-8")
        )
        ReloadConfigMap()
    }

    private fun getLerverConfigFile(): File {
        return File(LERVER_CONFIG_PATH)
    }

    private fun ReloadConfigMap() {
        val lerverConfigFile = getLerverConfigFile()
        val properties = Properties()
        lerverConfigFile.inputStream().use {
            properties.load(it)
            CONFIG_MAP.clear()
            properties.forEach { p ->
                CONFIG_MAP[p.key.toString()] = p.value.toString()
            }

        }
    }

}