/**
 * 队列管理 ViewModel
 *
 * 本文件提供队列管理页面的状态管理
 * 每秒自动刷新队列数据
 */

package ui.screens.queue

import androidx.compose.runtime.*
import domain.queue.TaskQueue
import domain.queue.TriggerTask
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest

/**
 * 队列管理 ViewModel 类
 *
 * 管理队列监控页面的 UI 状态
 * 提供实时队列数据展示
 *
 * @property taskQueue 任务队列
 */
class QueueManagementViewModel(private val taskQueue: TaskQueue) {
    /** 待处理任务列表状态 */
    private val _pendingTasks = mutableStateOf<List<TriggerTask>>(emptyList())
    val pendingTasks: State<List<TriggerTask>> = _pendingTasks

    /** 当前正在执行的任务状态 */
    private val _currentTask = mutableStateOf<TriggerTask?>(null)
    val currentTask: State<TriggerTask?> = _currentTask

    /** 队列大小状态 */
    private val _queueSize = mutableStateOf(0)
    val queueSize: State<Int> = _queueSize

    /** 协程作用域 */
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    /** 数据收集 Job */
    private var collectionJob: Job? = null

    /**
     * 开始收集队列数据
     *
     * 启动协程持续收集 TaskQueue 的 StateFlow 数据
     */
    fun startCollecting() {
        collectionJob?.cancel()
        collectionJob = scope.launch {
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
    fun stopCollecting() {
        collectionJob?.cancel()
        collectionJob = null
    }

    /**
     * 清理资源
     */
    fun dispose() {
        stopCollecting()
        scope.cancel()
    }
}
