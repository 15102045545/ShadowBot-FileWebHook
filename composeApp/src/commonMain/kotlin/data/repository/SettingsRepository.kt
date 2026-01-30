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
     * 对于缺失的配置项，使用预定义的默认值
     *
     * 默认值说明：
     * - triggerFilesPath: {user.home}/.filewebhook/triggerFiles
     * - globalQueueMaxLength: 50
     * - fileWebHookName: 空字符串
     * - fileWebHookSecretKey: 空字符串
     * - httpPort: 8089
     *
     * @return 系统设置对象
     */
    suspend fun getSettings(): AppSettings = withContext(Dispatchers.IO) {
        // 查询所有设置项，转换为 Map 便于查找
        val allSettings = queries.selectAllSettings().executeAsList().associate { it.settingKey to it.settingValue }

        // 计算默认触发器文件路径
        val defaultTriggerFilesPath = File(System.getProperty("user.home"), ".filewebhook/triggerFiles").absolutePath

        // 构建设置对象，缺失的配置使用默认值
        AppSettings(
            triggerFilesPath = allSettings[AppSettings.KEY_TRIGGER_FILES_PATH] ?: defaultTriggerFilesPath,
            globalQueueMaxLength = allSettings[AppSettings.KEY_GLOBAL_QUEUE_MAX_LENGTH]?.toIntOrNull() ?: 50,
            fileWebHookName = allSettings[AppSettings.KEY_FILE_WEBHOOK_NAME] ?: "",
            fileWebHookSecretKey = allSettings[AppSettings.KEY_FILE_WEBHOOK_SECRET_KEY] ?: "",
            httpPort = allSettings[AppSettings.KEY_HTTP_PORT]?.toIntOrNull() ?: 8089
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

        // 确保触发器文件目录存在
        // 该目录用于存放各触发器的 request.json 文件
        if (settings.triggerFilesPath.isNotEmpty()) {
            File(settings.triggerFilesPath).mkdirs()
        }
    }
}
