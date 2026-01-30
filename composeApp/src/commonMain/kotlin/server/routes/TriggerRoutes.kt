/**
 * 触发执行路由
 *
 * 本文件定义外部服务调用的触发执行接口
 * 接口路径：POST /trigger/execute
 */

package server.routes

import data.model.TriggerExecuteRequest
import data.model.TriggerExecuteResponse
import data.repository.PermissionRepository
import data.repository.TriggerRepository
import data.repository.UserRepository
import domain.queue.TaskQueue
import domain.service.RequestValidator
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * 注册触发执行路由
 *
 * 处理外部服务的触发请求，执行以下验证流程：
 * 1. 解析请求体
 * 2. 验证请求参数安全性
 * 3. 验证用户身份（userId + secretKey）
 * 4. 验证触发器存在性
 * 5. 验证用户权限
 * 6. 将任务加入队列
 *
 * @param userRepository 用户仓库
 * @param triggerRepository 触发器仓库
 * @param permissionRepository 权限仓库
 * @param taskQueue 任务队列
 */
fun Route.triggerRoutes(
    userRepository: UserRepository,
    triggerRepository: TriggerRepository,
    permissionRepository: PermissionRepository,
    taskQueue: TaskQueue
) {
    /**
     * POST /trigger/execute
     *
     * 外部服务触发执行接口
     *
     * 请求体：TriggerExecuteRequest
     * 响应体：TriggerExecuteResponse
     *
     * 响应码说明：
     * - C_0: 成功，任务已入队
     * - C_1: 身份验证失败
     * - C_2: 无权限
     * - C_3: 触发器不存在
     * - C_4: 非法业务参数
     * - C_5: 队列已满
     */
    post("/trigger/execute") {
        // 步骤 1: 解析请求体
        val request = try {
            call.receive<TriggerExecuteRequest>()
        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.BadRequest,
                TriggerExecuteResponse.invalidParam(null, null)
            )
            return@post
        }

        val triggerId = request.triggerId
        val requestTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toString()

        // 步骤 2: 验证请求参数安全性（防止恶意 JSON）
        val validationResult = RequestValidator.validate(request.requestParam)
        if (!validationResult.isValid) {
            call.respond(
                HttpStatusCode.BadRequest,
                TriggerExecuteResponse.invalidParam(null, triggerId)
            )
            return@post
        }

        // 步骤 3: 验证用户身份
        val user = userRepository.validateUser(request.userId, request.secretKey)
        if (user == null) {
            call.respond(
                HttpStatusCode.Unauthorized,
                TriggerExecuteResponse.authFailed(null, triggerId)
            )
            return@post
        }

        // 步骤 4: 验证触发器存在性
        val triggerIdLong = triggerId.toLongOrNull()
        if (triggerIdLong == null) {
            call.respond(
                HttpStatusCode.BadRequest,
                TriggerExecuteResponse.triggerNotFound(null, triggerId)
            )
            return@post
        }

        val trigger = triggerRepository.getTriggerById(triggerIdLong)
        if (trigger == null) {
            call.respond(
                HttpStatusCode.NotFound,
                TriggerExecuteResponse.triggerNotFound(null, triggerId)
            )
            return@post
        }

        // 步骤 5: 验证用户权限
        val hasPermission = permissionRepository.hasPermission(request.userId, triggerIdLong)
        if (!hasPermission) {
            call.respond(
                HttpStatusCode.Forbidden,
                TriggerExecuteResponse.noPermission(null, triggerId)
            )
            return@post
        }

        // 步骤 6: 生成事件 ID 并将任务加入队列
        // eventId 格式：{triggerId}-{timestamp}
        val timestamp = Clock.System.now().toEpochMilliseconds()
        val eventId = "$triggerId-$timestamp"

        val enqueueResult = taskQueue.enqueue(
            eventId = eventId,
            triggerId = triggerIdLong,
            userId = request.userId,
            requestParam = request.requestParam,
            requestTime = requestTime,
            callbackUrl = user.callbackUrl
        )

        // 根据入队结果返回响应
        if (enqueueResult.success) {
            call.respond(
                HttpStatusCode.OK,
                TriggerExecuteResponse.success(eventId, triggerId, enqueueResult.queuePosition)
            )
        } else {
            call.respond(
                HttpStatusCode.ServiceUnavailable,
                TriggerExecuteResponse.queueFull(eventId, triggerId)
            )
        }
    }
}
