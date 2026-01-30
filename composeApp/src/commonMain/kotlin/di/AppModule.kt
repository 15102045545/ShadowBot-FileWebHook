/**
 * Koin 依赖注入模块配置
 *
 * 本文件定义应用程序的所有依赖注入模块
 * 使用 Koin 框架管理单例对象的创建和依赖关系
 */

package di

import client.CallbackClient
import com.filewebhook.db.FileWebHookDatabase
import data.DatabaseDriverFactory
import data.repository.*
import domain.queue.TaskQueue
import domain.service.FileService
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import server.FileWebHookServer

/**
 * 数据层模块
 *
 * 提供数据库驱动、数据库实例和所有仓库的单例
 */
val dataModule = module {
    // 数据库驱动（平台特定实现）
    single { DatabaseDriverFactory().createDriver() }

    // SQLDelight 数据库实例
    single { FileWebHookDatabase(get()) }

    // 数据仓库
    singleOf(::SettingsRepository)                      // 设置仓库
    single { UserRepository(get()) }                    // 用户仓库
    single { TriggerRepository(get(), get()) }          // 触发器仓库（依赖 database 和 settingsRepository）
    singleOf(::ExecutionLogRepository)                  // 执行记录仓库
    singleOf(::PermissionRepository)                    // 权限仓库
}

/**
 * 领域层模块
 *
 * 提供业务逻辑相关的服务和组件
 */
val domainModule = module {
    // 回调客户端（HTTP Client）
    singleOf(::CallbackClient)

    // 文件服务（管理 request.json）
    single { FileService(get(), get()) }

    // 任务队列（核心调度组件）
    single { TaskQueue(get(), get(), get(), get(), get()) }
}

/**
 * 服务器模块
 *
 * 提供 HTTP 服务器实例
 */
val serverModule = module {
    // FileWebHook HTTP 服务器
    single { FileWebHookServer(get(), get(), get(), get()) }
}

/**
 * 所有模块列表
 *
 * 在应用启动时传递给 Koin 进行初始化
 */
val appModules = listOf(dataModule, domainModule, serverModule)
