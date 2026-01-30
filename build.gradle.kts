/**
 * 根项目构建脚本
 *
 * 本文件声明项目使用的 Gradle 插件，但不直接应用 (apply false)
 * 具体的插件应用在子模块 (composeApp) 中进行
 */

plugins {
    // Kotlin Multiplatform 插件 - 支持跨平台开发
    alias(libs.plugins.kotlin.multiplatform) apply false

    // Kotlin 序列化插件 - 用于 JSON 序列化/反序列化
    alias(libs.plugins.kotlin.serialization) apply false

    // Compose Multiplatform 插件 - JetBrains 跨平台 UI 框架
    alias(libs.plugins.compose.multiplatform) apply false

    // Compose 编译器插件 - Compose 代码编译支持
    alias(libs.plugins.compose.compiler) apply false

    // SQLDelight 插件 - 类型安全的 SQL 数据库框架
    alias(libs.plugins.sqldelight) apply false
}
