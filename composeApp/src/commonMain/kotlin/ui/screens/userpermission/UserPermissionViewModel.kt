/**
 * 用户权限管理 ViewModel
 *
 * 本文件提供用户权限管理页面的状态管理和业务逻辑
 * 支持权限列表查询、按用户ID搜索、生成Java客户端请求示例
 */

package ui.screens.userpermission

import androidx.compose.runtime.*
import data.model.UserPermission
import data.repository.PermissionRepository
import data.repository.SettingsRepository
import data.repository.UserRepository

/**
 * 用户权限 ViewModel 类
 *
 * 管理用户权限列表页面的 UI 状态和用户交互
 *
 * @property permissionRepository 权限仓库
 * @property userRepository 用户仓库（用于获取 secretKey）
 * @property settingsRepository 设置仓库（用于获取端口和 fileWebHookName）
 */
class UserPermissionViewModel(
    private val permissionRepository: PermissionRepository,
    private val userRepository: UserRepository,
    private val settingsRepository: SettingsRepository
) {
    /** 权限列表状态 */
    private val _permissions = mutableStateOf<List<UserPermission>>(emptyList())
    val permissions: State<List<UserPermission>> = _permissions

    /** 加载中状态 */
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    /** 搜索的用户ID */
    private val _searchUserId = mutableStateOf("")
    val searchUserId: State<String> = _searchUserId

    /**
     * 加载权限列表
     *
     * 如果有搜索条件则按用户ID搜索，否则加载全部
     */
    suspend fun loadPermissions() {
        _isLoading.value = true
        try {
            _permissions.value = if (_searchUserId.value.isBlank()) {
                permissionRepository.getAllPermissions()
            } else {
                permissionRepository.getAllPermissionsByUserId(_searchUserId.value.trim())
            }
        } catch (e: Exception) {
            _permissions.value = emptyList()
        } finally {
            _isLoading.value = false
        }
    }

    /**
     * 设置搜索用户ID
     *
     * @param userId 用户ID
     */
    fun setSearchUserId(userId: String) {
        _searchUserId.value = userId
    }

    /**
     * 生成 Java 客户端请求示例代码
     *
     * 根据权限记录动态生成包含用户信息和系统配置的 Java 代码示例
     *
     * @param permission 权限记录
     * @return Java 代码示例字符串
     */
    suspend fun generateJavaClientExample(permission: UserPermission): String {
        // 获取用户信息（包含 secretKey）
        val user = userRepository.getUserById(permission.userId)
        val secretKey = user?.secretKey ?: "USER_NOT_FOUND"

        // 获取系统设置（端口和 fileWebHookName）
        val settings = settingsRepository.getSettings()
        val httpPort = settings.httpPort
        val fileWebHookName = settings.fileWebHookName

        return """
/**
 * 执行应用
 */
@PostMapping("triggerExecute")
public String triggerExecute() throws Exception {
    JSONObject request = new JSONObject();
    request.set("triggerId", "${permission.triggerId}");
    request.set("userId", "${permission.userId}");
    request.set("secretKey", "$secretKey");

    JSONObject requestParam = new JSONObject();
    requestParam.set("key","value");
    request.set("requestParam", requestParam);

    String response = HttpUtils.doPost("http://127.0.0.1:$httpPort/trigger/execute", request.toString());
    log.info("triggerExecute response: {}", response);
    return response;
}

/**
 * 接收应用回调
 * @param object 回调信息
 * @return 回调信息
 */
@PostMapping("/$fileWebHookName/filewebhook/notify")
public Object notify(@RequestBody Object object) {
    log.info("notify object: {}", object);
    return object;
}
""".trimIndent()
    }
}
