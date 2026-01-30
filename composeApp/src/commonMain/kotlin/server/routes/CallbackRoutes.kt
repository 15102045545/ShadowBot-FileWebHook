/**
 * 影刀回调路由
 *
 * 本文件定义影刀 RPA 调用的回调接口：
 * - POST /trigged：影刀开始执行回调
 * - POST /notify：影刀执行完成回调
 */

package server.routes

import data.model.CallbackResponse
import data.model.NotifyCallbackRequest
import data.model.TriggeredCallbackRequest
import domain.queue.TaskQueue
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * 注册影刀回调路由
 *
 * 处理影刀 RPA 的两个回调接口，用于通知任务执行状态
 *
 * @param taskQueue 任务队列，用于更新任务状态
 */
fun Route.callbackRoutes(taskQueue: TaskQueue) {

    /**
     * POST /trigged
     *
     * 影刀开始执行回调接口
     *
     * 当影刀检测到 request.json 文件并开始执行机器人时，
     * 调用此接口通知 FileWebHook 任务已开始执行
     *
     * 请求体：TriggeredCallbackRequest
     * - triggerId: 触发器 ID
     * - eventId: 事件 ID
     *
     * 响应：CallbackResponse.OK
     */
    post("/trigged") {
        // 解析请求体
        val request = try {
            call.receive<TriggeredCallbackRequest>()
        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.BadRequest,
                CallbackResponse.error("Invalid request body")
            )
            return@post
        }

        // 通知任务队列：任务已开始执行
        taskQueue.onTriggered(request.eventId)

        call.respond(HttpStatusCode.OK, CallbackResponse.OK)
    }

    /**
     * POST /notify
     *
     * 影刀执行完成回调接口
     *
     * 当影刀机器人执行完成后，调用此接口通知 FileWebHook
     * 执行结果，包括响应码、响应消息和业务返回数据
     *
     * 请求体：NotifyCallbackRequest
     * - triggerId: 触发器 ID
     * - eventId: 事件 ID
     * - responseCode: 响应码（200 成功，500 失败）
     * - responseMessage: 响应消息
     * - responseData: 业务返回数据（可选）
     *
     * 响应：CallbackResponse.RECEIVED
     */
    post("/notify") {
        // 解析请求体
        val request = try {
            call.receive<NotifyCallbackRequest>()
        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.BadRequest,
                CallbackResponse.error("Invalid request body")
            )
            return@post
        }

        // 通知任务队列：任务已完成
        taskQueue.onNotify(
            eventId = request.eventId,
            responseCode = request.responseCode,
            responseMessage = request.responseMessage,
            responseData = request.responseData
        )

        call.respond(HttpStatusCode.OK, CallbackResponse.RECEIVED)
    }
}
