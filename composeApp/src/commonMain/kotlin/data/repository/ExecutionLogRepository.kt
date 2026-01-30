/**
 * 执行记录数据仓库
 *
 * 本文件提供执行记录的 CRUD 操作
 * 记录每次触发器执行的完整生命周期（排队→执行中→完成/失败）
 */

package data.repository

import com.filewebhook.db.FileWebHookDatabase
import data.model.ExecutionLog
import data.model.ExecutionStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * 执行记录仓库类
 *
 * 封装所有执行记录相关的数据库操作，支持：
 * - 按多种条件查询执行记录
 * - 创建新执行记录
 * - 更新执行状态（状态流转、时间戳、响应数据）
 *
 * @property database SQLDelight 生成的数据库实例
 */
class ExecutionLogRepository(private val database: FileWebHookDatabase) {

    /** 获取查询接口 */
    private val queries get() = database.fileWebHookQueries

    /**
     * 获取所有执行记录
     *
     * @param limit 返回记录数上限，默认 100 条
     * @return 按创建时间倒序排列的执行记录列表
     */
    suspend fun getAllLogs(limit: Long = 100): List<ExecutionLog> = withContext(Dispatchers.IO) {
        queries.selectAllExecutionLogs(limit).executeAsList().map { it.toExecutionLog() }
    }

    /**
     * 根据触发器 ID 获取执行记录
     *
     * @param triggerId 触发器 ID
     * @param limit 返回记录数上限，默认 100 条
     * @return 该触发器的执行记录列表
     */
    suspend fun getLogsByTriggerId(triggerId: Long, limit: Long = 100): List<ExecutionLog> = withContext(Dispatchers.IO) {
        queries.selectExecutionLogsByTriggerId(triggerId, limit).executeAsList().map { it.toExecutionLog() }
    }

    /**
     * 根据状态获取执行记录
     *
     * @param status 执行状态枚举
     * @param limit 返回记录数上限，默认 100 条
     * @return 指定状态的执行记录列表
     */
    suspend fun getLogsByStatus(status: ExecutionStatus, limit: Long = 100): List<ExecutionLog> = withContext(Dispatchers.IO) {
        queries.selectExecutionLogsByStatus(status.name, limit).executeAsList().map { it.toExecutionLog() }
    }

    /**
     * 根据事件 ID 获取执行记录
     *
     * @param eventId 事件唯一标识
     * @return 执行记录对象，不存在返回 null
     */
    suspend fun getLogByEventId(eventId: String): ExecutionLog? = withContext(Dispatchers.IO) {
        queries.selectExecutionLogByEventId(eventId).executeAsOneOrNull()?.toExecutionLog()
    }

    /**
     * 创建新的执行记录
     *
     * 当外部服务触发执行时调用，初始状态通常为 QUEUED
     *
     * @param eventId 事件唯一标识（UUID）
     * @param triggerId 触发器 ID
     * @param userId 调用用户 ID
     * @param status 初始执行状态
     * @param requestTime 请求时间字符串
     * @param requestParams 请求参数（JSON 字符串）
     * @return 创建成功的执行记录对象
     */
    suspend fun createLog(
        eventId: String,
        triggerId: Long,
        userId: String,
        status: ExecutionStatus,
        requestTime: String,
        requestParams: String
    ): ExecutionLog = withContext(Dispatchers.IO) {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toString()

        // 插入数据库记录
        queries.insertExecutionLog(
            eventId = eventId,
            triggerId = triggerId,
            userId = userId,
            status = status.name,
            requestTime = requestTime,
            requestParams = requestParams,
            createdAt = now,
            updatedAt = now
        )

        // 返回创建的执行记录对象
        ExecutionLog(
            eventId = eventId,
            triggerId = triggerId,
            userId = userId,
            status = status,
            requestTime = requestTime,
            requestParams = requestParams,
            createdAt = now,
            updatedAt = now
        )
    }

    /**
     * 更新执行状态
     *
     * 简单的状态更新，仅修改状态字段和更新时间
     *
     * @param eventId 事件 ID
     * @param status 新状态
     */
    suspend fun updateStatus(eventId: String, status: ExecutionStatus) = withContext(Dispatchers.IO) {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toString()
        queries.updateExecutionLogStatus(status.name, now, eventId)
    }

    /**
     * 更新影刀开始执行时间
     *
     * 当收到影刀 /trigged 回调时调用
     * 状态从 QUEUED/WAITING 变为 EXECUTING
     *
     * @param eventId 事件 ID
     * @param startTime 影刀开始执行时间字符串
     */
    suspend fun updateShadowbotStart(eventId: String, startTime: String) = withContext(Dispatchers.IO) {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toString()
        queries.updateExecutionLogShadowbotStart(
            status = ExecutionStatus.EXECUTING.name,
            shadowbotStartTime = startTime,
            updatedAt = now,
            eventId = eventId
        )
    }

    /**
     * 更新执行完成信息
     *
     * 当收到影刀 /notify 回调时调用
     * 记录完成时间、耗时统计、响应数据等完整信息
     *
     * @param eventId 事件 ID
     * @param status 最终状态（SUCCESS 或 FAILED）
     * @param endTime 影刀执行完成时间
     * @param totalDuration 总耗时（从请求到完成，毫秒）
     * @param shadowbotDuration 影刀执行耗时（毫秒）
     * @param responseParams 响应参数（JSON 字符串）
     * @param responseCode 响应码
     * @param responseMessage 响应消息
     */
    suspend fun updateComplete(
        eventId: String,
        status: ExecutionStatus,
        endTime: String,
        totalDuration: Long,
        shadowbotDuration: Long,
        responseParams: String?,
        responseCode: String,
        responseMessage: String
    ) = withContext(Dispatchers.IO) {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toString()
        queries.updateExecutionLogComplete(
            status = status.name,
            shadowbotEndTime = endTime,
            totalDuration = totalDuration,
            shadowbotDuration = shadowbotDuration,
            responseParams = responseParams,
            responseCode = responseCode,
            responseMessage = responseMessage,
            updatedAt = now,
            eventId = eventId
        )
    }

    /**
     * 将数据库实体转换为领域模型
     *
     * 注意：status 字段需要从字符串转换为枚举
     */
    private fun com.filewebhook.db.Execution_logs.toExecutionLog() = ExecutionLog(
        eventId = eventId,
        triggerId = triggerId,
        userId = userId,
        status = ExecutionStatus.valueOf(status),
        requestTime = requestTime,
        shadowbotStartTime = shadowbotStartTime,
        shadowbotEndTime = shadowbotEndTime,
        totalDuration = totalDuration,
        shadowbotDuration = shadowbotDuration,
        requestParams = requestParams,
        responseParams = responseParams,
        responseCode = responseCode,
        responseMessage = responseMessage,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
