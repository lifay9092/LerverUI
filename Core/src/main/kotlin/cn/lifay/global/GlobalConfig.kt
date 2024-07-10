package cn.lifay.global

import com.moandjiezana.toml.Toml
import com.moandjiezana.toml.TomlWriter
import java.io.File
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.util.*


data class DbEntity(

    val url: String,
    val user: String,
    val password: String
)

object GlobalConfig {

    private lateinit var LERVER_CONFIG_PATH: String

    private val CONFIG_MAP = HashMap<String, Any>()
    private val CHAR = Charset.defaultCharset()
    private val TOML_WRITER = TomlWriter.Builder()
        .indentValuesBy(2)
        .indentTablesBy(4)
        .padArrayDelimitersBy(0)
        .build()

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

    fun readConfig(): Toml {
        val lerverConfigFile = getLerverConfigFile()
        if (lerverConfigFile.exists()) {
            val toml = Toml()
            lerverConfigFile.inputStream().use {
                toml.read(it)
                val map = toml.toMap()

            }

        }

    }
}
    @Synchronized
    fun WriteProperties(key: String, value: Any) {
        WriteProperties(mapOf(Pair(key, value)))
    }

    @Synchronized
    fun WriteProperties(data: Map<String, Any>) {
        val lerverConfigFile = getLerverConfigFile()
        if (lerverConfigFile.exists()) {
            val toml = Toml()
            lerverConfigFile.inputStream().use {
                InputStreamReader(it).use {
                    toml.read(it)
                    val map = toml.toMap()
                    data.forEach { key, value ->
                        map[key] = value
                    }
                }

            }

        }
        lerverConfigFile.outputStream().use {
            TOML_WRITER.write(data, it)
        }
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
                        
                    """.trimIndent(), CHAR
        )
    }

    private fun getLerverConfigFile(): File {
        return File(LERVER_CONFIG_PATH)
    }
//
//    private fun ReloadConfigMap() {
//        val lerverConfigFile = getLerverConfigFile()
//        val toml = Toml()
//        lerverConfigFile.inputStream().use {
//            CONFIG_MAP.clear()
//            toml.read(it)
//            val map = toml.toMap()
//            map.forEach { key, value ->
//                CONFIG_MAP[key] = value
//            }
//        }
//    }

}