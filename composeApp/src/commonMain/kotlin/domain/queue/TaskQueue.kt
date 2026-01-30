/**
 * 任务队列
 *
 * 本文件实现 FIFO 任务队列，是 FileWebHook 的核心调度组件
 * 确保触发器执行的串行化处理（一次只执行一个任务）
 *
 * 任务生命周期：
 * REQUESTED → QUEUED → PRE_RESPONDED → FILE_WRITTEN → EXECUTING → COMPLETED → CALLBACK_SUCCESS/FAILED
 */

package domain.queue

import client.CallbackClient
import data.model.ExecutionStatus
import data.model.ExternalCallbackRequest
import data.repository.ExecutionLogRepository
import data.repository.SettingsRepository
import domain.service.FileService
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

/**
 * 触发任务数据类
 *
 * 封装一次触发执行的所有必要信息
 *
 * @property eventId 事件唯一标识
 * @property triggerId 触发器 ID
 * @property userId 调用用户 ID
 * @property requestParam 业务请求参数
 * @property requestTime 请求时间
 * @property callbackUrl 外部服务回调 URL
 * @property enqueueTime 入队时间
 */
data class TriggerTask(
    val eventId: String,
    val triggerId: Long,
    val userId: String,
    val requestParam: JsonObject,
    val requestTime: String,
    val callbackUrl: String,
    val enqueueTime: Instant = Clock.System.now()
)

/**
 * 入队结果数据类
 *
 * @property success 是否成功入队
 * @property queuePosition 在队列中的位置（-1 表示入队失败）
 */
data class EnqueueResult(
    val success: Boolean,
    val queuePosition: Int
)

/**
 * 任务队列类
 *
 * 使用 Kotlin Channel 实现 FIFO 队列，特点：
 * - 有界队列：队列满时拒绝新任务
 * - 串行消费：一次只处理一个任务
 * - 状态追踪：提供队列大小和当前任务的 StateFlow
 *
 * @property executionLogRepository 执行记录仓库
 * @property settingsRepository 设置仓库
 * @property fileService 文件服务
 * @property callbackClient 回调客户端
 */
class TaskQueue(
    private val executionLogRepository: ExecutionLogRepository,
    private val settingsRepository: SettingsRepository,
    private val fileService: FileService,
    private val callbackClient: CallbackClient
) {
    /** 队列处理协程作用域 */
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    /** JSON 序列化器 */
    private val json = Json { prettyPrint = true }

    /** 队列最大长度，从设置中读取 */
    private var maxQueueLength = 50

    /** 任务通道（有界 Channel） */
    private var taskChannel: Channel<TriggerTask>? = null

    /** 待处理任务列表（用于追踪队列状态） */
    private val pendingTasks = mutableListOf<TriggerTask>()

    /** 互斥锁，保护 pendingTasks 的并发访问 */
    private val mutex = Mutex()

    /** 当前正在执行的任务（StateFlow 供 UI 观察） */
    private val _currentTask = MutableStateFlow<TriggerTask?>(null)
    val currentTask: StateFlow<TriggerTask?> = _currentTask

    /** 当前队列大小（StateFlow 供 UI 观察） */
    private val _queueSize = MutableStateFlow(0)
    val queueSize: StateFlow<Int> = _queueSize

    /** 当前任务完成信号（用于等待影刀回调） */
    private var currentTaskCompletion: CompletableDeferred<Unit>? = null

    /** 消费者协程 Job */
    private var consumerJob: Job? = null

    /**
     * 启动任务队列
     *
     * 从设置中读取队列配置，创建 Channel 并启动消费者协程
     */
    suspend fun start() {
        val settings = settingsRepository.getSettings()
        maxQueueLength = settings.globalQueueMaxLength

        taskChannel = Channel(capacity = maxQueueLength)
        startConsumer()
    }

    /**
     * 停止任务队列
     *
     * 取消消费者协程并关闭 Channel
     */
    fun stop() {
        consumerJob?.cancel()
        taskChannel?.close()
        taskChannel = null
    }

    /**
     * 将任务加入队列
     *
     * 处理流程：
     * 1. 创建执行记录（状态：REQUESTED）
     * 2. 尝试发送到 Channel
     * 3. 成功则更新状态为 QUEUED
     *
     * @param eventId 事件 ID
     * @param triggerId 触发器 ID
     * @param userId 用户 ID
     * @param requestParam 请求参数
     * @param requestTime 请求时间
     * @param callbackUrl 回调 URL
     * @return 入队结果
     */
    suspend fun enqueue(
        eventId: String,
        triggerId: Long,
        userId: String,
        requestParam: JsonObject,
        requestTime: String,
        callbackUrl: String
    ): EnqueueResult {
        // 创建执行记录
        executionLogRepository.createLog(
            eventId = eventId,
            triggerId = triggerId,
            userId = userId,
            status = ExecutionStatus.REQUESTED,
            requestTime = requestTime,
            requestParams = json.encodeToString(requestParam)
        )

        // 构建任务对象
        val task = TriggerTask(
            eventId = eventId,
            triggerId = triggerId,
            userId = userId,
            requestParam = requestParam,
            requestTime = requestTime,
            callbackUrl = callbackUrl
        )

        val channel = taskChannel ?: return EnqueueResult(false, -1)

        // 尝试非阻塞发送到 Channel
        val result = channel.trySend(task)
        return if (result.isSuccess) {
            // 更新待处理列表
            mutex.withLock {
                pendingTasks.add(task)
                _queueSize.value = pendingTasks.size
            }
            // 更新状态为已入队
            executionLogRepository.updateStatus(eventId, ExecutionStatus.QUEUED)
            EnqueueResult(true, pendingTasks.size - 1)
        } else {
            // 队列已满
            EnqueueResult(false, -1)
        }
    }

    /**
     * 启动消费者协程
     *
     * 从 Channel 中串行消费任务
     */
    private fun startConsumer() {
        consumerJob = scope.launch {
            val channel = taskChannel ?: return@launch

            // 串行处理每个任务
            for (task in channel) {
                processTask(task)
            }
        }
    }

    /**
     * 处理单个任务
     *
     * 处理流程：
     * 1. 从待处理列表移除
     * 2. 更新状态为 PRE_RESPONDED
     * 3. 写入 request.json 文件
     * 4. 等待影刀回调（最长 30 分钟）
     *
     * @param task 要处理的任务
     */
    private suspend fun processTask(task: TriggerTask) {
        // 从待处理列表移除
        mutex.withLock {
            pendingTasks.removeAll { it.eventId == task.eventId }
            _queueSize.value = pendingTasks.size
        }

        // 设置当前任务
        _currentTask.value = task

        // 更新状态：准备响应
        executionLogRepository.updateStatus(task.eventId, ExecutionStatus.PRE_RESPONDED)

        // 写入 request.json 文件（供影刀文件触发器读取）
        val writeResult = fileService.writeRequestFile(
            triggerId = task.triggerId,
            eventId = task.eventId,
            userId = task.userId,
            requestData = task.requestParam
        )

        if (writeResult.isFailure) {
            // 文件写入失败，标记任务失败并继续处理下一个
            executionLogRepository.updateStatus(task.eventId, ExecutionStatus.CALLBACK_FAILED)
            _currentTask.value = null
            return
        }

        // 更新状态：文件已写入
        executionLogRepository.updateStatus(task.eventId, ExecutionStatus.FILE_WRITTEN)

        // 等待影刀回调（onNotify 会完成这个 Deferred）
        currentTaskCompletion = CompletableDeferred()
        try {
            // 最长等待 30 分钟
            withTimeout(30 * 60 * 1000L) {
                currentTaskCompletion?.await()
            }
        } catch (e: TimeoutCancellationException) {
            // 超时，标记任务失败
            executionLogRepository.updateStatus(task.eventId, ExecutionStatus.CALLBACK_FAILED)
        } finally {
            currentTaskCompletion = null
            _currentTask.value = null
        }
    }

    /**
     * 处理影刀开始执行回调
     *
     * 当影刀检测到 request.json 并开始执行时调用
     * 更新执行记录的开始时间和状态
     *
     * @param eventId 事件 ID
     */
    suspend fun onTriggered(eventId: String) {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toString()
        executionLogRepository.updateShadowbotStart(eventId, now)
    }

    /**
     * 处理影刀执行完成回调
     *
     * 当影刀执行完成时调用，处理流程：
     * 1. 计算耗时统计
     * 2. 更新执行记录
     * 3. 删除 request.json 文件
     * 4. 回调外部服务
     * 5. 通知任务完成
     *
     * @param eventId 事件 ID
     * @param responseCode 响应码
     * @param responseMessage 响应消息
     * @param responseData 响应数据
     */
    suspend fun onNotify(
        eventId: String,
        responseCode: String,
        responseMessage: String,
        responseData: JsonObject?
    ) {
        val task = _currentTask.value
        // 验证事件 ID 匹配
        if (task == null || task.eventId != eventId) {
            return
        }

        val now = Clock.System.now()
        val nowStr = now.toLocalDateTime(TimeZone.currentSystemDefault()).toString()

        // 获取执行记录以计算耗时
        val log = executionLogRepository.getLogByEventId(eventId)

        // 计算总耗时（从请求到完成）
        val totalDuration = if (log != null) {
            now.toEpochMilliseconds() - parseTimeToEpochMillis(log.requestTime)
        } else null

        // 计算影刀执行耗时
        val shadowbotDuration = if (log?.shadowbotStartTime != null) {
            now.toEpochMilliseconds() - parseTimeToEpochMillis(log.shadowbotStartTime)
        } else null

        // 更新执行记录完成信息
        executionLogRepository.updateComplete(
            eventId = eventId,
            status = ExecutionStatus.COMPLETED,
            endTime = nowStr,
            totalDuration = totalDuration ?: 0,
            shadowbotDuration = shadowbotDuration ?: 0,
            responseParams = responseData?.let { json.encodeToString(it) },
            responseCode = responseCode,
            responseMessage = responseMessage
        )

        // 删除 request.json 文件
        fileService.deleteRequestFile(task.triggerId)

        // 构建外部服务回调请求
        val settings = settingsRepository.getSettings()
        val callbackRequest = ExternalCallbackRequest(
            fileWebHookName = settings.fileWebHookName,
            fileWebHookSecretKey = settings.fileWebHookSecretKey,
            triggerId = task.triggerId.toString(),
            eventId = eventId,
            responseCode = responseCode,
            responseMessage = responseMessage,
            responseData = responseData,
            requestTime = task.requestTime,
            shadowBotStartTime = log?.shadowbotStartTime,
            shadowBotEndTime = nowStr,
            totalDuration = totalDuration,
            shadowBotDuration = shadowbotDuration
        )

        // 发送回调到外部服务
        val callbackResult = callbackClient.sendCallback(
            callbackUrl = task.callbackUrl,
            fileWebHookName = settings.fileWebHookName,
            request = callbackRequest
        )

        // 根据回调结果更新最终状态
        if (callbackResult.isSuccess) {
            executionLogRepository.updateStatus(eventId, ExecutionStatus.CALLBACK_SUCCESS)
        } else {
            executionLogRepository.updateStatus(eventId, ExecutionStatus.CALLBACK_FAILED)
        }

        // 通知等待中的 processTask 继续执行下一个任务
        currentTaskCompletion?.complete(Unit)
    }

    /**
     * 解析时间字符串为毫秒时间戳
     *
     * @param timeStr 时间字符串
     * @return 毫秒时间戳
     */
    private fun parseTimeToEpochMillis(timeStr: String): Long {
        return try {
            Instant.parse(timeStr.replace(" ", "T") + "Z").toEpochMilliseconds()
        } catch (e: Exception) {
            Clock.System.now().toEpochMilliseconds()
        }
    }
}
