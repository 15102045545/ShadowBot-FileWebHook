/**
 * 用户数据模型
 *
 * 本文件定义了与用户管理相关的数据类
 */

package data.model

import kotlinx.serialization.Serializable

/**
 * 用户实体类
 *
 * 代表一个可以调用 FileWebHook 的外部服务用户
 *
 * @property userId 用户唯一标识（UUID 格式）
 * @property name 用户名称（显示用）
 * @property remark 备注信息（可选）
 * @property secretKey 密钥（UUID 格式），用于 API 身份验证
 * @property callbackUrl 回调地址，影刀执行完成后通知此地址
 * @property createdAt 创建时间（ISO 8601 格式）
 * @property updatedAt 更新时间（ISO 8601 格式）
 */
@Serializable
data class User(
    val userId: String,
    val name: String,
    val remark: String? = null,
    val secretKey: String,
    val callbackUrl: String,
    val createdAt: String,
    val updatedAt: String
)

/**
 * 创建用户请求
 *
 * 用于新建用户时的表单数据
 *
 * @property name 用户名称（必填）
 * @property remark 备注信息（可选）
 * @property callbackUrl 回调地址（必填）
 */
@Serializable
data class CreateUserRequest(
    val name: String,
    val remark: String? = null,
    val callbackUrl: String
)

/**
 * 更新用户请求
 *
 * 用于编辑用户信息时的表单数据（不包含密钥）
 *
 * @property name 用户名称
 * @property remark 备注信息
 * @property callbackUrl 回调地址
 */
@Serializable
data class UpdateUserRequest(
    val name: String,
    val remark: String? = null,
    val callbackUrl: String
)
