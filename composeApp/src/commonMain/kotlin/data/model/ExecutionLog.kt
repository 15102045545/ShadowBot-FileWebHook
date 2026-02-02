/**
 * 执行记录数据模型
 *
 * 本文件定义了与执行记录相关的数据类和状态枚举
 */

package data.model

import kotlinx.serialization.Serializable

/**
 * 执行记录实体类
 *
 * 记录一次完整的触发执行过程，包含从请求到回调的全部信息
 *
 * @property eventId 执行事件唯一标识（格式：{triggerId}-{时间戳毫秒}）
 * @property triggerId 触发器 ID
 * @property userId 调用用户 ID
 * @property status 当前执行状态
 * @property requestTime 外部服务请求到达时间
 * @property shadowbotStartTime 影刀开始执行时间（收到 /triggered 回调时记录）
 * @property shadowbotEndTime 影刀执行完毕时间（收到 /notify 回调时记录）
 * @property totalDuration 总耗时（毫秒）：从请求到回调完成
 * @property shadowbotDuration 影刀执行耗时（毫秒）：从开始到完成
 * @property requestParams 入参参数（JSON 字符串）
 * @property responseParams 出参参数（JSON 字符串）
 * @property responseCode 影刀响应码
 * @property responseMessage 影刀响应消息
 * @property createdAt 记录创建时间
 * @property updatedAt 记录更新时间
 */
@Serializable
data class ExecutionLog(
    val eventId: String,
    val triggerId: Long,
    val userId: String,
    val status: ExecutionStatus,
    val requestTime: String,
    val shadowbotStartTime: String? = null,
    val shadowbotEndTime: String? = null,
    val totalDuration: Long? = null,
    val shadowbotDuration: Long? = null,
    val requestParams: String,
    val responseParams: String? = null,
    val responseCode: String? = null,
    val responseMessage: String? = null,
    val createdAt: String,
    val updatedAt: String
)

/**
 * 执行状态枚举
 *
 * 定义执行记录的状态流转：
 * REQUESTED -> QUEUED -> PRE_RESPONDED -> FILE_WRITTEN -> EXECUTING -> COMPLETED -> CALLBACK_SUCCESS/CALLBACK_FAILED
 *
 * @property displayName 状态的中文显示名称
 */
@Serializable
enum class ExecutionStatus(val displayName: String) {
    /** 用户已请求 - 收到外部服务请求 */
    REQUESTED("用户已请求"),

    /** 已收到请求放入队列 - 请求已入队等待处理 */
    QUEUED("已收到请求放入队列"),

    /** 已写入文件待影刀执行 - request.json 文件已写入 */
    FILE_WRITTEN("已写入文件待影刀执行"),

    /** 影刀执行中 - 收到影刀的 /triggered 回调 */
    EXECUTING("影刀执行中"),

    /** 影刀执行完成 - 收到影刀的 /notify 回调 */
    COMPLETED("影刀执行完成"),

    /** 回调用户完成 - 成功通知外部服务 */
    CALLBACK_SUCCESS("回调用户完成"),

    /** 回调外部服务失败 - 通知外部服务失败 */
    CALLBACK_FAILED("回调外部服务失败")
}
