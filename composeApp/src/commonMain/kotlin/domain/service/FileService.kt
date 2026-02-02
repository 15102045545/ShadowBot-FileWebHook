/**
 * 文件服务
 *
 * 本文件提供 request.json 文件的读写操作
 * request.json 是 FileWebHook 与影刀文件触发器的交互媒介
 *
 * 文件操作策略（v1.2.0 优化）：
 * - 直接覆盖写入文件，不删除后再创建
 * - 影刀监听文件修改事件（而非创建事件）
 * - 串行队列确保无并发问题
 */

package domain.service

import data.model.RequestFileContent
import data.repository.SettingsRepository
import data.repository.TriggerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import java.io.File

/**
 * 文件服务类
 *
 * 负责管理触发器文件夹中的 request.json 文件
 * 文件路径格式：{triggerFilesPath}/{triggerId}/request.json
 *
 * @property triggerRepository 触发器仓库，获取触发器配置
 * @property settingsRepository 设置仓库，获取基础路径
 */
class FileService(
    private val triggerRepository: TriggerRepository,
    private val settingsRepository: SettingsRepository
) {
    /** JSON 序列化器 */
    private val json = Json {
        prettyPrint = true      // 格式化输出，便于调试
        encodeDefaults = true   // 编码默认值
    }

    /**
     * 写入 request.json 文件
     *
     * 当任务开始处理时调用，写入执行参数供影刀读取
     *
     * v1.2.0 优化：直接覆盖写入文件
     * - 影刀监听文件修改事件（而非创建事件）
     * - 串行队列确保无并发问题，文件唯一
     * - 无需删除后再创建，直接覆盖即可触发影刀
     *
     * 文件内容格式（RequestFileContent）：
     * {
     *   "eventId": "1-1234567890",
     *   "triggerId": "1",
     *   "userId": "uuid-xxx",
     *   "requestData": { ... }
     * }
     *
     * @param triggerId 触发器 ID
     * @param eventId 事件 ID
     * @param userId 用户 ID
     * @param requestData 业务请求参数
     * @return Result<File> 成功返回文件对象，失败返回异常
     */
    suspend fun writeRequestFile(
        triggerId: Long,
        eventId: String,
        userId: String,
        requestData: JsonObject
    ): Result<File> = withContext(Dispatchers.IO) {
        try {
            // 获取触发器信息（主要是 folderPath）
            val trigger = triggerRepository.getTriggerById(triggerId)
                ?: return@withContext Result.failure(Exception("Trigger not found: $triggerId"))

            // 确保触发器目录存在
            val triggerDir = File(trigger.folderPath)
            if (!triggerDir.exists()) {
                triggerDir.mkdirs()
            }

            // 构建文件路径
            val requestFile = File(triggerDir, "request.json")

            // 构建文件内容
            val content = RequestFileContent(
                eventId = eventId,
                triggerId = triggerId.toString(),
                userId = userId,
                requestData = requestData
            )

            // 直接覆盖写入文件（影刀监听文件修改事件）
            requestFile.writeText(json.encodeToString(content))

            Result.success(requestFile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 删除 request.json 文件
     *
     * v1.2.0 注意：当前版本不再调用此方法
     * 文件将在下次任务执行时被覆盖，无需显式删除
     * 保留此方法以备将来需要清理文件时使用
     *
     * @param triggerId 触发器 ID
     * @return Result<Unit> 成功或失败
     */
    suspend fun deleteRequestFile(triggerId: Long): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // 获取触发器信息
            val trigger = triggerRepository.getTriggerById(triggerId)
                ?: return@withContext Result.failure(Exception("Trigger not found: $triggerId"))

            // 删除文件（如果存在）
            val requestFile = File(trigger.folderPath, "request.json")
            if (requestFile.exists()) {
                requestFile.delete()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 确保触发器目录存在
     *
     * 创建触发器时调用，确保文件夹已创建
     *
     * @param triggerId 触发器 ID
     * @return Result<File> 成功返回目录对象，失败返回异常
     */
    suspend fun ensureTriggerDirectory(triggerId: Long): Result<File> = withContext(Dispatchers.IO) {
        try {
            // 获取触发器信息
            val trigger = triggerRepository.getTriggerById(triggerId)
                ?: return@withContext Result.failure(Exception("Trigger not found: $triggerId"))

            // 创建目录（如果不存在）
            val triggerDir = File(trigger.folderPath)
            if (!triggerDir.exists()) {
                triggerDir.mkdirs()
            }

            Result.success(triggerDir)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
