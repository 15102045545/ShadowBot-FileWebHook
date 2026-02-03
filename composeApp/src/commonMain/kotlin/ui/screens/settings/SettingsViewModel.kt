/**
 * 系统设置 ViewModel
 *
 * 本文件提供系统设置页面的状态管理和业务逻辑
 * 管理 HTTP 服务器的启停和系统配置，以及队列信息的实时监控
 */

package ui.screens.settings

import androidx.compose.runtime.*
import data.model.AppSettings
import data.repository.SettingsRepository
import domain.python.ShadowBotPythonFinder
import domain.queue.TaskQueue
import domain.queue.TriggerTask
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import server.FileWebHookServer
import java.awt.Desktop
import java.io.File
import kotlin.system.exitProcess

/**
 * 设置 ViewModel 类
 *
 * 管理系统设置页面的 UI 状态和服务控制
 * 同时管理队列信息的实时展示
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

    // ==========================================
    // 队列信息相关状态
    // ==========================================

    /** 待处理任务列表状态 */
    private val _pendingTasks = mutableStateOf<List<TriggerTask>>(emptyList())
    val pendingTasks: State<List<TriggerTask>> = _pendingTasks

    /** 当前正在执行的任务状态 */
    private val _currentTask = mutableStateOf<TriggerTask?>(null)
    val currentTask: State<TriggerTask?> = _currentTask

    /** 协程作用域 */
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    /** 队列数据收集 Job */
    private var queueCollectionJob: Job? = null

    // ==========================================
    // 运行时数据目录相关
    // ==========================================

    /**
     * 获取运行时数据目录路径
     *
     * @return 运行时数据目录的绝对路径
     */
    fun getRuntimeDataPath(): String {
        return File(System.getProperty("user.home"), ".filewebhook").absolutePath
    }

    /**
     * 打开运行时数据目录
     *
     * 使用系统默认文件管理器打开目录
     */
    fun openRuntimeDataDirectory() {
        try {
            val dataDir = File(getRuntimeDataPath())
            if (!dataDir.exists()) {
                dataDir.mkdirs()
            }
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(dataDir)
            }
        } catch (e: Exception) {
            // 忽略打开失败的异常
        }
    }

    /**
     * 清空运行时数据目录并退出应用
     *
     * 删除所有运行时数据后关闭应用程序
     */
    fun clearRuntimeDataAndExit() {
        try {
            // 停止服务器和队列
            server.stop()
            taskQueue.stop()

            // 删除运行时数据目录
            val dataDir = File(getRuntimeDataPath())
            if (dataDir.exists()) {
                dataDir.deleteRecursively()
            }

            // 退出应用
            exitProcess(0)
        } catch (e: Exception) {
            // 即使发生异常也尝试退出
            exitProcess(1)
        }
    }

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

    // ==========================================
    // 队列数据收集方法
    // ==========================================

    /**
     * 开始收集队列数据
     *
     * 启动协程持续收集 TaskQueue 的 StateFlow 数据
     */
    fun startQueueDataCollection() {
        queueCollectionJob?.cancel()
        queueCollectionJob = scope.launch {
            // 并行收集三个 StateFlow
            launch {
                taskQueue.pendingTasksFlow.collectLatest { tasks ->
                    _pendingTasks.value = tasks
                }
            }
            launch {
                taskQueue.currentTask.collectLatest { task ->
                    _currentTask.value = task
                }
            }
            launch {
                taskQueue.queueSize.collectLatest { size ->
                    _queueSize.value = size
                }
            }
        }
    }

    /**
     * 停止收集队列数据
     */
    fun stopQueueDataCollection() {
        queueCollectionJob?.cancel()
        queueCollectionJob = null
    }

    /**
     * 清理资源
     */
    fun dispose() {
        stopQueueDataCollection()
        scope.cancel()
    }
}
