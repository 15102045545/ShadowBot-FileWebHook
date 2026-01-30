/**
 * ComposeApp 模块构建脚本
 *
 * 本模块是 FileWebHook 的主应用模块，包含：
 * - Compose Multiplatform UI 界面
 * - Ktor HTTP 服务端（接收外部请求和影刀回调）
 * - Ktor HTTP 客户端（回调外部服务）
 * - SQLDelight 数据库（用户、触发器、执行记录管理）
 * - Koin 依赖注入
 */

import org.jetbrains.compose.desktop.application.dsl.TargetFormat

// ============================================
// 插件配置
// ============================================
plugins {
    alias(libs.plugins.kotlin.multiplatform)    // Kotlin 跨平台支持
    alias(libs.plugins.kotlin.serialization)    // JSON 序列化支持
    alias(libs.plugins.compose.multiplatform)   // Compose UI 框架
    alias(libs.plugins.compose.compiler)        // Compose 编译器
    alias(libs.plugins.sqldelight)              // SQLDelight 数据库
}

// ============================================
// Kotlin Multiplatform 配置
// ============================================
kotlin {
    // 目标平台：JVM Desktop（Windows/macOS/Linux）
    jvm("desktop")

    // 源代码集配置
    sourceSets {
        // Desktop 平台特定代码
        val desktopMain by getting

        // ----------------------------------------
        // 通用代码依赖（跨平台共享）
        // ----------------------------------------
        commonMain.dependencies {
            // -------- Compose UI 框架 --------
            implementation(compose.runtime)              // Compose 运行时
            implementation(compose.foundation)           // 基础布局组件
            implementation(compose.material3)            // Material Design 3 组件
            implementation(compose.ui)                   // UI 核心
            implementation(compose.components.resources) // 资源加载
            implementation(compose.materialIconsExtended) // 扩展图标库

            // -------- Ktor HTTP 服务端 --------
            implementation(libs.ktor.server.core)               // 服务端核心
            implementation(libs.ktor.server.cio)                // CIO 异步引擎
            implementation(libs.ktor.server.content.negotiation) // JSON 内容协商
            implementation(libs.ktor.server.status.pages)       // 错误状态页
            implementation(libs.ktor.server.cors)               // CORS 跨域支持

            // -------- Ktor HTTP 客户端（回调外部服务用）--------
            implementation(libs.ktor.client.core)               // 客户端核心
            implementation(libs.ktor.client.cio)                // CIO 引擎
            implementation(libs.ktor.client.content.negotiation) // JSON 内容协商

            // -------- JSON 序列化 --------
            implementation(libs.ktor.serialization.kotlinx.json) // Ktor JSON 序列化
            implementation(libs.kotlinx.serialization.json)      // Kotlinx 序列化

            // -------- Kotlinx 扩展 --------
            implementation(libs.kotlinx.coroutines.core) // 协程核心
            implementation(libs.kotlinx.datetime)        // 日期时间处理

            // -------- SQLDelight 数据库 --------
            implementation(libs.sqldelight.runtime)     // 运行时
            implementation(libs.sqldelight.coroutines)  // 协程扩展

            // -------- Koin 依赖注入 --------
            implementation(libs.koin.core)    // 核心库
            implementation(libs.koin.compose) // Compose 集成

            // -------- 日志 --------
            implementation(libs.slf4j.api) // SLF4J 日志门面
        }

        // ----------------------------------------
        // Desktop 平台特定依赖
        // ----------------------------------------
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)     // 当前操作系统的 Compose 实现
            implementation(libs.kotlinx.coroutines.swing) // Swing UI 线程调度器
            implementation(libs.sqldelight.sqlite.driver) // SQLite JDBC 驱动
            implementation(libs.logback.classic)          // Logback 日志实现
        }
    }
}

// ============================================
// SQLDelight 数据库配置
// ============================================
sqldelight {
    databases {
        // 创建名为 FileWebHookDatabase 的数据库
        create("FileWebHookDatabase") {
            // 生成的代码包名
            packageName.set("com.filewebhook.db")
        }
    }
}

// ============================================
// Compose Desktop 应用配置
// ============================================
compose.desktop {
    application {
        // 主类入口
        mainClass = "MainKt"

        // 原生分发包配置
        nativeDistributions {
            // 目标格式：Windows MSI 安装包和 EXE 安装程序
            targetFormats(TargetFormat.Msi, TargetFormat.Exe)

            // 应用包名
            packageName = "FileWebHook"
            // 版本号
            packageVersion = "1.0.0"
            // 应用描述
            description = "ShadowBot FileWebHook - HTTP to File Trigger Bridge"
            // 开发商
            vendor = "ShadowBot"

            // Windows 平台特定配置
            windows {
                // 开始菜单文件夹名称
                menuGroup = "FileWebHook"
                // 升级 UUID（用于 MSI 安装包升级识别）
                upgradeUuid = "8b5a7d9e-3f2c-4e8a-b1d6-9c4f5e7a8b3d"
            }
        }
    }
}
