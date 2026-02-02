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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.ContentCopy
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
import ui.theme.ShadowBotPink

/**
 * 触发器列表页面组件
 *
 * 显示触发器列表，支持：
 * - 查看触发器信息（ID、名称、描述、文件夹路径、创建时间）
 * - 复制文件夹路径到剪贴板
 * - 复制 FileWebHook-App-Framework 框架指令到剪贴板
 * - 新建触发器
 * - 编辑触发器
 * - 删除触发器
 * - 查看触发器执行记录
 *
 * @param viewModel 触发器 ViewModel
 * @param onViewExecutionLogs 查看执行记录回调
 * @param modifier 修饰符
 */
@Composable
fun TriggerListScreen(
    viewModel: TriggerViewModel,
    onViewExecutionLogs: (triggerId: Long) -> Unit = {},
    modifier: Modifier = Modifier
) {
    // 从 ViewModel 获取状态
    val triggers by viewModel.triggers
    val isLoading by viewModel.isLoading
    val copyFrameworkMessage by viewModel.copyFrameworkMessage
    val copyFrameworkSuccess by viewModel.copyFrameworkSuccess
    val isCopyingFramework by viewModel.isCopyingFramework
    val scope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current

    // 项目根目录路径
    val projectRootPath = remember { System.getProperty("user.dir") ?: "" }

    // 本地 UI 状态
    var showCreateDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf<Trigger?>(null) }
    var showDeleteDialog by remember { mutableStateOf<Trigger?>(null) }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }

    // 页面加载时获取数据
    LaunchedEffect(Unit) {
        viewModel.loadTriggers()
    }

    // 监听复制框架指令的结果消息
    LaunchedEffect(copyFrameworkMessage) {
        copyFrameworkMessage?.let { message ->
            snackbarMessage = message
            viewModel.clearCopyFrameworkMessage()
        }
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
                    },
                    // 操作列（增加宽度以容纳新按钮）
                    TableColumn(header = "操作", weight = 1.8f) { trigger ->
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            // 复制框架指令按钮
                            IconButton(
                                onClick = {
                                    scope.launch {
                                        viewModel.copyFrameworkInstruction(trigger.id, projectRootPath)
                                    }
                                },
                                enabled = !isCopyingFramework
                            ) {
                                Icon(
                                    Icons.Default.ContentCopy,
                                    contentDescription = "复制框架指令",
                                    modifier = Modifier.size(16.dp),
                                    tint = ShadowBotPink
                                )
                            }
                            // 查看记录按钮
                            IconButton(
                                onClick = { onViewExecutionLogs(trigger.id) }
                            ) {
                                Icon(
                                    Icons.Default.Visibility,
                                    contentDescription = "查看记录",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            // 编辑按钮
                            IconButton(
                                onClick = { showEditDialog = trigger }
                            ) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "编辑",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            // 删除按钮
                            IconButton(
                                onClick = { showDeleteDialog = trigger }
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

    // 编辑触发器对话框
    showEditDialog?.let { trigger ->
        EditTriggerDialog(
            trigger = trigger,
            onDismiss = { showEditDialog = null },
            onConfirm = { name, description, remark ->
                scope.launch {
                    val result = viewModel.updateTrigger(trigger.id, name, description, remark)
                    if (result.isSuccess) {
                        snackbarMessage = "更新成功"
                    } else {
                        snackbarMessage = "更新失败: ${result.exceptionOrNull()?.message}"
                    }
                }
                showEditDialog = null
            }
        )
    }

    // 删除确认对话框
    showDeleteDialog?.let { trigger ->
        ConfirmDeleteDialog(
            triggerName = trigger.name,
            onDismiss = { showDeleteDialog = null },
            onConfirm = {
                scope.launch {
                    val result = viewModel.deleteTrigger(trigger.id)
                    if (result.isSuccess) {
                        snackbarMessage = "删除成功"
                    } else {
                        snackbarMessage = "删除失败: ${result.exceptionOrNull()?.message}"
                    }
                }
                showDeleteDialog = null
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

/**
 * 编辑触发器对话框
 *
 * 包含名称、描述、备注三个输入字段，初始值为当前触发器的值
 *
 * @param trigger 要编辑的触发器
 * @param onDismiss 取消回调
 * @param onConfirm 确认回调，传递表单数据
 */
@Composable
private fun EditTriggerDialog(
    trigger: Trigger,
    onDismiss: () -> Unit,
    onConfirm: (name: String, description: String?, remark: String?) -> Unit
) {
    // 表单状态，初始化为当前触发器的值
    var name by remember { mutableStateOf(trigger.name) }
    var description by remember { mutableStateOf(trigger.description ?: "") }
    var remark by remember { mutableStateOf(trigger.remark ?: "") }

    FormDialog(
        title = "编辑触发器",
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

/**
 * 删除确认对话框
 *
 * @param triggerName 触发器名称
 * @param onDismiss 取消回调
 * @param onConfirm 确认回调
 */
@Composable
private fun ConfirmDeleteDialog(
    triggerName: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("确认删除") },
        text = {
            Text("确定要删除触发器 \"$triggerName\" 吗？\n\n注意：删除后不会影响已产生的执行记录，但触发器文件夹需要手动清理。")
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("删除")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
