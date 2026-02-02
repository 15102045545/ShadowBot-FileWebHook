/**
 * 用户权限数据仓库
 *
 * 本文件提供用户-触发器权限关联的 CRUD 操作
 * 控制哪些用户可以调用哪些触发器
 */

package data.repository

import com.filewebhook.db.FileWebHookDatabase
import data.model.UserPermission
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * 权限仓库类
 *
 * 封装用户与触发器之间的权限关联操作
 * 采用多对多关系：一个用户可有多个触发器权限，一个触发器可授权给多个用户
 *
 * @property database SQLDelight 生成的数据库实例
 */
class PermissionRepository(private val database: FileWebHookDatabase) {

    /** 获取查询接口 */
    private val queries get() = database.fileWebHookQueries

    /**
     * 获取用户拥有的所有触发器权限
     *
     * @param userId 用户 ID
     * @return 该用户有权访问的触发器 ID 列表
     */
    suspend fun getPermissionsByUserId(userId: String): List<Long> = withContext(Dispatchers.IO) {
        queries.selectPermissionsByUserId(userId).executeAsList()
    }

    /**
     * 检查用户是否有指定触发器的权限
     *
     * @param userId 用户 ID
     * @param triggerId 触发器 ID
     * @return true 表示有权限，false 表示无权限
     */
    suspend fun hasPermission(userId: String, triggerId: Long): Boolean = withContext(Dispatchers.IO) {
        queries.selectPermissionByUserAndTrigger(userId, triggerId).executeAsOneOrNull() != null
    }

    /**
     * 授予用户触发器权限
     *
     * 如果权限已存在，则忽略（幂等操作）
     *
     * @param userId 用户 ID
     * @param triggerId 触发器 ID
     */
    suspend fun grantPermission(userId: String, triggerId: Long) = withContext(Dispatchers.IO) {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toString()
        try {
            queries.insertPermission(userId, triggerId, now)
        } catch (e: Exception) {
            // 权限已存在，忽略重复插入错误
        }
    }

    /**
     * 撤销用户触发器权限
     *
     * @param userId 用户 ID
     * @param triggerId 触发器 ID
     */
    suspend fun revokePermission(userId: String, triggerId: Long) = withContext(Dispatchers.IO) {
        queries.deletePermission(userId, triggerId)
    }

    /**
     * 设置用户的触发器权限列表
     *
     * 先删除用户的所有现有权限，再批量添加新权限
     * 用于用户编辑页面的权限批量更新
     *
     * @param userId 用户 ID
     * @param triggerIds 要授予的触发器 ID 列表
     */
    suspend fun setPermissions(userId: String, triggerIds: List<Long>) = withContext(Dispatchers.IO) {
        // 先清空该用户的所有权限
        queries.deletePermissionsByUserId(userId)

        // 批量添加新权限
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toString()
        triggerIds.forEach { triggerId ->
            queries.insertPermission(userId, triggerId, now)
        }
    }

    /**
     * 获取所有权限记录
     *
     * @return 所有权限记录列表（按创建时间倒序）
     */
    suspend fun getAllPermissions(): List<UserPermission> = withContext(Dispatchers.IO) {
        queries.selectAllPermissions().executeAsList().map { row ->
            UserPermission(
                id = row.id,
                userId = row.userId,
                triggerId = row.triggerId,
                createdAt = row.createdAt
            )
        }
    }

    /**
     * 获取指定用户的所有权限记录
     *
     * @param userId 用户 ID
     * @return 该用户的所有权限记录列表（按创建时间倒序）
     */
    suspend fun getAllPermissionsByUserId(userId: String): List<UserPermission> = withContext(Dispatchers.IO) {
        queries.selectAllPermissionsByUserId(userId).executeAsList().map { row ->
            UserPermission(
                id = row.id,
                userId = row.userId,
                triggerId = row.triggerId,
                createdAt = row.createdAt
            )
        }
    }
}
