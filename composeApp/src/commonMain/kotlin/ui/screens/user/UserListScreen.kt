/**
 * 用户列表页面
 *
 * 本文件提供用户管理的主界面
 * 支持用户的增删改查、密钥重置和权限分配
 */

package ui.screens.user

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.FileCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import data.model.User
import kotlinx.coroutines.launch
import ui.components.*

/**
 * 用户列表页面组件
 *
 * 显示用户列表，支持：
 * - 查看用户信息（名称、回调地址、密钥、创建时间）
 * - 新建用户
 * - 编辑用户
 * - 删除用户
 * - 重置密钥
 * - 分配权限
 *
 * @param viewModel 用户 ViewModel
 * @param modifier 修饰符
 */
@Composable
fun UserListScreen(
    viewModel: UserViewModel,
    modifier: Modifier = Modifier
) {
    // 从 ViewModel 获取状态
    val users by viewModel.users
    val triggers by viewModel.triggers
    val isLoading by viewModel.isLoading
    val scope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current

    // 对话框状态
    var showCreateDialog by remember { mutableStateOf(false) }
    var editingUser by remember { mutableStateOf<User?>(null) }
    var permissionUser by remember { mutableStateOf<User?>(null) }
    var deleteUser by remember { mutableStateOf<User?>(null) }
    var resetKeyUser by remember { mutableStateOf<User?>(null) }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }

    // 页面加载时获取数据
    LaunchedEffect(Unit) {
        viewModel.loadUsers()
    }

    Column(modifier = modifier.fillMaxSize().padding(24.dp)) {
        // 页面顶部：标题和新建按钮
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "用户管理",
                style = MaterialTheme.typography.headlineMedium
            )

            Button(onClick = { showCreateDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("新建用户")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 页面主体：用户列表
        if (isLoading) {
            // 加载中状态
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (users.isEmpty()) {
            // 空状态提示
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "暂无用户，点击右上角按钮创建",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            // 用户数据表格
            DataTable(
                items = users,
                columns = listOf(
                    // 用户ID列（带复制按钮）
                    TableColumn(header = "用户ID", weight = 1f) { user ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            TableCellText(
                                text = user.userId,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(user.userId))
                                    snackbarMessage = "已复制用户ID到剪贴板"
                                }
                            ) {
                                Icon(
                                    Icons.Outlined.FileCopy,
                                    contentDescription = "复制用户ID",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    },
                    // 名称列
                    TableColumn(header = "名称", weight = 1f) { user ->
                        TableCellText(user.name)
                    },
                    // 回调地址列
                    TableColumn(header = "回调地址", weight = 1.5f) { user ->
                        TableCellText(user.callbackUrl)
                    },
                    // 密钥列（带复制按钮，仅显示前 8 位）
                    TableColumn(header = "密钥", weight = 1.5f) { user ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            TableCellText(
                                text = "${user.secretKey.take(8)}...",
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(user.secretKey))
                                    snackbarMessage = "已复制密钥到剪贴板"
                                }
                            ) {
                                Icon(
                                    Icons.Outlined.FileCopy,
                                    contentDescription = "复制",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    },
                    // 创建时间列
                    TableColumn(header = "创建时间", weight = 1f) { user ->
                        TableCellText(user.createdAt.take(19).replace("T", " "))
                    },
                    // 操作列（编辑、权限、重置密钥、删除）
                    TableColumn(header = "操作", weight = 1.2f) { user ->
                        Row {
                            // 编辑按钮
                            IconButton(onClick = { editingUser = user }) {
                                Icon(Icons.Default.Edit, "编辑", modifier = Modifier.size(18.dp))
                            }
                            // 权限按钮
                            IconButton(onClick = { permissionUser = user }) {
                                Icon(Icons.Default.Lock, "权限", modifier = Modifier.size(18.dp))
                            }
                            // 重置密钥按钮
                            IconButton(onClick = { resetKeyUser = user }) {
                                Icon(Icons.Default.Refresh, "重置密钥", modifier = Modifier.size(18.dp))
                            }
                            // 删除按钮（红色）
                            IconButton(onClick = { deleteUser = user }) {
                                Icon(
                                    Icons.Default.Delete,
                                    "删除",
                                    modifier = Modifier.size(18.dp),
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                )
            )
        }
    }

    // 新建用户对话框
    if (showCreateDialog) {
        UserFormDialog(
            title = "新建用户",
            onDismiss = { showCreateDialog = false },
            onConfirm = { name, remark, callbackUrl, callbackHeaders ->
                scope.launch {
                    val result = viewModel.createUser(name, remark, callbackUrl, callbackHeaders)
                    snackbarMessage = if (result.isSuccess) "创建成功" else "创建失败"
                }
                showCreateDialog = false
            }
        )
    }

    // 编辑用户对话框
    editingUser?.let { user ->
        UserFormDialog(
            title = "编辑用户",
            initialName = user.name,
            initialRemark = user.remark ?: "",
            initialCallbackUrl = user.callbackUrl,
            initialCallbackHeaders = user.callbackHeaders,
            onDismiss = { editingUser = null },
            onConfirm = { name, remark, callbackUrl, callbackHeaders ->
                scope.launch {
                    val result = viewModel.updateUser(user.userId, name, remark, callbackUrl, callbackHeaders)
                    snackbarMessage = if (result.isSuccess) "更新成功" else "更新失败"
                }
                editingUser = null
            }
        )
    }

    // 权限分配对话框
    permissionUser?.let { user ->
        var userPermissions by remember { mutableStateOf<List<Long>>(emptyList()) }

        // 加载用户当前权限
        LaunchedEffect(user.userId) {
            userPermissions = viewModel.getUserPermissions(user.userId)
        }

        PermissionDialog(
            userName = user.name,
            triggers = triggers,
            selectedTriggerIds = userPermissions,
            onDismiss = { permissionUser = null },
            onConfirm = { selectedIds ->
                scope.launch {
                    val result = viewModel.setUserPermissions(user.userId, selectedIds)
                    snackbarMessage = if (result.isSuccess) "权限更新成功" else "权限更新失败"
                }
                permissionUser = null
            }
        )
    }

    // 删除确认对话框
    deleteUser?.let { user ->
        ConfirmDialog(
            title = "删除用户",
            message = "确定要删除用户 \"${user.name}\" 吗？此操作不可恢复。",
            onDismiss = { deleteUser = null },
            onConfirm = {
                scope.launch {
                    val result = viewModel.deleteUser(user.userId)
                    snackbarMessage = if (result.isSuccess) "删除成功" else "删除失败"
                }
                deleteUser = null
            },
            isDestructive = true
        )
    }

    // 重置密钥确认对话框
    resetKeyUser?.let { user ->
        ConfirmDialog(
            title = "重置密钥",
            message = "确定要重置用户 \"${user.name}\" 的密钥吗？旧密钥将立即失效。",
            onDismiss = { resetKeyUser = null },
            onConfirm = {
                scope.launch {
                    val result = viewModel.resetSecretKey(user.userId)
                    if (result.isSuccess) {
                        // 自动复制新密钥到剪贴板
                        clipboardManager.setText(AnnotatedString(result.getOrNull() ?: ""))
                        snackbarMessage = "密钥已重置并复制到剪贴板"
                    } else {
                        snackbarMessage = "重置失败"
                    }
                }
                resetKeyUser = null
            }
        )
    }
}

/**
 * 用户表单对话框
 *
 * 用于新建和编辑用户
 *
 * @param title 对话框标题
 * @param initialName 初始名称
 * @param initialRemark 初始备注
 * @param initialCallbackUrl 初始回调地址
 * @param initialCallbackHeaders 初始回调请求头
 * @param onDismiss 取消回调
 * @param onConfirm 确认回调
 */
@Composable
private fun UserFormDialog(
    title: String,
    initialName: String = "",
    initialRemark: String = "",
    initialCallbackUrl: String = "",
    initialCallbackHeaders: Map<String, String> = emptyMap(),
    onDismiss: () -> Unit,
    onConfirm: (name: String, remark: String?, callbackUrl: String, callbackHeaders: Map<String, String>) -> Unit
) {
    // 表单状态
    var name by remember { mutableStateOf(initialName) }
    var remark by remember { mutableStateOf(initialRemark) }
    var callbackUrl by remember { mutableStateOf(initialCallbackUrl) }
    var callbackHeaders by remember { mutableStateOf(initialCallbackHeaders.toMutableMap()) }
    var newHeaderKey by remember { mutableStateOf("") }
    var newHeaderValue by remember { mutableStateOf("") }

    FormDialog(
        title = title,
        onDismiss = onDismiss,
        onConfirm = {
            onConfirm(name, remark.ifBlank { null }, callbackUrl, callbackHeaders)
        },
        confirmEnabled = name.isNotBlank() && callbackUrl.isNotBlank()  // 名称和回调地址为必填
    ) {
        // 名称输入框（必填）
        FormTextField(
            value = name,
            onValueChange = { name = it },
            label = "名称 *"
        )
        Spacer(modifier = Modifier.height(16.dp))
        // 备注输入框（可选）
        FormTextField(
            value = remark,
            onValueChange = { remark = it },
            label = "备注"
        )
        Spacer(modifier = Modifier.height(16.dp))
        // 回调地址输入框（必填）
        FormTextField(
            value = callbackUrl,
            onValueChange = { callbackUrl = it },
            label = "回调地址 *",
            supportingText = "例如: http://192.168.1.100:8080"
        )
        Spacer(modifier = Modifier.height(16.dp))

        // 回调请求头部分
        Text(
            text = "回调请求头（可选）",
            style = MaterialTheme.typography.titleSmall
        )
        Spacer(modifier = Modifier.height(8.dp))

        // 显示已有的请求头
        if (callbackHeaders.isNotEmpty()) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                callbackHeaders.forEach { (key, value) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$key: $value",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = {
                                callbackHeaders = callbackHeaders.toMutableMap().apply { remove(key) }
                            }
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "删除",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // 添加新请求头的输入框
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FormTextField(
                value = newHeaderKey,
                onValueChange = { newHeaderKey = it },
                label = "Header Key",
                modifier = Modifier.weight(1f)
            )
            FormTextField(
                value = newHeaderValue,
                onValueChange = { newHeaderValue = it },
                label = "Header Value",
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                if (newHeaderKey.isNotBlank() && newHeaderValue.isNotBlank()) {
                    callbackHeaders = callbackHeaders.toMutableMap().apply {
                        put(newHeaderKey.trim(), newHeaderValue.trim())
                    }
                    newHeaderKey = ""
                    newHeaderValue = ""
                }
            },
            enabled = newHeaderKey.isNotBlank() && newHeaderValue.isNotBlank()
        ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("添加请求头")
        }
    }
}

/**
 * 权限分配对话框
 *
 * 显示触发器列表，可勾选分配给用户
 *
 * @param userName 用户名称（显示在标题中）
 * @param triggers 可用触发器列表
 * @param selectedTriggerIds 当前已选中的触发器 ID 列表
 * @param onDismiss 取消回调
 * @param onConfirm 确认回调，传递选中的触发器 ID 列表
 */
@Composable
private fun PermissionDialog(
    userName: String,
    triggers: List<data.model.Trigger>,
    selectedTriggerIds: List<Long>,
    onDismiss: () -> Unit,
    onConfirm: (List<Long>) -> Unit
) {
    // 选中状态
    var selected by remember(selectedTriggerIds) { mutableStateOf(selectedTriggerIds.toSet()) }

    FormDialog(
        title = "分配权限 - $userName",
        onDismiss = onDismiss,
        onConfirm = { onConfirm(selected.toList()) }
    ) {
        if (triggers.isEmpty()) {
            // 无触发器提示
            Text(
                text = "暂无可分配的触发器",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            // 触发器复选框列表
            Column {
                triggers.forEach { trigger ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = trigger.id in selected,
                            onCheckedChange = { checked ->
                                selected = if (checked) {
                                    selected + trigger.id
                                } else {
                                    selected - trigger.id
                                }
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(trigger.name)
                            // 显示描述（如果有）
                            trigger.description?.let {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
