/**
 * 触发器数据模型
 *
 * 本文件定义了与触发器管理相关的数据类
 */

package data.model

import kotlinx.serialization.Serializable

/**
 * 触发器实体类
 *
 * 代表一个 FileWebHook 触发器，对应影刀 RPA 的一个应用
 *
 * @property id 触发器 ID（自增数字，同时作为 triggerId）
 * @property name 触发器名称
 * @property description 触发器描述（可选）
 * @property remark 备注信息（可选）
 * @property folderPath 文件夹完整路径，影刀的文件触发器需要监听此路径
 * @property createdAt 创建时间（ISO 8601 格式）
 */
@Serializable
data class Trigger(
    val id: Long,
    val name: String,
    val description: String? = null,
    val remark: String? = null,
    val folderPath: String,
    val createdAt: String
)

/**
 * 创建触发器请求
 *
 * 用于新建触发器时的表单数据
 *
 * @property name 触发器名称（必填）
 * @property description 触发器描述（可选）
 * @property remark 备注信息（可选）
 */
@Serializable
data class CreateTriggerRequest(
    val name: String,
    val description: String? = null,
    val remark: String? = null
)
