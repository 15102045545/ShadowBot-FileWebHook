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
 * 注意：触发器文件路径固定为用户目录/.filewebhook/triggerFiles，不可配置
 *
 * @property globalQueueMaxLength 全局队列最大长度，超过此数量的请求将被拒绝
 * @property fileWebHookName 本机 FileWebHook 名称标识，用于回调外部服务时的身份识别
 * @property fileWebHookSecretKey 本机密钥，回调外部服务时携带，用于验证身份
 * @property httpPort HTTP 服务监听端口，默认 8089
 * @property pythonInterpreterPath Python 解释器路径，空字符串表示自动检测影刀内置 Python
 */
@Serializable
data class AppSettings(
    val globalQueueMaxLength: Int,
    val fileWebHookName: String,
    val fileWebHookSecretKey: String,
    val httpPort: Int = 8089,
    val pythonInterpreterPath: String = ""
) {
    companion object {
        // ============================================
        // 设置键名常量
        // ============================================

        /** 队列最大长度 */
        const val KEY_GLOBAL_QUEUE_MAX_LENGTH = "globalQueueMaxLength"

        /** FileWebHook 名称 */
        const val KEY_FILE_WEBHOOK_NAME = "fileWebHookName"

        /** FileWebHook 密钥 */
        const val KEY_FILE_WEBHOOK_SECRET_KEY = "fileWebHookSecretKey"

        /** HTTP 端口 */
        const val KEY_HTTP_PORT = "httpPort"

        /** Python 解释器路径 */
        const val KEY_PYTHON_INTERPRETER_PATH = "pythonInterpreterPath"

        /**
         * 默认设置
         *
         * 应用首次启动时使用的默认配置
         */
        val DEFAULT = AppSettings(
            globalQueueMaxLength = 10,       // 默认队列容量
            fileWebHookName = "xiaokeer",    // 默认名称
            fileWebHookSecretKey = "123456", // 默认密钥
            httpPort = 58700,                // 默认端口
            pythonInterpreterPath = ""       // 空字符串表示自动检测
        )

        /**
         * 获取固定的触发器文件路径
         *
         * 固定使用用户目录/.filewebhook/triggerFiles
         *
         * @return 触发器文件目录的绝对路径
         */
        fun getTriggerFilesPath(): String {
            return java.io.File(System.getProperty("user.home"), ".filewebhook/triggerFiles").absolutePath
        }
    }
}
