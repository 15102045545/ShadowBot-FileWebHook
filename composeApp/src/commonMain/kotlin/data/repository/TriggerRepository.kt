/**
 * 触发器数据仓库
 *
 * 本文件提供触发器数据的 CRUD 操作
 * 触发器创建时会自动创建对应的文件夹
 */

package data.repository

import com.filewebhook.db.FileWebHookDatabase
import data.model.Trigger
import data.model.CreateTriggerRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.io.File

/**
 * 触发器仓库类
 *
 * 封装所有触发器相关的数据库操作
 *
 * @property database SQLDelight 生成的数据库实例
 * @property settingsRepository 设置仓库，用于获取 triggerFilesPath
 */
class TriggerRepository(
    private val database: FileWebHookDatabase,
    private val settingsRepository: SettingsRepository
) {
    /** 获取查询接口 */
    private val queries get() = database.fileWebHookQueries

    /**
     * 获取所有触发器
     *
     * @return 按 ID 升序排列的触发器列表
     */
    suspend fun getAllTriggers(): List<Trigger> = withContext(Dispatchers.IO) {
        queries.selectAllTriggers().executeAsList().map { it.toTrigger() }
    }

    /**
     * 根据 ID 获取触发器
     *
     * @param triggerId 触发器 ID
     * @return 触发器对象，不存在返回 null
     */
    suspend fun getTriggerById(triggerId: Long): Trigger? = withContext(Dispatchers.IO) {
        queries.selectTriggerById(triggerId).executeAsOneOrNull()?.toTrigger()
    }

    /**
     * 创建新触发器
     *
     * 创建流程：
     * 1. 获取当前最大 ID，新 ID = 最大 ID + 1
     * 2. 生成文件夹路径：{triggerFilesPath}/{newId}
     * 3. 插入数据库记录
     * 4. 创建实际的文件夹
     *
     * @param request 创建触发器请求
     * @return 创建成功的触发器对象
     */
    suspend fun createTrigger(request: CreateTriggerRequest): Trigger = withContext(Dispatchers.IO) {
        // 获取 triggerFilesPath 配置
        val settings = settingsRepository.getSettings()
        val basePath = settings.triggerFilesPath.ifEmpty {
            // 默认路径：用户目录/.filewebhook/triggerFiles
            File(System.getProperty("user.home"), ".filewebhook/triggerFiles").absolutePath
        }

        // 计算新的触发器 ID
        val lastId = queries.selectLastTriggerId().executeAsOneOrNull()?.lastId ?: 0L
        val newId = lastId + 1

        // 生成文件夹路径
        val folderPath = File(basePath, newId.toString()).absolutePath

        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toString()

        // 插入数据库
        queries.insertTrigger(
            name = request.name,
            description = request.description,
            remark = request.remark,
            folderPath = folderPath,
            createdAt = now
        )

        // 创建触发器文件夹（影刀文件触发器需要监听此文件夹）
        File(folderPath).mkdirs()

        // 返回创建的触发器
        Trigger(
            id = newId,
            name = request.name,
            description = request.description,
            remark = request.remark,
            folderPath = folderPath,
            createdAt = now
        )
    }

    /**
     * 将数据库实体转换为领域模型
     */
    private fun com.filewebhook.db.Triggers.toTrigger() = Trigger(
        id = id,
        name = name,
        description = description,
        remark = remark,
        folderPath = folderPath,
        createdAt = createdAt
    )
}
