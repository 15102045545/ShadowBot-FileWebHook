/**
 * 数据库驱动工厂 - 公共接口
 *
 * 本文件定义了数据库驱动创建的 expect 声明
 * 具体实现在各平台的 actual 实现中（如 desktopMain）
 */

package data

import app.cash.sqldelight.db.SqlDriver

/**
 * 数据库驱动工厂
 *
 * 用于创建平台特定的 SQLite 数据库驱动
 * 这是 Kotlin Multiplatform 的 expect/actual 模式
 */
expect class DatabaseDriverFactory() {
    /**
     * 创建 SQL 驱动
     *
     * @return 平台特定的 SqlDriver 实例
     */
    fun createDriver(): SqlDriver
}
