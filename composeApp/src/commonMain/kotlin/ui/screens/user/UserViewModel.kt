/**
 * 用户管理 ViewModel
 *
 * 本文件提供用户管理页面的状态管理和业务逻辑
 * 支持用户 CRUD、密钥重置和权限管理
 */

package ui.screens.user

import androidx.compose.runtime.*
import data.model.CreateUserRequest
import data.model.Trigger
import data.model.UpdateUserRequest
import data.model.User
import data.repository.PermissionRepository
import data.repository.TriggerRepository
import data.repository.UserRepository

/**
 * 用户 ViewModel 类
 *
 * 管理用户列表页面的 UI 状态和用户交互
 *
 * @property userRepository 用户仓库
 * @property triggerRepository 触发器仓库（用于权限分配时显示触发器列表）
 * @property permissionRepository 权限仓库
 */
class UserViewModel(
    private val userRepository: UserRepository,
    private val triggerRepository: TriggerRepository,
    private val permissionRepository: PermissionRepository
) {
    /** 用户列表状态 */
    private val _users = mutableStateOf<List<User>>(emptyList())
    val users: State<List<User>> = _users

    /** 触发器列表状态（用于权限分配） */
    private val _triggers = mutableStateOf<List<Trigger>>(emptyList())
    val triggers: State<List<Trigger>> = _triggers

    /** 加载中状态 */
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    /** 错误信息状态 */
    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    /**
     * 加载用户列表和触发器列表
     *
     * 同时加载两个列表，便于权限分配功能使用
     */
    suspend fun loadUsers() {
        _isLoading.value = true
        _error.value = null
        try {
            _users.value = userRepository.getAllUsers()
            _triggers.value = triggerRepository.getAllTriggers()
        } catch (e: Exception) {
            _error.value = e.message
        } finally {
            _isLoading.value = false
        }
    }

    /**
     * 创建新用户
     *
     * @param name 用户名称
     * @param remark 备注（可选）
     * @param callbackUrl 回调地址
     * @param callbackHeaders 回调请求头
     * @return Result<User> 创建结果
     */
    suspend fun createUser(
        name: String,
        remark: String?,
        callbackUrl: String,
        callbackHeaders: Map<String, String>
    ): Result<User> {
        return try {
            val user = userRepository.createUser(
                CreateUserRequest(name, remark, callbackUrl, callbackHeaders)
            )
            loadUsers()
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 更新用户信息
     *
     * @param userId 用户 ID
     * @param name 新名称
     * @param remark 新备注
     * @param callbackUrl 新回调地址
     * @param callbackHeaders 回调请求头
     * @return Result<Unit> 更新结果
     */
    suspend fun updateUser(
        userId: String,
        name: String,
        remark: String?,
        callbackUrl: String,
        callbackHeaders: Map<String, String>
    ): Result<Unit> {
        return try {
            userRepository.updateUser(userId, UpdateUserRequest(name, remark, callbackUrl, callbackHeaders))
            loadUsers()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 删除用户
     *
     * 同时删除用户的所有权限关联
     *
     * @param userId 用户 ID
     * @return Result<Unit> 删除结果
     */
    suspend fun deleteUser(userId: String): Result<Unit> {
        return try {
            userRepository.deleteUser(userId)
            loadUsers()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 重置用户密钥
     *
     * 生成新密钥，旧密钥立即失效
     *
     * @param userId 用户 ID
     * @return Result<String> 新密钥
     */
    suspend fun resetSecretKey(userId: String): Result<String> {
        return try {
            val newKey = userRepository.resetSecretKey(userId)
            loadUsers()
            Result.success(newKey)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 获取用户的触发器权限列表
     *
     * @param userId 用户 ID
     * @return 有权访问的触发器 ID 列表
     */
    suspend fun getUserPermissions(userId: String): List<Long> {
        return permissionRepository.getPermissionsByUserId(userId)
    }

    /**
     * 设置用户的触发器权限
     *
     * 替换用户的全部权限
     *
     * @param userId 用户 ID
     * @param triggerIds 新的触发器 ID 列表
     * @return Result<Unit> 设置结果
     */
    suspend fun setUserPermissions(userId: String, triggerIds: List<Long>): Result<Unit> {
        return try {
            permissionRepository.setPermissions(userId, triggerIds)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
