/**
 * 触发器列表页面
 *
 * 本文件提供触发器管理的主界面
 * 包含触发器列表展示和新建触发器功能
 */

package ui.screens.trigger

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.FileCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import data.model.Trigger
import kotlinx.coroutines.launch
import ui.components.*

/**
 * 触发器列表页面组件
 *
 * 显示触发器列表，支持：
 * - 查看触发器信息（ID、名称、描述、文件夹路径、创建时间）
 * - 复制文件夹路径到剪贴板
 * - 新建触发器
 *
 * @param viewModel 触发器 ViewModel
 * @param modifier 修饰符
 */
@Composable
fun TriggerListScreen(
    viewModel: TriggerViewModel,
    modifier: Modifier = Modifier
) {
    // 从 ViewModel 获取状态
    val triggers by viewModel.triggers
    val isLoading by viewModel.isLoading
    val scope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current

    // 本地 UI 状态
    var showCreateDialog by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }

    // 页面加载时获取数据
    LaunchedEffect(Unit) {
        viewModel.loadTriggers()
    }

    Column(modifier = modifier.fillMaxSize().padding(24.dp)) {
        // 页面顶部：标题和新建按钮
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "触发器管理",
                style = MaterialTheme.typography.headlineMedium
            )

            Button(
                onClick = { showCreateDialog = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("新建触发器")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 页面主体：触发器列表
        if (isLoading) {
            // 加载中状态
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (triggers.isEmpty()) {
            // 空状态提示
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "暂无触发器，点击右上角按钮创建",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            // 触发器数据表格
            DataTable(
                items = triggers,
                columns = listOf(
                    // ID 列
                    TableColumn(header = "ID", weight = 0.5f) { trigger ->
                        TableCellText(trigger.id.toString())
                    },
                    // 名称列
                    TableColumn(header = "名称", weight = 1f) { trigger ->
                        TableCellText(trigger.name)
                    },
                    // 描述列
                    TableColumn(header = "描述", weight = 1.5f) { trigger ->
                        TableCellText(trigger.description ?: "-")
                    },
                    // 文件夹路径列（带复制按钮）
                    TableColumn(header = "文件夹路径", weight = 2f) { trigger ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            TableCellText(
                                text = trigger.folderPath,
                                modifier = Modifier.weight(1f)
                            )
                            // 复制路径按钮
                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(trigger.folderPath))
                                    snackbarMessage = "已复制路径到剪贴板"
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
                    TableColumn(header = "创建时间", weight = 1.2f) { trigger ->
                        TableCellText(trigger.createdAt.take(19).replace("T", " "))
                    }
                )
            )
        }
    }

    // 新建触发器对话框
    if (showCreateDialog) {
        CreateTriggerDialog(
            onDismiss = { showCreateDialog = false },
            onConfirm = { name, description, remark ->
                scope.launch {
                    val result = viewModel.createTrigger(name, description, remark)
                    if (result.isSuccess) {
                        snackbarMessage = "创建成功"
                    } else {
                        snackbarMessage = "创建失败: ${result.exceptionOrNull()?.message}"
                    }
                }
                showCreateDialog = false
            }
        )
    }

    // 提示消息（2 秒后自动消失）
    snackbarMessage?.let { message ->
        LaunchedEffect(message) {
            kotlinx.coroutines.delay(2000)
            snackbarMessage = null
        }
    }
}

/**
 * 新建触发器对话框
 *
 * 包含名称、描述、备注三个输入字段
 *
 * @param onDismiss 取消回调
 * @param onConfirm 确认回调，传递表单数据
 */
@Composable
private fun CreateTriggerDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, description: String?, remark: String?) -> Unit
) {
    // 表单状态
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var remark by remember { mutableStateOf("") }

    FormDialog(
        title = "新建触发器",
        onDismiss = onDismiss,
        onConfirm = {
            onConfirm(
                name,
                description.ifBlank { null },
                remark.ifBlank { null }
            )
        },
        confirmEnabled = name.isNotBlank()  // 名称为必填项
    ) {
        // 名称输入框（必填）
        FormTextField(
            value = name,
            onValueChange = { name = it },
            label = "名称 *"
        )
        Spacer(modifier = Modifier.height(16.dp))
        // 描述输入框（可选）
        FormTextField(
            value = description,
            onValueChange = { description = it },
            label = "描述"
        )
        Spacer(modifier = Modifier.height(16.dp))
        // 备注输入框（可选）
        FormTextField(
            value = remark,
            onValueChange = { remark = it },
            label = "备注"
        )
    }
}
