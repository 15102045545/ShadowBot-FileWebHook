/**
 * 数据库驱动工厂 - Desktop 平台实现
 *
 * 本文件提供 Desktop 平台（Windows/macOS/Linux）的 SQLite 数据库驱动实现
 */

package data

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.filewebhook.db.FileWebHookDatabase
import java.io.File

/**
 * Desktop 平台数据库驱动工厂实现
 *
 * 使用 JDBC SQLite 驱动，数据库文件存储在用户目录下的 .filewebhook 文件夹中
 */
actual class DatabaseDriverFactory {
    /**
     * 创建 SQLite JDBC 驱动
     *
     * 数据库文件路径：{user.home}/.filewebhook/filewebhook.db
     * 如果数据库文件不存在，会自动创建并初始化表结构
     *
     * @return JDBC SQLite 驱动实例
     */
    actual fun createDriver(): SqlDriver {
        // 应用数据目录：用户主目录/.filewebhook
        val appDir = File(System.getProperty("user.home"), ".filewebhook")
        if (!appDir.exists()) {
            appDir.mkdirs()
        }

        // 数据库文件路径
        val dbFile = File(appDir, "filewebhook.db")

        // 创建 JDBC SQLite 驱动
        val driver = JdbcSqliteDriver("jdbc:sqlite:${dbFile.absolutePath}")

        // 如果数据库文件不存在，创建表结构
        if (!dbFile.exists()) {
            FileWebHookDatabase.Schema.create(driver)
        }

        return driver
    }
}
