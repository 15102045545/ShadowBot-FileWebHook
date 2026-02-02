/**
 * 系统设置数据模型
 *
 * 本文件定义了应用程序的配置设置数据类
 */

package data.model

import kotlinx.serialization.Serializable

/**
 * 应用设置实体类
 *
 * 包含 FileWebHook 的所有可配置项
 *
 * @property triggerFilesPath 触发器文件存放路径，影刀文件触发器监听此目录下的子文件夹
 * @property globalQueueMaxLength 全局队列最大长度，超过此数量的请求将被拒绝
 * @property fileWebHookName 本机 FileWebHook 名称标识，用于回调外部服务时的身份识别
 * @property fileWebHookSecretKey 本机密钥，回调外部服务时携带，用于验证身份
 * @property httpPort HTTP 服务监听端口，默认 8089
 */
@Serializable
data class AppSettings(
    val triggerFilesPath: String,
    val globalQueueMaxLength: Int,
    val fileWebHookName: String,
    val fileWebHookSecretKey: String,
    val httpPort: Int = 8089
) {
    companion object {
        // ============================================
        // 设置键名常量
        // ============================================

        /** 触发器文件路径 */
        const val KEY_TRIGGER_FILES_PATH = "triggerFilesPath"

        /** 队列最大长度 */
        const val KEY_GLOBAL_QUEUE_MAX_LENGTH = "globalQueueMaxLength"

        /** FileWebHook 名称 */
        const val KEY_FILE_WEBHOOK_NAME = "fileWebHookName"

        /** FileWebHook 密钥 */
        const val KEY_FILE_WEBHOOK_SECRET_KEY = "fileWebHookSecretKey"

        /** HTTP 端口 */
        const val KEY_HTTP_PORT = "httpPort"

        /**
         * 默认设置
         *
         * 应用首次启动时使用的默认配置
         */
        val DEFAULT = AppSettings(
            triggerFilesPath = "",           // 需要用户配置
            globalQueueMaxLength = 10,       // 默认队列容量 50
            fileWebHookName = "xiaokeer",    // 默认名称
            fileWebHookSecretKey = "123456", // 默认密钥
            httpPort = 58700                  // 默认端口
        )
    }
}
