/**
 * Gradle 项目设置文件
 *
 * 本文件配置项目的基本信息、插件仓库和依赖仓库
 */

// 项目根名称
rootProject.name = "ShadowBot-FileWebHook"

/**
 * 插件管理配置
 * 定义 Gradle 插件的下载仓库
 */
pluginManagement {
    repositories {
        google()              // Google Maven 仓库 (Android/Compose 相关)
        mavenCentral()        // Maven 中央仓库
        gradlePluginPortal()  // Gradle 插件门户
    }
}

/**
 * 依赖解析管理
 * 定义项目依赖的下载仓库
 */
dependencyResolutionManagement {
    repositories {
        google()        // Google Maven 仓库
        mavenCentral()  // Maven 中央仓库
    }
}

// 包含子模块 - composeApp 是主应用模块
include(":composeApp")
