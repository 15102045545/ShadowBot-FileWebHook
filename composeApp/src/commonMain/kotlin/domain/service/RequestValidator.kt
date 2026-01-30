/**
 * 请求参数安全校验器
 *
 * 本文件提供 JSON 请求参数的安全性验证
 * 防止恶意请求导致的安全问题（如路径遍历、XSS 等）
 */

package domain.service

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

/**
 * 请求参数验证器对象
 *
 * 提供静态方法验证 JSON 请求参数的合法性
 * 检查项包括：
 * - 请求体大小限制
 * - 危险字符模式检测
 * - JSON 嵌套深度限制
 * - 对象字段数量限制
 */
object RequestValidator {
    /** 请求体最大字节数：1MB */
    private const val MAX_SIZE_BYTES = 1024 * 1024

    /** JSON 最大嵌套深度：10 层 */
    private const val MAX_NESTING_DEPTH = 10

    /** 单个对象最大字段数：100 个 */
    private const val MAX_FIELDS_PER_OBJECT = 100

    /**
     * 危险字符模式列表
     *
     * 包含可能导致安全问题的常见模式：
     * - 路径遍历：../ 和 ..\
     * - XSS 攻击：<script、javascript:、vbscript:
     * - 数据注入：data:
     */
    private val DANGEROUS_PATTERNS = listOf(
        "../",          // Unix 路径遍历
        "..\\",         // Windows 路径遍历
        "<script",      // HTML 脚本标签
        "javascript:",  // JavaScript 协议
        "data:",        // Data URI
        "vbscript:"     // VBScript 协议
    )

    /**
     * 验证结果数据类
     *
     * @property isValid 是否通过验证
     * @property errorMessage 错误信息（验证失败时）
     */
    data class ValidationResult(
        val isValid: Boolean,
        val errorMessage: String? = null
    )

    /**
     * 验证 JSON 对象
     *
     * 执行多项安全检查：
     * 1. 检查请求体大小
     * 2. 检查危险字符模式
     * 3. 检查嵌套深度和字段数量
     *
     * @param json 要验证的 JSON 对象
     * @return 验证结果
     */
    fun validate(json: JsonObject): ValidationResult {
        // 检查 1: 请求体大小
        val jsonString = json.toString()
        if (jsonString.toByteArray().size > MAX_SIZE_BYTES) {
            return ValidationResult(false, "请求体超过1MB限制")
        }

        // 检查 2: 危险字符模式
        if (containsDangerousPatterns(jsonString)) {
            return ValidationResult(false, "包含非法字符")
        }

        // 检查 3: 嵌套深度和字段数量
        val depthResult = checkDepthAndFields(json, 0)
        if (!depthResult.isValid) {
            return depthResult
        }

        return ValidationResult(true)
    }

    /**
     * 检查是否包含危险字符模式
     *
     * 大小写不敏感匹配
     *
     * @param content 要检查的内容
     * @return true 表示包含危险模式
     */
    private fun containsDangerousPatterns(content: String): Boolean {
        val lowerContent = content.lowercase()
        return DANGEROUS_PATTERNS.any { pattern ->
            lowerContent.contains(pattern.lowercase())
        }
    }

    /**
     * 递归检查 JSON 嵌套深度和字段数量
     *
     * @param element 当前 JSON 元素
     * @param currentDepth 当前深度
     * @return 验证结果
     */
    private fun checkDepthAndFields(element: JsonElement, currentDepth: Int): ValidationResult {
        // 深度检查
        if (currentDepth > MAX_NESTING_DEPTH) {
            return ValidationResult(false, "JSON嵌套深度超过${MAX_NESTING_DEPTH}层限制")
        }

        when (element) {
            is JsonObject -> {
                // 字段数量检查
                if (element.size > MAX_FIELDS_PER_OBJECT) {
                    return ValidationResult(false, "单个对象字段数超过${MAX_FIELDS_PER_OBJECT}个限制")
                }
                // 递归检查每个字段值
                for ((_, value) in element) {
                    val result = checkDepthAndFields(value, currentDepth + 1)
                    if (!result.isValid) return result
                }
            }
            is JsonArray -> {
                // 递归检查数组中的每个元素
                for (item in element) {
                    val result = checkDepthAndFields(item, currentDepth + 1)
                    if (!result.isValid) return result
                }
            }
            is JsonPrimitive -> {
                // 基本类型无需检查
            }
        }

        return ValidationResult(true)
    }
}
