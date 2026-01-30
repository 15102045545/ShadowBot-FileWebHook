/**
 * 触发器管理 ViewModel
 *
 * 本文件提供触发器管理页面的状态管理和业务逻辑
 * 遵循 MVVM 架构模式
 */

package ui.screens.trigger

import androidx.compose.runtime.*
import data.model.CreateTriggerRequest
import data.model.Trigger
import data.repository.TriggerRepository
import kotlinx.coroutines.launch

/**
 * 触发器 ViewModel 类
 *
 * 管理触发器列表页面的 UI 状态和用户交互
 *
 * @property triggerRepository 触发器仓库
 */
class TriggerViewModel(private val triggerRepository: TriggerRepository) {
    /** 触发器列表状态 */
    private val _triggers = mutableStateOf<List<Trigger>>(emptyList())
    val triggers: State<List<Trigger>> = _triggers

    /** 加载中状态 */
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    /** 错误信息状态 */
    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

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
}
