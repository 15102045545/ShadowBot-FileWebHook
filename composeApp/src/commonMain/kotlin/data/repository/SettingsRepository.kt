/**
 * 系统设置数据仓库
 *
 * 本文件提供系统设置的读写操作
 * 使用键值对形式存储配置项，支持默认值
 */

package data.repository

import com.filewebhook.db.FileWebHookDatabase
import data.model.AppSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * 设置仓库类
 *
 * 封装所有系统设置相关的数据库操作
 * 配置项以键值对形式存储在 settings 表中
 *
 * @property database SQLDelight 生成的数据库实例
 */
class SettingsRepository(private val database: FileWebHookDatabase) {

    /** 获取查询接口 */
    private val queries get() = database.fileWebHookQueries

    /**
     * 获取所有系统设置
     *
     * 从数据库读取所有配置项，组装为 AppSettings 对象
     * 对于缺失的配置项，使用 AppSettings.DEFAULT 中定义的默认值
     *
     * 默认值说明（来自 AppSettings.DEFAULT）：
     * - triggerFilesPath: {user.home}/.filewebhook/triggerFiles（动态计算）
     * - globalQueueMaxLength: 2
     * - fileWebHookName: "xiaokeer"
     * - fileWebHookSecretKey: "123456"
     * - httpPort: 8089
     *
     * @return 系统设置对象
     */
    suspend fun getSettings(): AppSettings = withContext(Dispatchers.IO) {
        // 查询所有设置项，转换为 Map 便于查找
        val allSettings = queries.selectAllSettings().executeAsList().associate { it.settingKey to it.settingValue }

        // 计算默认触发器文件路径
        val defaultTriggerFilesPath = File(System.getProperty("user.home"), ".filewebhook/triggerFiles").absolutePath

        // 构建设置对象，缺失的配置使用 AppSettings.DEFAULT 中定义的默认值
        AppSettings(
            triggerFilesPath = allSettings[AppSettings.KEY_TRIGGER_FILES_PATH]
                ?: defaultTriggerFilesPath,
            globalQueueMaxLength = allSettings[AppSettings.KEY_GLOBAL_QUEUE_MAX_LENGTH]?.toIntOrNull()
                ?: AppSettings.DEFAULT.globalQueueMaxLength,
            fileWebHookName = allSettings[AppSettings.KEY_FILE_WEBHOOK_NAME]
                ?: AppSettings.DEFAULT.fileWebHookName,
            fileWebHookSecretKey = allSettings[AppSettings.KEY_FILE_WEBHOOK_SECRET_KEY]
                ?: AppSettings.DEFAULT.fileWebHookSecretKey,
            httpPort = allSettings[AppSettings.KEY_HTTP_PORT]?.toIntOrNull()
                ?: AppSettings.DEFAULT.httpPort,
            pythonInterpreterPath = allSettings[AppSettings.KEY_PYTHON_INTERPRETER_PATH]
                ?: AppSettings.DEFAULT.pythonInterpreterPath
        )
    }

    /**
     * 保存单个设置项
     *
     * 使用 UPSERT 语义：存在则更新，不存在则插入
     *
     * @param key 设置项键名
     * @param value 设置项值
     */
    suspend fun saveSetting(key: String, value: String) = withContext(Dispatchers.IO) {
        queries.upsertSetting(key, value)
    }

    /**
     * 保存所有设置
     *
     * 批量保存所有配置项到数据库
     * 同时确保触发器文件目录存在
     *
     * @param settings 系统设置对象
     */
    suspend fun saveSettings(settings: AppSettings) = withContext(Dispatchers.IO) {
        // 逐项保存配置（使用 UPSERT 语义）
        queries.upsertSetting(AppSettings.KEY_TRIGGER_FILES_PATH, settings.triggerFilesPath)
        queries.upsertSetting(AppSettings.KEY_GLOBAL_QUEUE_MAX_LENGTH, settings.globalQueueMaxLength.toString())
        queries.upsertSetting(AppSettings.KEY_FILE_WEBHOOK_NAME, settings.fileWebHookName)
        queries.upsertSetting(AppSettings.KEY_FILE_WEBHOOK_SECRET_KEY, settings.fileWebHookSecretKey)
        queries.upsertSetting(AppSettings.KEY_HTTP_PORT, settings.httpPort.toString())
        queries.upsertSetting(AppSettings.KEY_PYTHON_INTERPRETER_PATH, settings.pythonInterpreterPath)

        // 确保触发器文件目录存在
        // 该目录用于存放各触发器的 request.json 文件
        if (settings.triggerFilesPath.isNotEmpty()) {
            File(settings.triggerFilesPath).mkdirs()
        }
    }

    /**
     * 初始化默认设置
     *
     * 检查 settings 表是否为空，如果为空则写入默认配置
     * 该方法应在应用启动时调用，确保数据库中存在初始配置
     *
     * 默认值来源：AppSettings.DEFAULT
     * - triggerFilesPath: {user.home}/.filewebhook/triggerFiles
     * - globalQueueMaxLength: 2
     * - fileWebHookName: "xiaokeer"
     * - fileWebHookSecretKey: "123456"
     * - httpPort: 8089
     */
    suspend fun initializeDefaultSettings() = withContext(Dispatchers.IO) {
        // 检查数据库是否已有设置
        val existingSettings = queries.selectAllSettings().executeAsList()

        // 如果数据库为空，写入默认配置
        if (existingSettings.isEmpty()) {
            // 计算默认触发器文件路径
            val defaultTriggerFilesPath = File(System.getProperty("user.home"), ".filewebhook/triggerFiles").absolutePath

            // 创建包含所有默认值的设置对象
            val defaultSettings = AppSettings.DEFAULT.copy(
                triggerFilesPath = defaultTriggerFilesPath
            )

            // 保存默认设置到数据库
            saveSettings(defaultSettings)
        }
    }
}
