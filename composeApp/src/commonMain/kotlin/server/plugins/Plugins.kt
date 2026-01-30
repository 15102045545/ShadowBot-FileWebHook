/**
 * Ktor 服务器插件配置
 *
 * 本文件配置 Ktor 服务器的核心插件：
 * - ContentNegotiation：JSON 序列化/反序列化
 * - CORS：跨域资源共享
 * - StatusPages：全局异常处理
 */

package server.plugins

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.http.*
import io.ktor.server.response.*
import kotlinx.serialization.json.Json

/**
 * 配置 Ktor 应用插件
 *
 * 为 Ktor Application 安装和配置必要的插件
 */
fun Application.configurePlugins() {
    /**
     * 内容协商插件
     *
     * 配置 JSON 序列化器：
     * - prettyPrint: 格式化输出 JSON（便于调试）
     * - isLenient: 宽松解析模式（允许非标准 JSON）
     * - ignoreUnknownKeys: 忽略未知字段（向前兼容）
     */
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }

    /**
     * 跨域资源共享插件
     *
     * 配置 CORS 策略：
     * - anyHost: 允许任意来源访问（开发环境友好）
     * - 允许 Content-Type 头（JSON 请求必需）
     * - 允许 POST/GET/OPTIONS 方法
     */
    install(CORS) {
        anyHost()
        allowHeader(HttpHeaders.ContentType)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Options)
    }

    /**
     * 状态页面插件
     *
     * 全局异常处理：
     * - 捕获所有未处理异常
     * - 返回 500 状态码和错误信息
     */
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respond(
                HttpStatusCode.InternalServerError,
                mapOf("error" to (cause.message ?: "Unknown error"))
            )
        }
    }
}
