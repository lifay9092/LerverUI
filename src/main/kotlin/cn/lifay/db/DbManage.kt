package cn.lifay.db

import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.logging.ConsoleLogger
import org.ktorm.logging.LogLevel
import org.ktorm.logging.Logger
import org.ktorm.schema.BaseTable
import org.ktorm.schema.ColumnDeclaring
import org.ktorm.support.sqlite.InsertOrUpdateStatementBuilder
import org.ktorm.support.sqlite.insertOrUpdate
import java.io.File
import java.io.FileFilter
import java.nio.charset.Charset
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

/**
 *@ClassName DbManage
 *@Description DB管理工具,使用:DbManage.Init()
 *@Author lifay
 *@Date 2023/6/14 20:44
 **/
object DbManage {

    private lateinit var DB_NAME :String
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

    fun Init(DB_NAME: String = "db.db") {
        //获取db配置文件
        val userDir = System.getProperty("user.dir")
        val dbConfigPath = "${userDir + File.separator}db.config"
        val dbConfigFile = File(dbConfigPath)
        if (!dbConfigFile.exists()) {
            //拷贝db.db
            val resourceAsStream = DbManage.javaClass.getResourceAsStream("/db/db.db")
            if (resourceAsStream == null) {
                throw Exception("失败")
            }
            val dbPath = userDir + File.separator + DB_NAME
            resourceAsStream.use {
                File(dbPath).writeBytes(it.readAllBytes())
            }
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
        /*更新版本脚本*/
        //db最后一次版本
        val lastVersion = GetLastVersion(true)
        if (lastVersion.isBlank()) {
            throw Exception("初始化db失败")
        }
        val lastVersionNo = lastVersion.toInt()
        //脚本目录
        val sqlDir = File("${System.getProperty("user.dir") + File.separator}脚本")
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
                println(sqlFile.name)
                // val result = ExecuteSql(*sqlFile.readText().split(";").filter { it.trim().isNotBlank() }.map { "$it;" }.toTypedArray())
                val result = ExecuteSql(sqlFile.readText())
                if (!result) {
                    throw Exception("升级版本失败:${sqlFile.name}")
                } else {
                    println("${sqlFile.name} 升级成功...")
                    newLasVersion = sqlFileName
                }
            }
            if (lastVersion != newLasVersion) {
                //版本号不一致,更新版本
                ExecuteSql(INSERT_NEW_VERSION_SQL.format(newLasVersion))
                GetLastVersion()
            }
        }
    }

    fun ExecuteSql(sql: String): Boolean {
        try {
            database.useConnection { connection ->
                val runner = ScriptRunner(connection, true, true)
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
                connection.prepareStatement(GET_LAST_VERSION).use { statement ->
                    statement.executeQuery().getString(1)
                }
            }
            println("当前版本号:$version")
            return version
        } catch (e: Exception) {
            //新建库表
            if (init && e.message?.contains("no such table: APP_VERSION") == true) {
                val createVersionTb = database.useConnection { connection ->
                    try {
                        return@useConnection ExecuteSql(CREATE_VERSION_TB_SQL + "\n" + INSERT_NEW_VERSION_SQL.format("0"))

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
            throw Exception("请先初始化数据库连接:Config()")
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
        return database.batchUpdate(t, block)
    }

    /*
        delete(Employees) { it.id eq 4 }
     */
    inline fun <reified T : BaseTable<*>> delete(t: T, noinline predicate: (T) -> ColumnDeclaring<Boolean>): Int {
        return database.delete(t, predicate)
    }

    inline fun <reified T : BaseTable<*>> deleteAll(t: T): Int {
        return database.deleteAll(t)
    }

    /*

    fun find(predicate: (Table<E>) -> ColumnDeclaring<Boolean>): E? {
        return SEQUENCE.find(predicate)
    }

    fun filter(predicate: (Table<E>) -> ColumnDeclaring<Boolean>): EntitySequence<E, Table<E>> {
        return SEQUENCE.filter(predicate)
    }

    fun add(entity: E): Int {
        return SEQUENCE.add(entity)
    }

    fun update(entity: E): Int {
        return SEQUENCE.update(entity)
    }

    fun delete(predicate: (Table<E>) -> ColumnDeclaring<Boolean>): Boolean {
        val entity = find(predicate) ?: return false
        entity.delete()
        return true
    }*/

}