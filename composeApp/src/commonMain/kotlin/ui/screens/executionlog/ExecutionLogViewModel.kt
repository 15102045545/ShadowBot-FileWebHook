/**
 * 执行记录管理 ViewModel
 *
 * 本文件提供执行记录管理页面的状态管理和业务逻辑
 * 遵循 MVVM 架构模式
 */

package ui.screens.executionlog

import androidx.compose.runtime.*
import data.model.ExecutionLog
import data.model.ExecutionStatus
import data.model.Trigger
import data.model.User
import data.repository.ExecutionLogRepository
import data.repository.TriggerRepository
import data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * 执行记录 ViewModel 类
 *
 * 管理执行记录列表页面的 UI 状态和用户交互
 *
 * @property executionLogRepository 执行记录仓库
 * @property triggerRepository 触发器仓库
 * @property userRepository 用户仓库
 */
class ExecutionLogViewModel(
    private val executionLogRepository: ExecutionLogRepository,
    private val triggerRepository: TriggerRepository,
    private val userRepository: UserRepository
) {
    /** 执行记录列表状态 */
    private val _executionLogs = mutableStateOf<List<ExecutionLog>>(emptyList())
    val executionLogs: State<List<ExecutionLog>> = _executionLogs

    /** 触发器列表状态（用于筛选下拉框） */
    private val _triggers = mutableStateOf<List<Trigger>>(emptyList())
    val triggers: State<List<Trigger>> = _triggers

    /** 用户列表状态（用于筛选下拉框） */
    private val _users = mutableStateOf<List<User>>(emptyList())
    val users: State<List<User>> = _users

    /** 加载中状态 */
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    /** 错误信息状态 */
    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    /** 当前筛选的触发器 ID */
    private val _selectedTriggerId = MutableStateFlow<Long?>(null)
    val selectedTriggerId: StateFlow<Long?> = _selectedTriggerId

    /** 当前筛选的用户 ID */
    private val _selectedUserId = MutableStateFlow<String?>(null)
    val selectedUserId: StateFlow<String?> = _selectedUserId

    /** 当前筛选的状态 */
    private val _selectedStatus = MutableStateFlow<ExecutionStatus?>(null)
    val selectedStatus: StateFlow<ExecutionStatus?> = _selectedStatus

    /** 当前筛选的事件 ID */
    private val _selectedEventId = MutableStateFlow<String?>(null)
    val selectedEventId: StateFlow<String?> = _selectedEventId

    /** 当前筛选的开始时间 */
    private val _startTime = MutableStateFlow<String?>(null)
    val startTime: StateFlow<String?> = _startTime

    /** 当前筛选的结束时间 */
    private val _endTime = MutableStateFlow<String?>(null)
    val endTime: StateFlow<String?> = _endTime

    /** 当前页码（从 1 开始） */
    private val _currentPage = mutableStateOf(1)
    val currentPage: State<Int> = _currentPage

    /** 每页记录数 */
    private val pageSize = 50L

    /** 总记录数 */
    private val _totalCount = mutableStateOf(0L)
    val totalCount: State<Long> = _totalCount

    /** 总页数 */
    val totalPages: Int
        get() = ((totalCount.value + pageSize - 1) / pageSize).toInt().coerceAtLeast(1)

    /**
     * 初始化，加载触发器和用户列表
     */
    suspend fun initialize() {
        loadTriggers()
        loadUsers()
    }

    /**
     * 加载触发器列表
     *
     * 从数据库获取所有触发器用于筛选下拉框
     */
    private suspend fun loadTriggers() {
        try {
            _triggers.value = triggerRepository.getAllTriggers()
        } catch (e: Exception) {
            _error.value = "加载触发器列表失败: ${e.message}"
        }
    }

    /**
     * 加载用户列表
     *
     * 从数据库获取所有用户用于筛选下拉框
     */
    private suspend fun loadUsers() {
        try {
            _users.value = userRepository.getAllUsers()
        } catch (e: Exception) {
            _error.value = "加载用户列表失败: ${e.message}"
        }
    }

    /**
     * 加载执行记录列表
     *
     * 根据当前筛选条件和分页参数加载数据
     */
    suspend fun loadExecutionLogs() {
        _isLoading.value = true
        _error.value = null
        try {
            val offset = (currentPage.value - 1) * pageSize
            _executionLogs.value = executionLogRepository.getLogsByFilters(
                triggerId = _selectedTriggerId.value,
                userId = _selectedUserId.value,
                status = _selectedStatus.value,
                eventId = _selectedEventId.value,
                startTime = _startTime.value,
                endTime = _endTime.value,
                limit = pageSize,
                offset = offset
            )
            // 统计总记录数
            _totalCount.value = executionLogRepository.countLogsByFilters(
                triggerId = _selectedTriggerId.value,
                userId = _selectedUserId.value,
                status = _selectedStatus.value,
                eventId = _selectedEventId.value,
                startTime = _startTime.value,
                endTime = _endTime.value
            )
        } catch (e: Exception) {
            _error.value = e.message
        } finally {
            _isLoading.value = false
        }
    }

    /**
     * 应用筛选条件
     *
     * @param triggerId 触发器 ID
     * @param userId 用户 ID
     * @param status 执行状态
     * @param eventId 事件 ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     */
    suspend fun applyFilters(
        triggerId: Long?,
        userId: String?,
        status: ExecutionStatus?,
        eventId: String?,
        startTime: String?,
        endTime: String?
    ) {
        _selectedTriggerId.value = triggerId
        _selectedUserId.value = userId
        _selectedStatus.value = status
        _selectedEventId.value = eventId
        _startTime.value = startTime
        _endTime.value = endTime
        _currentPage.value = 1  // 重置到第一页
        loadExecutionLogs()
    }

    /**
     * 清除所有筛选条件
     */
    suspend fun clearFilters() {
        _selectedTriggerId.value = null
        _selectedUserId.value = null
        _selectedStatus.value = null
        _selectedEventId.value = null
        _startTime.value = null
        _endTime.value = null
        _currentPage.value = 1
        loadExecutionLogs()
    }

    /**
     * 设置筛选的触发器 ID
     *
     * @param triggerId 触发器 ID，null 表示显示所有记录
     */
    suspend fun setSelectedTriggerId(triggerId: Long?) {
        _selectedTriggerId.value = triggerId
        _currentPage.value = 1
        loadExecutionLogs()
    }

    /**
     * 跳转到指定页
     *
     * @param page 页码（从 1 开始）
     */
    suspend fun goToPage(page: Int) {
        if (page in 1..totalPages) {
            _currentPage.value = page
            loadExecutionLogs()
        }
    }

    /**
     * 上一页
     */
    suspend fun previousPage() {
        if (currentPage.value > 1) {
            _currentPage.value -= 1
            loadExecutionLogs()
        }
    }

    /**
     * 下一页
     */
    suspend fun nextPage() {
        if (currentPage.value < totalPages) {
            _currentPage.value += 1
            loadExecutionLogs()
        }
    }

    /**
     * 根据触发器 ID 获取触发器名称
     *
     * @param triggerId 触发器 ID
     * @return 触发器名称，未找到返回 null
     */
    fun getTriggerNameById(triggerId: Long): String? {
        return _triggers.value.find { it.id == triggerId }?.name
    }

    /**
     * 根据用户 ID 获取用户名称
     *
     * @param userId 用户 ID
     * @return 用户名称，未找到返回 null
     */
    fun getUserNameById(userId: String): String? {
        return _users.value.find { it.userId == userId }?.name
    }

    /**
     * 检查是否有任何筛选条件
     *
     * @return 是否有筛选条件
     */
    fun hasFilters(): Boolean {
        return _selectedTriggerId.value != null ||
                _selectedUserId.value != null ||
                _selectedStatus.value != null ||
                !_selectedEventId.value.isNullOrBlank() ||
                _startTime.value != null ||
                _endTime.value != null
    }
}
