/**
 * API 数据模型
 *
 * 本文件定义了所有 HTTP API 的请求和响应数据类
 * 包括：
 * - 外部服务触发执行 API
 * - 影刀回调 API
 * - 回调外部服务
 * - request.json 文件格式
 */

package data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

// ============================================
// 外部服务调用接口
// ============================================

/**
 * 触发执行请求
 *
 * 外部服务调用 POST /trigger/execute 时的请求体
 *
 * @property userId 用户唯一标识
 * @property secretKey 用户密钥，用于身份验证
 * @property triggerId 要触发的触发器 ID
 * @property requestParam 业务请求参数（JSON 对象）
 */
@Serializable
data class TriggerExecuteRequest(
    val userId: String,
    val secretKey: String,
    val triggerId: String,
    val requestParam: JsonObject = JsonObject(emptyMap())
)

/**
 * 触发执行响应
 *
 * POST /trigger/execute 的响应体
 *
 * @property code 响应码（C_0 成功，C_1-C_5 各类错误）
 * @property message 响应消息
 * @property eventId 执行事件 ID（成功时返回）
 * @property triggerId 触发器 ID
 * @property queuePosition 队列位置（-1 表示入队失败）
 */
@Serializable
data class TriggerExecuteResponse(
    val code: String,
    val message: String,
    val eventId: String? = null,
    val triggerId: String? = null,
    val queuePosition: Int? = null
) {
    companion object {
        /**
         * 成功响应
         * @param eventId 生成的事件 ID
         * @param triggerId 触发器 ID
         * @param queuePosition 在队列中的位置
         */
        fun success(eventId: String, triggerId: String, queuePosition: Int) = TriggerExecuteResponse(
            code = "C_0",
            message = "请求已接受",
            eventId = eventId,
            triggerId = triggerId,
            queuePosition = queuePosition
        )

        /** 身份验证失败（用户不存在或密钥错误） */
        fun authFailed(eventId: String?, triggerId: String?) = TriggerExecuteResponse(
            code = "C_1",
            message = "身份验证失败",
            eventId = eventId,
            triggerId = triggerId
        )

        /** 无权限（用户没有调用此触发器的权限） */
        fun noPermission(eventId: String?, triggerId: String?) = TriggerExecuteResponse(
            code = "C_2",
            message = "无权限",
            eventId = eventId,
            triggerId = triggerId
        )

        /** 触发器不存在 */
        fun triggerNotFound(eventId: String?, triggerId: String?) = TriggerExecuteResponse(
            code = "C_3",
            message = "触发器不存在",
            eventId = eventId,
            triggerId = triggerId
        )

        /** 非法业务参数（安全校验失败） */
        fun invalidParam(eventId: String?, triggerId: String?) = TriggerExecuteResponse(
            code = "C_4",
            message = "非法业务参数",
            eventId = eventId,
            triggerId = triggerId
        )

        /** 队列已满 */
        fun queueFull(eventId: String?, triggerId: String?) = TriggerExecuteResponse(
            code = "C_5",
            message = "队列已满",
            eventId = eventId,
            triggerId = triggerId,
            queuePosition = -1
        )
    }
}

// ============================================
// 影刀回调接口
// ============================================

/**
 * 影刀开始执行回调请求
 *
 * 影刀调用 POST /triggered 时的请求体
 * 表示影刀已检测到文件触发器并开始执行
 *
 * @property triggerId 触发器 ID
 * @property eventId 执行事件 ID
 */
@Serializable
data class TriggeredCallbackRequest(
    val triggerId: String,
    val eventId: String
)

/**
 * 影刀执行完成回调请求
 *
 * 影刀调用 POST /notify 时的请求体
 * 表示影刀已完成执行并返回结果
 *
 * @property triggerId 触发器 ID
 * @property eventId 执行事件 ID
 * @property responseCode 响应码（200 成功，500 失败）
 * @property responseMessage 响应消息
 * @property responseData 业务返回数据（JSON 对象）
 */
@Serializable
data class NotifyCallbackRequest(
    val triggerId: String,
    val eventId: String,
    val responseCode: String,
    val responseMessage: String,
    val responseData: JsonObject? = null
)

/**
 * 通用回调响应
 *
 * /triggered 和 /notify 的响应体
 *
 * @property code 响应码
 * @property message 响应消息
 */
@Serializable
data class CallbackResponse(
    val code: Int,
    val message: String
) {
    companion object {
        /** /triggered 成功响应 */
        val OK = CallbackResponse(200, "已知晓")

        /** /notify 成功响应 */
        val RECEIVED = CallbackResponse(200, "已接收执行结果")

        /** 错误响应 */
        fun error(message: String) = CallbackResponse(500, message)
    }
}

// ============================================
// 回调外部服务
// ============================================

/**
 * 回调外部服务的请求体
 *
 * FileWebHook 调用外部服务 POST http://{callbackUrl}/{fileWebHookName}/filewebhook/notify 时的请求体
 *
 * @property fileWebHookName 本机 FileWebHook 名称标识
 * @property fileWebHookSecretKey 本机密钥，用于验证身份
 * @property triggerId 触发器 ID
 * @property eventId 执行事件 ID
 * @property responseCode 影刀响应码
 * @property responseMessage 影刀响应消息
 * @property responseData 业务返回数据
 * @property requestTime 外部服务请求时间
 * @property shadowBotStartTime 影刀开始执行时间
 * @property shadowBotEndTime 影刀执行完成时间
 * @property totalDuration 总耗时（毫秒）
 * @property shadowBotDuration 影刀执行耗时（毫秒）
 */
@Serializable
data class ExternalCallbackRequest(
    val fileWebHookName: String,
    val fileWebHookSecretKey: String,
    val triggerId: String,
    val eventId: String,
    val responseCode: String,
    val responseMessage: String,
    val responseData: JsonObject? = null,
    val requestTime: String,
    val shadowBotStartTime: String?,
    val shadowBotEndTime: String?,
    val totalDuration: Long?,
    val shadowBotDuration: Long?
)

// ============================================
// 文件格式
// ============================================

/**
 * request.json 文件内容
 *
 * 写入 triggerFiles/{triggerId}/request.json 的数据结构
 * 影刀文件触发器读取此文件获取执行参数
 *
 * @property eventId 执行事件 ID
 * @property triggerId 触发器 ID
 * @property userId 调用用户 ID
 * @property requestData 业务请求参数
 */
@Serializable
data class RequestFileContent(
    val eventId: String,
    val triggerId: String,
    val userId: String,
    val requestData: JsonObject
)
