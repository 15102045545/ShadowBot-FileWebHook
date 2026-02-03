/**
 * 外部服务回调客户端
 *
 * 本文件提供向外部服务发送执行结果回调的 HTTP 客户端
 * 当影刀执行完成后，将结果通知给原始调用方
 */

package client

import data.model.ExternalCallbackRequest
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

/**
 * 回调客户端类
 *
 * 使用 Ktor CIO HTTP 客户端向外部服务发送回调请求
 * 回调 URL 格式：{callbackUrl}/{fileWebHookName}/filewebhook/notify
 */
class CallbackClient {
    /**
     * Ktor HTTP 客户端实例
     *
     * 配置 JSON 序列化插件，与服务端保持一致的 JSON 处理策略
     */
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true      // 格式化输出
                isLenient = true        // 宽松解析
                ignoreUnknownKeys = true // 忽略未知字段
            })
        }
    }

    /**
     * 发送回调请求
     *
     * 向外部服务发送执行结果通知
     * 使用 Result 包装返回值，便于调用方处理成功/失败情况
     *
     * @param callbackUrl 外部服务回调基础 URL（用户配置）
     * @param fileWebHookName 本机 FileWebHook 名称标识
     * @param request 回调请求体，包含执行结果详情
     * @param customHeaders 自定义请求头（用户配置的回调请求头）
     * @return Result<Unit> 成功返回 Unit，失败返回异常
     */
    suspend fun sendCallback(
        callbackUrl: String,
        fileWebHookName: String,
        request: ExternalCallbackRequest,
        customHeaders: Map<String, String> = emptyMap()
    ): Result<Unit> {
        return try {
            // 构建完整的回调 URL
            val fullUrl = buildCallbackUrl(callbackUrl, fileWebHookName)

            // 发送 POST 请求
            val response = client.post(fullUrl) {
                contentType(ContentType.Application.Json)
                setBody(request)
                // 添加用户自定义的请求头
                customHeaders.forEach { (key, value) ->
                    header(key, value)
                }
            }

            // 检查响应状态码
            if (response.status.isSuccess()) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Callback failed with status: ${response.status}"))
            }
        } catch (e: Exception) {
            // 网络异常等错误
            Result.failure(e)
        }
    }

    /**
     * 构建回调 URL
     *
     * URL 格式：{baseUrl}/{fileWebHookName}/filewebhook/notify
     * 例如：http://example.com/my-webhook/filewebhook/notify
     *
     * @param baseUrl 基础 URL（去除末尾斜杠）
     * @param fileWebHookName FileWebHook 名称标识
     * @return 完整的回调 URL
     */
    private fun buildCallbackUrl(baseUrl: String, fileWebHookName: String): String {
        val normalizedBase = baseUrl.trimEnd('/')
        return "$normalizedBase/$fileWebHookName/filewebhook/notify"
    }

    /**
     * 关闭客户端
     *
     * 释放 HTTP 客户端资源，应用退出时调用
     */
    fun close() {
        client.close()
    }
}
