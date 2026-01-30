/**
 * 用户数据仓库
 *
 * 本文件提供用户数据的 CRUD 操作
 * 包括用户创建、查询、更新、删除以及身份验证
 */

package data.repository

import com.filewebhook.db.FileWebHookDatabase
import data.model.User
import data.model.CreateUserRequest
import data.model.UpdateUserRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID

/**
 * 用户仓库类
 *
 * 封装所有用户相关的数据库操作
 *
 * @property database SQLDelight 生成的数据库实例
 */
class UserRepository(private val database: FileWebHookDatabase) {

    /** 获取查询接口 */
    private val queries get() = database.fileWebHookQueries

    /** JSON 序列化器 */
    private val json = Json { ignoreUnknownKeys = true }

    /**
     * 获取所有用户
     *
     * @return 按创建时间倒序排列的用户列表
     */
    suspend fun getAllUsers(): List<User> = withContext(Dispatchers.IO) {
        queries.selectAllUsers().executeAsList().map { it.toUser() }
    }

    /**
     * 根据用户 ID 获取用户
     *
     * @param userId 用户唯一标识
     * @return 用户对象，不存在返回 null
     */
    suspend fun getUserById(userId: String): User? = withContext(Dispatchers.IO) {
        queries.selectUserById(userId).executeAsOneOrNull()?.toUser()
    }

    /**
     * 验证用户身份
     *
     * 同时校验 userId 和 secretKey，用于 API 身份验证
     *
     * @param userId 用户 ID
     * @param secretKey 用户密钥
     * @return 验证成功返回用户对象，失败返回 null
     */
    suspend fun validateUser(userId: String, secretKey: String): User? = withContext(Dispatchers.IO) {
        queries.selectUserByIdAndSecretKey(userId, secretKey).executeAsOneOrNull()?.toUser()
    }

    /**
     * 创建新用户
     *
     * 自动生成 userId 和 secretKey（UUID 格式）
     *
     * @param request 创建用户请求
     * @return 创建成功的用户对象
     */
    suspend fun createUser(request: CreateUserRequest): User = withContext(Dispatchers.IO) {
        // 生成唯一标识和密钥
        val userId = UUID.randomUUID().toString()
        val secretKey = UUID.randomUUID().toString()
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toString()

        // 将 callbackHeaders Map 序列化为 JSON 字符串
        val callbackHeadersJson = if (request.callbackHeaders.isNotEmpty()) {
            json.encodeToString(request.callbackHeaders)
        } else {
            null
        }

        // 插入数据库
        queries.insertUser(
            userId = userId,
            name = request.name,
            remark = request.remark,
            secretKey = secretKey,
            callbackUrl = request.callbackUrl,
            callbackHeaders = callbackHeadersJson,
            createdAt = now,
            updatedAt = now
        )

        // 返回创建的用户
        User(
            userId = userId,
            name = request.name,
            remark = request.remark,
            secretKey = secretKey,
            callbackUrl = request.callbackUrl,
            callbackHeaders = request.callbackHeaders,
            createdAt = now,
            updatedAt = now
        )
    }

    /**
     * 更新用户信息
     *
     * 不包含密钥更新，密钥请使用 resetSecretKey
     *
     * @param userId 用户 ID
     * @param request 更新请求
     * @return 是否更新成功
     */
    suspend fun updateUser(userId: String, request: UpdateUserRequest): Boolean = withContext(Dispatchers.IO) {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toString()

        // 将 callbackHeaders Map 序列化为 JSON 字符串
        val callbackHeadersJson = if (request.callbackHeaders.isNotEmpty()) {
            json.encodeToString(request.callbackHeaders)
        } else {
            null
        }

        queries.updateUser(
            name = request.name,
            remark = request.remark,
            callbackUrl = request.callbackUrl,
            callbackHeaders = callbackHeadersJson,
            updatedAt = now,
            userId = userId
        )
        true
    }

    /**
     * 重置用户密钥
     *
     * 生成新的 UUID 密钥，旧密钥立即失效
     *
     * @param userId 用户 ID
     * @return 新生成的密钥
     */
    suspend fun resetSecretKey(userId: String): String = withContext(Dispatchers.IO) {
        val newSecretKey = UUID.randomUUID().toString()
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toString()
        queries.updateUserSecretKey(
            secretKey = newSecretKey,
            updatedAt = now,
            userId = userId
        )
        newSecretKey
    }

    /**
     * 删除用户
     *
     * 同时删除用户的所有权限关联
     *
     * @param userId 用户 ID
     * @return 是否删除成功
     */
    suspend fun deleteUser(userId: String): Boolean = withContext(Dispatchers.IO) {
        // 先删除权限关联
        queries.deletePermissionsByUserId(userId)
        // 再删除用户
        queries.deleteUser(userId)
        true
    }

    /**
     * 将数据库实体转换为领域模型
     */
    private fun com.filewebhook.db.Users.toUser(): User {
        // 从 JSON 字符串反序列化 callbackHeaders
        val headers = try {
            callbackHeaders?.let {
                json.decodeFromString<Map<String, String>>(it)
            } ?: emptyMap()
        } catch (e: Exception) {
            emptyMap()
        }

        return User(
            userId = userId,
            name = name,
            remark = remark,
            secretKey = secretKey,
            callbackUrl = callbackUrl,
            callbackHeaders = headers,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}
