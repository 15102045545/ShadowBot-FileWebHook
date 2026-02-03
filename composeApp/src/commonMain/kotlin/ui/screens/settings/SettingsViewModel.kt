/**
 * 系统设置 ViewModel
 *
 * 本文件提供系统设置页面的状态管理和业务逻辑
 * 管理 HTTP 服务器的启停和系统配置
 */

package ui.screens.settings

import androidx.compose.runtime.*
import data.model.AppSettings
import data.repository.SettingsRepository
import domain.python.ShadowBotPythonFinder
import domain.queue.TaskQueue
import server.FileWebHookServer

/**
 * 设置 ViewModel 类
 *
 * 管理系统设置页面的 UI 状态和服务控制
 *
 * @property settingsRepository 设置仓库
 * @property server HTTP 服务器实例
 * @property taskQueue 任务队列实例
 * @property pythonFinder Python 解释器查找器
 */
class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val server: FileWebHookServer,
    private val taskQueue: TaskQueue,
    private val pythonFinder: ShadowBotPythonFinder = ShadowBotPythonFinder()
) {
    /** 当前设置状态 */
    private val _settings = mutableStateOf(AppSettings.DEFAULT)
    val settings: State<AppSettings> = _settings

    /** 加载中状态 */
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    /** 服务器运行状态 */
    private val _serverRunning = mutableStateOf(false)
    val serverRunning: State<Boolean> = _serverRunning

    /** 当前队列大小 */
    private val _queueSize = mutableStateOf(0)
    val queueSize: State<Int> = _queueSize

    /** 检测到的 Python 解释器路径 */
    private val _detectedPythonPath = mutableStateOf("")
    val detectedPythonPath: State<String> = _detectedPythonPath

    /** Python 检测消息 */
    private val _pythonDetectionMessage = mutableStateOf<String?>(null)
    val pythonDetectionMessage: State<String?> = _pythonDetectionMessage

    /**
     * 加载系统设置
     *
     * 从数据库读取设置并更新服务器状态
     * 首次加载时会初始化默认配置（如果数据库为空）
     */
    suspend fun loadSettings() {
        _isLoading.value = true
        try {
            // 确保数据库中有默认配置
            settingsRepository.initializeDefaultSettings()
            _settings.value = settingsRepository.getSettings()
            _serverRunning.value = server.isRunning
        } finally {
            _isLoading.value = false
        }
    }

    /**
     * 保存系统设置
     *
     * 将设置保存到数据库
     *
     * @param settings 新的设置对象
     * @return Result<Unit> 保存结果
     */
    suspend fun saveSettings(settings: AppSettings): Result<Unit> {
        return try {
            settingsRepository.saveSettings(settings)
            _settings.value = settings
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 启动 HTTP 服务器
     *
     * 同时启动服务器和任务队列
     */
    suspend fun startServer() {
        val currentSettings = _settings.value
        server.start(port = currentSettings.httpPort)
        taskQueue.start()
        _serverRunning.value = true
    }

    /**
     * 停止 HTTP 服务器
     *
     * 同时停止服务器和任务队列
     */
    fun stopServer() {
        server.stop()
        taskQueue.stop()
        _serverRunning.value = false
    }

    /**
     * 重启服务器
     *
     * 停止服务器并清空队列，然后重新启动
     * 用于让配置更改（端口、队列长度等）生效
     */
    suspend fun restartServer() {
        // 停止服务器
        server.stop()
        // 清空队列并停止
        taskQueue.clearAndStop()
        _serverRunning.value = false
        // 重新启动服务器
        startServer()
    }

    /**
     * 应用启动时自动启动服务器
     *
     * 加载设置后自动启动 HTTP 服务器和任务队列
     */
    suspend fun autoStartServerOnLaunch() {
        loadSettings()
        if (!_serverRunning.value) {
            startServer()
        }
    }

    /**
     * 检测影刀 Python 解释器
     *
     * 自动扫描系统中的影刀安装目录，定位 Python 解释器
     */
    fun detectPythonInterpreter() {
        val result = pythonFinder.findShadowBotPython()
        if (result.success) {
            _detectedPythonPath.value = result.pythonPath
            _pythonDetectionMessage.value = "影刀版本: ${result.shadowbotVersion}, Python ${result.version}"
        } else {
            _detectedPythonPath.value = ""
            _pythonDetectionMessage.value = result.message
        }
    }

}
