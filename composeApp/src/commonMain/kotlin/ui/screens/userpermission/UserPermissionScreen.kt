/**
 * 用户权限管理页面
 *
 * 本文件提供用户权限管理的主界面
 * 支持权限列表展示、按用户ID搜索、复制Java客户端请求示例
 */

package ui.screens.userpermission

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import data.model.UserPermission
import kotlinx.coroutines.launch
import ui.components.*

/**
 * 用户权限列表页面组件
 *
 * 显示用户权限列表，支持：
 * - 查看权限信息（ID、用户ID、触发器ID、创建时间）
 * - 按用户ID精准搜索
 * - 复制Java客户端请求示例
 *
 * @param viewModel 用户权限 ViewModel
 * @param modifier 修饰符
 */
@Composable
fun UserPermissionScreen(
    viewModel: UserPermissionViewModel,
    modifier: Modifier = Modifier
) {
    // 从 ViewModel 获取状态
    val permissions by viewModel.permissions
    val isLoading by viewModel.isLoading
    val searchUserId by viewModel.searchUserId
    val scope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current

    // Snackbar 状态
    var snackbarMessage by remember { mutableStateOf<String?>(null) }

    // 页面加载时获取数据
    LaunchedEffect(Unit) {
        viewModel.loadPermissions()
    }

    Column(modifier = modifier.fillMaxSize().padding(24.dp)) {
        // 页面顶部：标题
        Text(
            text = "用户权限管理",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 搜索框
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchUserId,
                onValueChange = { viewModel.setSearchUserId(it) },
                label = { Text("用户ID搜索") },
                placeholder = { Text("输入用户ID精准搜索") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = {
                        scope.launch {
                            viewModel.loadPermissions()
                        }
                    }) {
                        Icon(Icons.Default.Search, contentDescription = "搜索")
                    }
                }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = {
                scope.launch {
                    viewModel.loadPermissions()
                }
            }) {
                Icon(Icons.Default.Search, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("搜索")
            }
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedButton(onClick = {
                viewModel.setSearchUserId("")
                scope.launch {
                    viewModel.loadPermissions()
                }
            }) {
                Text("清空")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 页面主体：权限列表
        if (isLoading) {
            // 加载中状态
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (permissions.isEmpty()) {
            // 空状态提示
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = if (searchUserId.isBlank()) "暂无权限数据" else "未找到该用户的权限记录",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            // 权限数据表格
            DataTable(
                items = permissions,
                columns = listOf(
                    // ID列
                    TableColumn(header = "ID", weight = 0.5f) { permission ->
                        TableCellText(permission.id.toString())
                    },
                    // 用户ID列
                    TableColumn(header = "用户ID", weight = 1.5f) { permission ->
                        TableCellText(permission.userId)
                    },
                    // 触发器ID列
                    TableColumn(header = "触发器ID", weight = 0.7f) { permission ->
                        TableCellText(permission.triggerId.toString())
                    },
                    // 创建时间列
                    TableColumn(header = "创建时间", weight = 1f) { permission ->
                        TableCellText(permission.createdAt.take(19).replace("T", " "))
                    },
                    // 操作列
                    TableColumn(header = "操作", weight = 1f) { permission ->
                        CopyClientExampleButton(
                            permission = permission,
                            viewModel = viewModel,
                            clipboardManager = clipboardManager,
                            onCopied = { snackbarMessage = "已复制Java客户端请求示例到剪贴板" }
                        )
                    }
                )
            )
        }
    }

    // Snackbar 显示
    snackbarMessage?.let { message ->
        LaunchedEffect(message) {
            kotlinx.coroutines.delay(2000)
            snackbarMessage = null
        }
    }
}

/**
 * 复制客户端请求示例按钮组件
 *
 * 点击后生成Java客户端请求示例并复制到剪贴板
 *
 * @param permission 权限记录
 * @param viewModel ViewModel（用于生成示例代码）
 * @param clipboardManager 剪贴板管理器
 * @param onCopied 复制成功后的回调
 */
@Composable
private fun CopyClientExampleButton(
    permission: UserPermission,
    viewModel: UserPermissionViewModel,
    clipboardManager: androidx.compose.ui.platform.ClipboardManager,
    onCopied: () -> Unit
) {
    val scope = rememberCoroutineScope()

    Button(
        onClick = {
            scope.launch {
                val example = viewModel.generateJavaClientExample(permission)
                clipboardManager.setText(AnnotatedString(example))
                onCopied()
            }
        },
        modifier = Modifier.height(36.dp),
        contentPadding = PaddingValues(horizontal = 12.dp)
    ) {
        Icon(
            Icons.Outlined.ContentCopy,
            contentDescription = "复制",
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text("拷贝客户端请求示例", style = MaterialTheme.typography.bodySmall)
    }
}
