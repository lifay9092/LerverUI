package cn.lifay.db

import cn.lifay.exception.LerverUIException
import cn.lifay.extension.toCamelCase
import cn.lifay.logutil.LerverLog
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.logging.ConsoleLogger
import org.ktorm.logging.LogLevel
import org.ktorm.logging.Logger
import org.ktorm.schema.BaseTable
import org.ktorm.schema.Column
import org.ktorm.schema.ColumnDeclaring
import org.ktorm.support.sqlite.InsertOrUpdateStatementBuilder
import org.ktorm.support.sqlite.insertOrUpdate
import java.io.File
import java.io.FileFilter
import java.nio.charset.Charset
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

/**
 *@ClassName DbManage
 *@Description DB管理工具,使用:DbManage.Init()
 *@Author lifay
 *@Date 2023/6/14 20:44
 **/
object DbManage {

    val GET_LAST_VERSION = """
                                SELECT MAX(VERSION_NO) FROM APP_VERSION
                          """
    val CREATE_VERSION_TB_SQL = """
                                CREATE TABLE APP_VERSION(
                                   VERSION_NO INTEGER PRIMARY KEY ,
                                   TIME TEXT NOT NULL
                                );
                          """
    val INSERT_NEW_VERSION_SQL = """
                                INSERT INTO APP_VERSION (VERSION_NO,TIME)
                                VALUES (%s, '${LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)}' );
                          """
    lateinit var database: Database
    const val WRAP = "\n"
    fun Init(DB_NAME: String = "db.db") {

        try {//获取db配置文件
            val userDir = System.getProperty("user.dir")
            val dbConfigPath = "${userDir + File.separator}db.config"
            val dbConfigFile = File(dbConfigPath)
            if (!dbConfigFile.exists()) {
                LerverLog.debug("db.config 不存在...")
                //拷贝db.db
    //            val resourceAsStream = cn.lifay.db.DbManage.javaClass.getResourceAsStream("/db/db.db")
    //            if (resourceAsStream == null) {
    //                throw LerverUIException("失败")
    //            }
                val dbPath = userDir + File.separator + DB_NAME
    //            resourceAsStream.use {
    //                File(dbPath).writeBytes(it.readAllBytes())
    //            }
                //配置数据库连接文件:db.config
                val jdbcUrl = "jdbc:sqlite:${dbPath}".replace("\\", "/")
                val text = """
                        url = ${jdbcUrl}
                        user =
                        password =
                    """.trimIndent()
                dbConfigFile.writeText(text, Charset.forName("utf-8"))
                InitDataBase(jdbcUrl, "", "")
            } else {
                LerverLog.debug("db.config 已存在...")
                dbConfigFile.inputStream().use {
                    val properties = Properties()
                    properties.load(it)
                    val url =
                        properties.getProperty(
                            "url",
                            "jdbc:sqlite:${(userDir + File.separator).replace("\\", "/") + DB_NAME}"
                        )
                    val user = properties.getProperty("user", "")
                    val password = properties.getProperty("password", "")
                    InitDataBase(url, user, password)
                }
            }
        } catch (e: Exception) {
            LerverLog.error(e)
        }

    }

    fun InitDataBase(
        url: String,
        user: String,
        password: String,
        logger: Logger = ConsoleLogger(threshold = LogLevel.DEBUG)
    ) {
        database = Database.connect(
            url = url,
            user = user,
            password = password,
            logger = logger
        )
        LerverLog.debug("dbUrl:${url + WRAP + WRAP}")

        /*更新版本脚本*/
        //db最后一次版本
        val lastVersion = GetLastVersion(true)
        if (lastVersion.isBlank()) {
            throw LerverUIException("初始化db失败")
        }
        val lastVersionNo = lastVersion.toInt()
        //脚本目录
        val sqlDir = File("${System.getProperty("user.dir") + File.separator}scripts")
        if (sqlDir.exists()) {
            var newLasVersion = lastVersion
            for (sqlFile in sqlDir.listFiles(FileFilter { it.isFile && it.name.endsWith(".sql") })
                .sortedBy { it.name }) {
                val sqlFileNames = sqlFile.name.split(".")
                val sqlFileName = sqlFileNames[0]
                if (sqlFileName.toInt() <= lastVersionNo) {
                    continue
                }
                //执行脚本
                LerverLog.debug(sqlFile.name + WRAP)
                // val result = ExecuteSql(*sqlFile.readText().split(";").filter { it.trim().isNotBlank() }.map { "$it;" }.toTypedArray())
                val result = ExecuteSql(sqlFile.readText())
                if (!result) {
                    throw LerverUIException("升级版本失败:${sqlFile.name}")
                } else {
                    LerverLog.debug("${sqlFile.name} 升级成功...")
                    newLasVersion = sqlFileName
                }
                LerverLog.debug(WRAP)
                LerverLog.debug(WRAP)
            }
            if (lastVersion != newLasVersion) {
                //版本号不一致,更新版本
                ExecuteSql(INSERT_NEW_VERSION_SQL.format(newLasVersion))
                GetLastVersion()
                LerverLog.debug("更新完成")
            }
        }
    }

    fun ExecuteSql(sql: String): Boolean {
        try {
            VerifyConfig()
            database.useConnection { connection ->
                val runner = cn.lifay.db.ScriptRunner(connection, true, true)
                runner.runScript(sql.reader())
                /*connection.createStatement().use { statement ->
                    for (sql in sqls) {
                        if (sql.isBlank()) {
                            continue
                        }
                        println(sql)
                       // statement.addBatch(sql)
                        //statement.executeBatch()
                        statement.
                        println(statement.execute(sql))
//                        for (i in statement.executeBatch()) {
//                            println(i)
//                        }
                    }

                }*/
                return true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }


    fun GetLastVersion(init: Boolean = false): String {
        try {
            val version = database.useConnection { connection ->
                connection.prepareStatement(cn.lifay.db.DbManage.GET_LAST_VERSION).use { statement ->
                    statement.executeQuery().getString(1)
                }
            }
            LerverLog.debug("当前版本号:$version")
            return version
        } catch (e: Exception) {
            //新建库表
            if (init && e.message?.contains("no such table: APP_VERSION") == true) {
                LerverLog.debug("新建版本表[APP_VERSION]")
                val createVersionTb = database.useConnection { connection ->
                    try {
                        return@useConnection ExecuteSql(
                            cn.lifay.db.DbManage.CREATE_VERSION_TB_SQL + "\n" + INSERT_NEW_VERSION_SQL.format(
                                "0"
                            )
                        )

                    } catch (e: Exception) {
                        e.printStackTrace()
                        return@useConnection false
                    }
                }
                if (createVersionTb) {
                    return GetLastVersion()
                }
            } else {
                e.printStackTrace()
            }
        }
        return ""
    }


    fun VerifyConfig() {
        if (!this::database.isInitialized) {
            LerverLog.error("请先初始化数据库连接:Init()")
            throw LerverUIException("请先初始化数据库连接:Init()")
        }
    }

    /*
        from(Employees)
        .select(Employees.name)
        .whereWithConditions {
            if (someCondition) {
                it += Employees.managerId.isNull()
            }
            if (otherCondition) {
                it += Employees.departmentId eq 1
            }
        }

        from(emp)
           .leftJoin(dept, on = emp.departmentId eq dept.id)
            .leftJoin(mgr, on = emp.managerId eq mgr.id)
            .select(emp.name, mgr.name, dept.name)
            .orderBy(emp.id.asc())
            .map { row ->
                Names(
                    name = row[emp.name],
                    managerName = row[mgr.name],
                    departmentName = row[dept.name]
                )
            }
    */
    inline fun <reified T : BaseTable<*>> from(t: T): QuerySource {
        VerifyConfig()
        return database.from(t)
    }

    /*
          insertOrUpdate(Employees) {
                set(it.id, 1)
                set(it.name, "vince")
                set(it.job, "engineer")
                set(it.salary, 1000)
                set(it.hireDate, LocalDate.now())
                set(it.departmentId, 1)
                onConflict(it.id) {
                    set(it.salary, it.salary + 900)
                }
            }
      */
    inline fun <reified T : BaseTable<*>> insertOrUpdate(
        t: T,
        noinline block: InsertOrUpdateStatementBuilder.(T) -> Unit
    ): Int {
        VerifyConfig()
        return database.insertOrUpdate(t, block)
    }

    /*
           insert(Employees) {
                set(it.name, "jerry")
                set(it.job, "trainee")
                set(it.managerId, 1)
                set(it.hireDate, LocalDate.now())
                set(it.salary, 50)
                set(it.departmentId, 1)
            }
       */
    inline fun <reified T : BaseTable<*>> insert(t: T, noinline block: AssignmentsBuilder.(T) -> Unit): Int {
        VerifyConfig()
        return database.insert(t, block)
    }

    /*
         batchInsert(Employees) {
            item {
                set(it.name, "jerry")
                set(it.job, "trainee")
                set(it.managerId, 1)
                set(it.hireDate, LocalDate.now())
                set(it.salary, 50)
                set(it.departmentId, 1)
            }
            item {
                set(it.name, "linda")
                set(it.job, "assistant")
                set(it.managerId, 3)
                set(it.hireDate, LocalDate.now())
                set(it.salary, 100)
                set(it.departmentId, 2)
            }
        }
     */
    inline fun <reified T : BaseTable<*>> batchInsert(
        t: T,
        noinline block: BatchInsertStatementBuilder<T>.() -> Unit
    ): IntArray {
        VerifyConfig()
        return database.batchInsert(t, block)
    }

    /*
          database.update(Employees) {
            set(it.job, "engineer")
            set(it.managerId, null)
            set(it.salary, 100)
            where {
                it.id eq 2
            }
        }
      */
    inline fun <reified T : BaseTable<*>> update(t: T, noinline block: UpdateStatementBuilder.(T) -> Unit): Int {
        VerifyConfig()
        return database.update(t, block)
    }

    /*
        batchUpdate(Departments) {
            for (i in 1..2) {
                item {
                    set(it.location, "Hong Kong")
                    where {
                        it.id eq i
                    }
                }
            }
        }
    */
    inline fun <reified T : BaseTable<*>> batchUpdate(
        t: T,
        noinline block: BatchUpdateStatementBuilder<T>.() -> Unit
    ): IntArray {
        VerifyConfig()
        return database.batchUpdate(t, block)
    }

    /*
        delete(Employees) { it.id eq 4 }
     */
    inline fun <reified T : BaseTable<*>> delete(t: T, noinline predicate: (T) -> ColumnDeclaring<Boolean>): Int {
        VerifyConfig()
        return database.delete(t, predicate)
    }

    inline fun <reified T : BaseTable<*>> deleteAll(t: T): Int {
        VerifyConfig()
        return database.deleteAll(t)
    }


    /**
     * 为BaseTable新增扩展方法:添加实体直接入库
     */
    inline fun <reified T : Any> BaseTable<T>.add(entity: T): Int {
        val kClass = entity::class
        val memberProperties = kClass.memberProperties
        val temp = HashMap<String, Any?>()
        for (memberProperty in memberProperties) {
            memberProperty.isAccessible
            val prop = memberProperty.name
            val value = memberProperty.call(entity)
            temp[prop] = value
        }
        insert(this) { tb ->
            columns.forEach { col ->
                val value = temp[col.name.toCamelCase()]
                if (value != null) {
                    when (value::class.java) {
                        java.lang.Boolean::class.java -> {
                            set(col as Column<Boolean>, value as Boolean)
                        }

                        java.lang.String::class.java -> {
                            set(col as Column<String>, value as String)
                        }

                        java.lang.Integer::class.java -> {
                            set(col as Column<Integer>, value as Integer)
                        }

                        java.lang.Double::class.java -> {
                            set(col as Column<Double>, value as Double)
                        }

                        java.lang.Float::class.java -> {
                            set(col as Column<Float>, value as Float)
                        }

                        java.lang.Long::class.java -> {
                            set(col as Column<Long>, value as Long)
                        }

                        else -> {
                            LerverLog.error("not surport")
                            set(col as Column<String>, value.toString())
                        }
                    }
                }
            }
        }
        return 1
    }

    /**
     * 为BaseTable新增扩展方法:更新实体直接入库
     */
    inline fun <reified T : Any> BaseTable<T>.update(entity: T): Int {
        val kClass = entity::class
        val memberProperties = kClass.memberProperties
        val temp = HashMap<String, Any?>()
        var pkName: String? = null
        for (memberProperty in memberProperties) {
            memberProperty.isAccessible
            val prop = memberProperty.name
            val value = memberProperty.call(entity)
            val primary = isPrimary(this, prop)
            if (primary) {
                if (value == null) {
                    LerverLog.error("主键值不能为空!")
                    throw LerverUIException("主键值不能为空!")
                }
                pkName = prop
            }
            temp[prop] = value
        }
        if (pkName == null) {
            LerverLog.error("未检测到主键值!")
            throw LerverUIException("未检测到主键值!")
        }
        update(this) { tb ->
            columns.forEach { col ->
                val value = temp[col.name.toCamelCase()]
                if (value != null) {
                    when (value::class.java) {
                        java.lang.Boolean::class.java -> {

                            set(col as Column<Boolean>, value as Boolean)
                        }

                        java.lang.String::class.java -> {
                            set(col as Column<String>, value as String)
                            if (pkName == col.name.toCamelCase()) {
                                where {
                                    col eq value
                                }
                            }
                        }

                        java.lang.Integer::class.java -> {
                            set(col as Column<Integer>, value as Integer)
                            if (pkName == col.name.toCamelCase()) {
                                where {
                                    col eq value
                                }
                            }
                        }

                        java.lang.Double::class.java -> {
                            set(col as Column<Double>, value as Double)
                        }

                        java.lang.Float::class.java -> {
                            set(col as Column<Float>, value as Float)
                        }

                        java.lang.Long::class.java -> {
                            set(col as Column<Long>, value as Long)
                            if (pkName == col.name.toCamelCase()) {
                                where {
                                    col eq value
                                }
                            }
                        }

                        else -> {
                            LerverLog.error("not surport")
                            set(col as Column<String>, value.toString())
                            if (pkName == col.name.toCamelCase()) {
                                where {
                                    col eq value.toString()
                                }
                            }
                        }
                    }

                }
            }

        }
        return 1
    }

    fun <T : Any> isPrimary(table: BaseTable<T>, colName: String): Boolean {
        for (primaryKey in table.primaryKeys) {
            if (primaryKey.name == colName) {
                return true
            }
        }
        return false
    }

    fun likeKeyword(keyword:String):String{
        if (!keyword.contains("%")) {
            return "%${keyword}%"
        }
        return keyword
    }
}