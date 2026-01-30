/**
 * FileWebHook HTTP 服务器
 *
 * 本文件提供嵌入式 Ktor CIO 服务器的启动、停止和重启功能
 * 服务器对外暴露 REST API 供外部服务和影刀调用
 */

package server

import data.repository.PermissionRepository
import data.repository.TriggerRepository
import data.repository.UserRepository
import domain.queue.TaskQueue
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.routing.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import server.plugins.configurePlugins
import server.routes.callbackRoutes
import server.routes.triggerRoutes

/**
 * FileWebHook 服务器类
 *
 * 封装 Ktor CIO 嵌入式服务器，提供：
 * - /trigger/execute：外部服务触发执行接口
 * - /triggered：影刀开始执行回调接口
 * - /notify：影刀执行完成回调接口
 *
 * @property userRepository 用户仓库，用于身份验证
 * @property triggerRepository 触发器仓库，用于验证触发器
 * @property permissionRepository 权限仓库，用于验证权限
 * @property taskQueue 任务队列，用于任务调度
 */
class FileWebHookServer(
    private val userRepository: UserRepository,
    private val triggerRepository: TriggerRepository,
    private val permissionRepository: PermissionRepository,
    private val taskQueue: TaskQueue
) {
    /** Ktor 嵌入式服务器实例 */
    private var server: EmbeddedServer<CIOApplicationEngine, CIOApplicationEngine.Configuration>? = null

    /** 用于启动服务器的协程作用域 */
    private val scope = CoroutineScope(Dispatchers.IO)

    /** 服务器运行状态标志 */
    var isRunning: Boolean = false
        private set

    /** 当前服务器监听端口 */
    var currentPort: Int = 8089
        private set

    /**
     * 启动服务器
     *
     * 在后台协程中启动 Ktor CIO 服务器
     * 如果服务器已在运行，则直接返回不做任何操作
     *
     * @param port 监听端口，默认 8089
     * @param host 绑定地址，默认 0.0.0.0（监听所有网卡）
     */
    fun start(port: Int = 8089, host: String = "0.0.0.0") {
        // 防止重复启动
        if (isRunning) {
            return
        }

        currentPort = port

        // 在 IO 协程中启动服务器
        scope.launch {
            server = embeddedServer(CIO, port = port, host = host) {
                // 配置 Ktor 插件（JSON 序列化、CORS、错误处理）
                configurePlugins()

                // 配置路由
                routing {
                    // 外部服务触发执行路由
                    triggerRoutes(
                        userRepository = userRepository,
                        triggerRepository = triggerRepository,
                        permissionRepository = permissionRepository,
                        taskQueue = taskQueue
                    )
                    // 影刀回调路由
                    callbackRoutes(taskQueue = taskQueue)
                }
            }

            // 启动服务器（非阻塞模式）
            server?.start(wait = false)
            isRunning = true
        }
    }

    /**
     * 停止服务器
     *
     * 优雅关闭服务器：
     * - 等待 1 秒让现有连接完成
     * - 最多等待 2 秒后强制关闭
     */
    fun stop() {
        server?.stop(1000, 2000)
        server = null
        isRunning = false
    }

    /**
     * 重启服务器
     *
     * 先停止再启动，可用于端口变更后的重启
     *
     * @param port 新端口，默认使用当前端口
     * @param host 绑定地址，默认 0.0.0.0
     */
    fun restart(port: Int = currentPort, host: String = "0.0.0.0") {
        stop()
        start(port, host)
    }
}
