/**
 * 核心状态页面
 *
 * 本文件提供核心状态的统一展示界面
 * 采用单页面下拉形式展示所有核心指标，包括服务状态、队列信息、系统配置等
 */

package ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import data.model.AppSettings
import domain.queue.TriggerTask
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import ui.components.*
import ui.theme.ShadowBotError
import ui.theme.ShadowBotPink
import ui.theme.ShadowBotSuccess

/**
 * 核心状态页面组件
 *
 * 采用单页面下拉形式展示所有核心指标：
 * - 服务状态卡片
 * - 队列状态卡片（当前任务、待处理数量）
 * - 核心配置卡片（包含基本配置、身份配置、Python 配置）
 * - 运行时数据卡片
 * - 关于卡片
 *
 * @param viewModel 设置 ViewModel
 * @param modifier 修饰符
 */
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    // 从 ViewModel 获取状态
    val settings by viewModel.settings
    val serverRunning by viewModel.serverRunning
    val detectedPythonPath by viewModel.detectedPythonPath
    val pythonDetectionMessage by viewModel.pythonDetectionMessage
    val scope = rememberCoroutineScope()
    val uriHandler = LocalUriHandler.current

    // 队列相关状态
    val pendingTasks by viewModel.pendingTasks
    val currentTask by viewModel.currentTask
    val queueSize by viewModel.queueSize

    // 表单状态（从当前设置初始化）
    var queueMaxLength by remember(settings) { mutableStateOf(settings.globalQueueMaxLength.toString()) }
    var fileWebHookName by remember(settings) { mutableStateOf(settings.fileWebHookName) }
    var fileWebHookSecretKey by remember(settings) { mutableStateOf(settings.fileWebHookSecretKey) }
    var httpPort by remember(settings) { mutableStateOf(settings.httpPort.toString()) }
    var pythonInterpreterPath by remember(settings) { mutableStateOf(settings.pythonInterpreterPath) }

    // UI 状态
    var snackbarMessage by remember { mutableStateOf<String?>(null) }
    var hasChanges by remember { mutableStateOf(false) }
    var showClearDataDialog by remember { mutableStateOf(false) }

    // 页面加载时获取数据和开始收集队列数据
    LaunchedEffect(Unit) {
        viewModel.loadSettings()
        viewModel.detectPythonInterpreter()
        viewModel.startQueueDataCollection()
    }

    // 页面离开时停止收集队列数据
    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopQueueDataCollection()
        }
    }

    // 追踪表单变更（用于启用/禁用保存按钮）
    LaunchedEffect(queueMaxLength, fileWebHookName, fileWebHookSecretKey, httpPort, pythonInterpreterPath) {
        hasChanges = queueMaxLength != settings.globalQueueMaxLength.toString() ||
                fileWebHookName != settings.fileWebHookName ||
                fileWebHookSecretKey != settings.fileWebHookSecretKey ||
                httpPort != settings.httpPort.toString() ||
                pythonInterpreterPath != settings.pythonInterpreterPath
    }

    // 清空数据确认对话框
    if (showClearDataDialog) {
        AlertDialog(
            onDismissRequest = { showClearDataDialog = false },
            title = { Text("确认清空运行数据") },
            text = {
                Text("你确定要清空运行数据目录吗？这会导致您的用户数据、触发器数据、记录等全部删除，该操作不可逆！\n\n当您确认后，软件将会关闭，请您重新启动软件。")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearRuntimeDataAndExit()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("确认清空并退出")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showClearDataDialog = false }) {
                    Text("取消")
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // ==========================================
        // 服务状态卡片
        // ==========================================
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "HTTP 服务状态",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        // 状态指示器（绿色运行中/红色已停止）
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                modifier = Modifier.size(8.dp),
                                shape = MaterialTheme.shapes.small,
                                color = if (serverRunning) ShadowBotSuccess else ShadowBotError
                            ) {}
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (serverRunning) "运行中 - 端口 ${settings.httpPort}" else "已停止",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ==========================================
        // 队列状态卡片
        // ==========================================
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // 标题行：队列状态 + 任务数
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "队列状态",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = "待处理: $queueSize",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 当前执行中的任务
                if (currentTask != null) {
                    CurrentTaskCompactCard(task = currentTask!!)
                } else {
                    // 空状态
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = "当前没有正在执行的任务",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                // 待处理任务简要列表（最多显示3个）
                if (pendingTasks.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "待处理队列",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    pendingTasks.take(3).forEachIndexed { index, task ->
                        PendingTaskCompactRow(index = index + 1, task = task)
                        if (index < minOf(pendingTasks.size - 1, 2)) {
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }

                    // 如果超过3个，显示提示
                    if (pendingTasks.size > 3) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "还有 ${pendingTasks.size - 3} 个任务等待处理...",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ==========================================
        // 核心配置卡片（简化配置项说明）
        // ==========================================
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "核心配置",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                // HTTP 端口（将说明合并到标签）
                FormTextField(
                    value = httpPort,
                    onValueChange = { httpPort = it },
                    label = "HTTP 端口（修改后需重启应用生效）"
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 队列最大长度
                FormTextField(
                    value = queueMaxLength,
                    onValueChange = { queueMaxLength = it },
                    label = "队列最大长度（全局任务队列的最大容量）"
                )

                Spacer(modifier = Modifier.height(16.dp))

                HorizontalDivider()

                Spacer(modifier = Modifier.height(16.dp))

                // FileWebHook 名称
                FormTextField(
                    value = fileWebHookName,
                    onValueChange = { fileWebHookName = it },
                    label = "FileWebHook 名称（回调外部服务时的身份标识）"
                )

                Spacer(modifier = Modifier.height(16.dp))

                // FileWebHook 密钥
                FormTextField(
                    value = fileWebHookSecretKey,
                    onValueChange = { fileWebHookSecretKey = it },
                    label = "FileWebHook 密钥（回调外部服务时的验证密钥）"
                )

                Spacer(modifier = Modifier.height(16.dp))

                HorizontalDivider()

                Spacer(modifier = Modifier.height(16.dp))

                // Python 解释器路径
                FormTextField(
                    value = pythonInterpreterPath,
                    onValueChange = { pythonInterpreterPath = it },
                    label = "Python 解释器路径（留空则自动检测影刀内置 Python）"
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 自动检测按钮和结果
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 检测结果
                    Column(modifier = Modifier.weight(1f)) {
                        if (detectedPythonPath.isNotBlank()) {
                            Text(
                                text = "检测到: $detectedPythonPath",
                                style = MaterialTheme.typography.bodySmall,
                                color = ShadowBotSuccess
                            )
                        }
                        pythonDetectionMessage?.let { message ->
                            Text(
                                text = message,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (detectedPythonPath.isBlank()) ShadowBotError else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // 自动检测按钮
                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                viewModel.detectPythonInterpreter()
                            }
                        }
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("自动检测")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // 使用检测到的路径
                    if (detectedPythonPath.isNotBlank()) {
                        Button(
                            onClick = {
                                pythonInterpreterPath = detectedPythonPath
                            }
                        ) {
                            Text("使用检测路径")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 保存按钮
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            scope.launch {
                                val newSettings = AppSettings(
                                    globalQueueMaxLength = queueMaxLength.toIntOrNull() ?: 50,
                                    fileWebHookName = fileWebHookName,
                                    fileWebHookSecretKey = fileWebHookSecretKey,
                                    httpPort = httpPort.toIntOrNull() ?: 8089,
                                    pythonInterpreterPath = pythonInterpreterPath
                                )
                                val result = viewModel.saveSettings(newSettings)
                                snackbarMessage = if (result.isSuccess) "保存成功" else "保存失败"
                                hasChanges = false
                            }
                        },
                        enabled = hasChanges
                    ) {
                        Text("保存设置")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ==========================================
        // 运行时数据卡片
        // ==========================================
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "运行时数据",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 数据目录路径
                Text(
                    text = "数据目录：${viewModel.getRuntimeDataPath()}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 按钮行
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 打开数据目录按钮
                    OutlinedButton(
                        onClick = { viewModel.openRuntimeDataDirectory() }
                    ) {
                        Icon(Icons.Default.FolderOpen, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("打开运行数据目录")
                    }

                    // 清空数据目录按钮
                    Button(
                        onClick = { showClearDataDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("清空运行数据目录")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ==========================================
        // 关于卡片
        // ==========================================
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "关于",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "FileWebHook",
                    style = MaterialTheme.typography.titleLarge,
                    color = ShadowBotPink
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "影刀 RPA 文件触发器中间件",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "© 2024-2025 xiaokeer. All rights reserved.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                // GitHub 链接
                Text(
                    text = "GitHub: https://github.com/15102045545/ShadowBot-FileWebHook",
                    style = MaterialTheme.typography.bodySmall.copy(
                        textDecoration = TextDecoration.Underline
                    ),
                    color = ShadowBotPink,
                    modifier = Modifier.clickable {
                        uriHandler.openUri("https://github.com/15102045545/ShadowBot-FileWebHook")
                    }
                )
            }
        }
    }
}

/**
 * 当前任务紧凑卡片组件
 *
 * 以紧凑形式显示当前正在执行的任务
 *
 * @param task 当前任务
 */
@Composable
private fun CurrentTaskCompactCard(task: TriggerTask) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
        shape = MaterialTheme.shapes.small
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // 第一行：执行中标签 + 事件 ID
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // 执行中指示器
                    Surface(
                        modifier = Modifier.size(8.dp),
                        shape = MaterialTheme.shapes.small,
                        color = ShadowBotSuccess
                    ) {}
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "执行中",
                        style = MaterialTheme.typography.labelMedium,
                        color = ShadowBotSuccess
                    )
                }
                Text(
                    text = task.eventId,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 第二行：关键信息
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TaskInfoCompact(label = "触发器", value = task.triggerId.toString())
                TaskInfoCompact(label = "用户", value = task.userId)
                TaskInfoCompact(label = "请求时间", value = task.requestTime.take(16).replace("T", " "))
            }
        }
    }
}

/**
 * 待处理任务紧凑行组件
 *
 * 以单行紧凑形式显示待处理任务
 *
 * @param index 序号
 * @param task 任务
 */
@Composable
private fun PendingTaskCompactRow(index: Int, task: TriggerTask) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 序号
            Text(
                text = "#$index",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.width(32.dp)
            )
            // 触发器 ID
            Text(
                text = "触发器: ${task.triggerId}",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.weight(1f)
            )
            // 用户 ID
            Text(
                text = task.userId,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            // 请求时间
            Text(
                text = task.requestTime.take(16).replace("T", " "),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * 紧凑任务信息组件
 *
 * 显示单个标签-值对
 *
 * @param label 标签
 * @param value 值
 */
@Composable
private fun TaskInfoCompact(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

/**
 * 格式化 Instant 为可读时间字符串
 *
 * @param instant 时间戳
 * @return 格式化后的时间字符串
 */
private fun formatInstant(instant: Instant): String {
    return try {
        instant.toLocalDateTime(TimeZone.currentSystemDefault())
            .toString()
            .take(19)
            .replace("T", " ")
    } catch (e: Exception) {
        "-"
    }
}
