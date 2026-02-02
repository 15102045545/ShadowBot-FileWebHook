/**
 * 用户权限数据模型
 *
 * 本文件定义了用户触发器权限相关的数据类
 */

package data.model

import kotlinx.serialization.Serializable

/**
 * 用户权限实体类
 *
 * 代表用户与触发器之间的权限关联记录
 *
 * @property id 权限记录主键
 * @property userId 用户 ID
 * @property triggerId 触发器 ID
 * @property createdAt 创建时间（ISO 8601 格式）
 */
@Serializable
data class UserPermission(
    val id: Long,
    val userId: String,
    val triggerId: Long,
    val createdAt: String
)
