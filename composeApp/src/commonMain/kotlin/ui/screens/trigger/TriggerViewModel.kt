/**
 * 触发器管理 ViewModel
 *
 * 本文件提供触发器管理页面的状态管理和业务逻辑
 * 遵循 MVVM 架构模式
 */

package ui.screens.trigger

import androidx.compose.runtime.*
import data.model.CreateTriggerRequest
import data.model.UpdateTriggerRequest
import data.model.Trigger
import data.repository.SettingsRepository
import data.repository.TriggerRepository
import domain.python.PythonExecutor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 触发器 ViewModel 类
 *
 * 管理触发器列表页面的 UI 状态和用户交互
 *
 * @property triggerRepository 触发器仓库
 * @property settingsRepository 设置仓库
 * @property pythonExecutor Python 脚本执行器
 */
class TriggerViewModel(
    private val triggerRepository: TriggerRepository,
    private val settingsRepository: SettingsRepository,
    private val pythonExecutor: PythonExecutor
) {
    companion object {
        /**
         * 修改后的指令文件名
         */
        private const val MODIFIED_INSTRUCTION_FILENAME = "ShadowbotAppFormwork_InstructData.pkl"
    }

    /** 触发器列表状态 */
    private val _triggers = mutableStateOf<List<Trigger>>(emptyList())
    val triggers: State<List<Trigger>> = _triggers

    /** 加载中状态 */
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    /** 错误信息状态 */
    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    /** 复制框架指令的结果消息 */
    private val _copyFrameworkMessage = mutableStateOf<String?>(null)
    val copyFrameworkMessage: State<String?> = _copyFrameworkMessage

    /** 复制框架指令是否成功 */
    private val _copyFrameworkSuccess = mutableStateOf(false)
    val copyFrameworkSuccess: State<Boolean> = _copyFrameworkSuccess

    /** 是否正在复制框架指令 */
    private val _isCopyingFramework = mutableStateOf(false)
    val isCopyingFramework: State<Boolean> = _isCopyingFramework

    /**
     * 加载触发器列表
     *
     * 从数据库获取所有触发器并更新 UI 状态
     */
    suspend fun loadTriggers() {
        _isLoading.value = true
        _error.value = null
        try {
            _triggers.value = triggerRepository.getAllTriggers()
        } catch (e: Exception) {
            _error.value = e.message
        } finally {
            _isLoading.value = false
        }
    }

    /**
     * 创建新触发器
     *
     * 创建触发器后自动刷新列表
     *
     * @param name 触发器名称
     * @param description 描述（可选）
     * @param remark 备注（可选）
     * @return Result<Trigger> 创建结果
     */
    suspend fun createTrigger(name: String, description: String?, remark: String?): Result<Trigger> {
        return try {
            val trigger = triggerRepository.createTrigger(
                CreateTriggerRequest(name, description, remark)
            )
            // 刷新列表
            loadTriggers()
            Result.success(trigger)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 更新触发器
     *
     * 更新触发器后自动刷新列表
     *
     * @param triggerId 触发器 ID
     * @param name 触发器名称
     * @param description 描述（可选）
     * @param remark 备注（可选）
     * @return Result<Unit> 更新结果
     */
    suspend fun updateTrigger(triggerId: Long, name: String, description: String?, remark: String?): Result<Unit> {
        return try {
            val result = triggerRepository.updateTrigger(
                triggerId,
                UpdateTriggerRequest(name, description, remark)
            )
            // 刷新列表
            loadTriggers()
            result
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 删除触发器
     *
     * 删除触发器后自动刷新列表
     * 注意：不会删除已产生的执行记录
     *
     * @param triggerId 触发器 ID
     * @return Result<Unit> 删除结果
     */
    suspend fun deleteTrigger(triggerId: Long): Result<Unit> {
        return try {
            val result = triggerRepository.deleteTrigger(triggerId)
            // 刷新列表
            loadTriggers()
            result
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 复制 FileWebHook-App-Framework 框架指令到剪贴板
     *
     * 使用 Python 脚本从 BaseFileWebHookAppFramework.pkl 文件读取元指令，
     * 替换变量后写入触发器目录下的临时文件，再从临时文件恢复到剪贴板
     *
     * @param triggerId 触发器 ID
     */
    suspend fun copyFrameworkInstruction(triggerId: Long) {
        _isCopyingFramework.value = true
        _copyFrameworkMessage.value = null

        try {
            // 获取当前设置
            val settings = settingsRepository.getSettings()

            // 获取固定的触发器文件路径
            val triggerFilesPath = data.model.AppSettings.getTriggerFilesPath()

            // 输入文件路径（元指令文件）- 使用 PythonExecutor 提供的路径
            val inputPath = pythonExecutor.getBaseFrameworkFilePath()

            // 输出目录
            val outputDir = "$triggerFilesPath/$triggerId"

            // 输出文件路径（修改后的指令文件）
            val outputPath = "$outputDir/$MODIFIED_INSTRUCTION_FILENAME"

            // 确保输出目录存在
            withContext(Dispatchers.IO) {
                java.io.File(outputDir).mkdirs()
            }

            // 服务器地址
            val serverIpAndPort = "http://127.0.0.1:${settings.httpPort}"

            // 步骤1：使用 modify_shadowbot_instructions.py 修改指令并保存到文件
            val modifyResult = withContext(Dispatchers.IO) {
                pythonExecutor.modifyAndSaveInstructions(
                    inputPath = inputPath,
                    outputPath = outputPath,
                    triggerId = triggerId,
                    triggerFilesPath = triggerFilesPath,
                    serverIpAndPort = serverIpAndPort
                )
            }

            if (!modifyResult.success) {
                _copyFrameworkMessage.value = modifyResult.message
                _copyFrameworkSuccess.value = false
                return
            }

            // 步骤2：从文件加载指令到剪贴板
            val loadResult = withContext(Dispatchers.IO) {
                pythonExecutor.loadInstructionsToClipboard(outputPath)
            }

            _copyFrameworkMessage.value = if (loadResult.success) {
                "框架指令已复制到剪贴板"
            } else {
                loadResult.message
            }
            _copyFrameworkSuccess.value = loadResult.success

        } catch (e: Exception) {
            _copyFrameworkMessage.value = "复制失败: ${e.message}"
            _copyFrameworkSuccess.value = false
        } finally {
            _isCopyingFramework.value = false
        }
    }

    /**
     * 清除复制框架指令的消息
     */
    fun clearCopyFrameworkMessage() {
        _copyFrameworkMessage.value = null
    }
}
