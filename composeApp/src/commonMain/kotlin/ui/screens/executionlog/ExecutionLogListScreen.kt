/**
 * 执行记录列表页面
 *
 * 本文件提供执行记录管理的主界面
 * 包含执行记录列表展示、多条件筛选和分页功能
 */

package ui.screens.executionlog

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import data.model.ExecutionLog
import data.model.ExecutionStatus
import kotlinx.coroutines.launch
import ui.components.*

/**
 * 执行记录列表页面组件
 *
 * 显示执行记录列表，支持：
 * - 查看执行记录信息（事件ID、触发器名称、用户ID、状态、请求时间等）
 * - 多条件筛选（触发器、用户、状态、事件ID、时间区间）
 * - 分页显示
 *
 * @param viewModel 执行记录 ViewModel
 * @param initialTriggerId 初始筛选的触发器 ID（可选）
 * @param modifier 修饰符
 */
@Composable
fun ExecutionLogListScreen(
    viewModel: ExecutionLogViewModel,
    initialTriggerId: Long? = null,
    modifier: Modifier = Modifier
) {
    // 从 ViewModel 获取状态
    val executionLogs by viewModel.executionLogs
    val triggers by viewModel.triggers
    val users by viewModel.users
    val isLoading by viewModel.isLoading
    val currentPage by viewModel.currentPage
    val totalCount by viewModel.totalCount
    val totalPages = viewModel.totalPages
    val hasFilters = viewModel.hasFilters()
    val scope = rememberCoroutineScope()

    // 本地 UI 状态
    var showFilterDialog by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }

    // 页面加载时初始化数据
    LaunchedEffect(Unit) {
        viewModel.initialize()
        if (initialTriggerId != null) {
            viewModel.setSelectedTriggerId(initialTriggerId)
        } else {
            viewModel.loadExecutionLogs()
        }
    }

    Column(modifier = modifier.fillMaxSize().padding(24.dp)) {
        // 页面顶部：标题和操作按钮
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "触发器记录管理",
                    style = MaterialTheme.typography.headlineMedium
                )
                // 显示记录统计信息
                Text(
                    text = "共 $totalCount 条记录，第 $currentPage / $totalPages 页",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // 清除筛选按钮
                if (hasFilters) {
                    TextButton(
                        onClick = {
                            scope.launch {
                                viewModel.clearFilters()
                            }
                        }
                    ) {
                        Text("清除筛选")
                    }
                }
                // 筛选按钮
                Button(
                    onClick = { showFilterDialog = true }
                ) {
                    Icon(Icons.Default.FilterAlt, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("筛选")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 显示当前筛选条件
        if (hasFilters) {
            FilterChips(viewModel)
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 页面主体：执行记录列表
        if (isLoading) {
            // 加载中状态
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (executionLogs.isEmpty()) {
            // 空状态提示
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = if (hasFilters) "没有符合条件的记录" else "暂无执行记录",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            // 执行记录数据表格
            Box(modifier = Modifier.weight(1f)) {
                DataTable(
                    items = executionLogs,
                    columns = listOf(
                        // 事件 ID 列
                        TableColumn(header = "事件ID", weight = 1.2f) { log ->
                            TableCellText(log.eventId)
                        },
                        // 触发器名称列
                        TableColumn(header = "触发器", weight = 0.8f) { log ->
                            val triggerName = viewModel.getTriggerNameById(log.triggerId) ?: "ID:${log.triggerId}"
                            TableCellText(triggerName)
                        },
                        // 用户名称列
                        TableColumn(header = "用户", weight = 0.8f) { log ->
                            val userName = viewModel.getUserNameById(log.userId) ?: log.userId
                            TableCellText(userName)
                        },
                        // 状态列
                        TableColumn(header = "状态", weight = 0.8f) { log ->
                            ExecutionStatusChip(log.status)
                        },
                        // 请求时间列
                        TableColumn(header = "请求时间", weight = 1.2f) { log ->
                            TableCellText(log.requestTime.take(19).replace("T", " "))
                        },
                        // 总耗时列
                        TableColumn(header = "总耗时(ms)", weight = 0.8f) { log ->
                            TableCellText(log.totalDuration?.toString() ?: "-")
                        },
                        // 影刀耗时列
                        TableColumn(header = "影刀耗时(ms)", weight = 0.8f) { log ->
                            TableCellText(log.shadowbotDuration?.toString() ?: "-")
                        },
                        // 响应码列
                        TableColumn(header = "响应码", weight = 0.6f) { log ->
                            TableCellText(log.responseCode ?: "-")
                        }
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 分页控件
            PaginationControls(
                currentPage = currentPage,
                totalPages = totalPages,
                onPreviousPage = { scope.launch { viewModel.previousPage() } },
                onNextPage = { scope.launch { viewModel.nextPage() } }
            )
        }
    }

    // 筛选对话框
    if (showFilterDialog) {
        FilterDialog(
            viewModel = viewModel,
            triggers = triggers,
            users = users,
            onDismiss = { showFilterDialog = false },
            onConfirm = { triggerId, userId, status, eventId, startTime, endTime ->
                scope.launch {
                    viewModel.applyFilters(triggerId, userId, status, eventId, startTime, endTime)
                }
                showFilterDialog = false
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
 * 筛选条件芯片组件
 *
 * 显示当前应用的筛选条件
 */
@Composable
private fun FilterChips(viewModel: ExecutionLogViewModel) {
    val selectedTriggerId by viewModel.selectedTriggerId.collectAsState()
    val selectedUserId by viewModel.selectedUserId.collectAsState()
    val selectedStatus by viewModel.selectedStatus.collectAsState()
    val selectedEventId by viewModel.selectedEventId.collectAsState()
    val startTime by viewModel.startTime.collectAsState()
    val endTime by viewModel.endTime.collectAsState()

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("当前筛选：", style = MaterialTheme.typography.bodyMedium)

        selectedTriggerId?.let { id ->
            FilterChip(
                selected = true,
                onClick = {},
                label = { Text("触发器: ${viewModel.getTriggerNameById(id)}") }
            )
        }

        selectedUserId?.let { id ->
            FilterChip(
                selected = true,
                onClick = {},
                label = { Text("用户: ${viewModel.getUserNameById(id)}") }
            )
        }

        selectedStatus?.let { status ->
            FilterChip(
                selected = true,
                onClick = {},
                label = { Text("状态: ${status.displayName}") }
            )
        }

        selectedEventId?.takeIf { it.isNotBlank() }?.let { id ->
            FilterChip(
                selected = true,
                onClick = {},
                label = { Text("事件ID: $id") }
            )
        }

        if (startTime != null || endTime != null) {
            FilterChip(
                selected = true,
                onClick = {},
                label = {
                    val timeRange = when {
                        startTime != null && endTime != null -> "$startTime ~ $endTime"
                        startTime != null -> ">= $startTime"
                        else -> "<= $endTime"
                    }
                    Text("时间: $timeRange")
                }
            )
        }
    }
}

/**
 * 分页控件组件
 */
@Composable
private fun PaginationControls(
    currentPage: Int,
    totalPages: Int,
    onPreviousPage: () -> Unit,
    onNextPage: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 上一页按钮
        IconButton(
            onClick = onPreviousPage,
            enabled = currentPage > 1
        ) {
            Icon(Icons.Default.NavigateBefore, contentDescription = "上一页")
        }

        Spacer(modifier = Modifier.width(16.dp))

        // 页码显示
        Text(
            text = "$currentPage / $totalPages",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.width(16.dp))

        // 下一页按钮
        IconButton(
            onClick = onNextPage,
            enabled = currentPage < totalPages
        ) {
            Icon(Icons.Default.NavigateNext, contentDescription = "下一页")
        }
    }
}

/**
 * 执行状态芯片组件
 *
 * 根据状态显示不同颜色的徽章
 */
@Composable
private fun ExecutionStatusChip(status: ExecutionStatus) {
    val color = when (status) {
        ExecutionStatus.REQUESTED, ExecutionStatus.QUEUED -> MaterialTheme.colorScheme.secondary
         ExecutionStatus.FILE_WRITTEN -> MaterialTheme.colorScheme.tertiary
        ExecutionStatus.EXECUTING -> MaterialTheme.colorScheme.primary
        ExecutionStatus.COMPLETED, ExecutionStatus.CALLBACK_SUCCESS -> MaterialTheme.colorScheme.primary
        ExecutionStatus.CALLBACK_FAILED -> MaterialTheme.colorScheme.error
    }

    val text = when (status) {
        ExecutionStatus.REQUESTED -> "已请求"
        ExecutionStatus.QUEUED -> "已入队"
        ExecutionStatus.FILE_WRITTEN -> "文件已写"
        ExecutionStatus.EXECUTING -> "执行中"
        ExecutionStatus.COMPLETED -> "已完成"
        ExecutionStatus.CALLBACK_SUCCESS -> "回调成功"
        ExecutionStatus.CALLBACK_FAILED -> "回调失败"
    }

    Surface(
        color = color.copy(alpha = 0.1f),
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.padding(4.dp)
    ) {
        Text(
            text = text,
            color = color,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

/**
 * 筛选对话框
 *
 * 允许用户设置多个筛选条件
 */
@Composable
private fun FilterDialog(
    viewModel: ExecutionLogViewModel,
    triggers: List<data.model.Trigger>,
    users: List<data.model.User>,
    onDismiss: () -> Unit,
    onConfirm: (
        triggerId: Long?,
        userId: String?,
        status: ExecutionStatus?,
        eventId: String?,
        startTime: String?,
        endTime: String?
    ) -> Unit
) {
    // 获取当前筛选条件
    val currentTriggerId by viewModel.selectedTriggerId.collectAsState()
    val currentUserId by viewModel.selectedUserId.collectAsState()
    val currentStatus by viewModel.selectedStatus.collectAsState()
    val currentEventId by viewModel.selectedEventId.collectAsState()
    val currentStartTime by viewModel.startTime.collectAsState()
    val currentEndTime by viewModel.endTime.collectAsState()

    // 对话框内的状态
    var selectedTriggerId by remember { mutableStateOf(currentTriggerId) }
    var selectedUserId by remember { mutableStateOf(currentUserId) }
    var selectedStatus by remember { mutableStateOf(currentStatus) }
    var eventIdInput by remember { mutableStateOf(currentEventId ?: "") }
    var startTimeInput by remember { mutableStateOf(currentStartTime ?: "") }
    var endTimeInput by remember { mutableStateOf(currentEndTime ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("筛选条件") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 触发器筛选
                Text("触发器：", style = MaterialTheme.typography.bodyMedium)
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedTriggerId == null,
                            onClick = { selectedTriggerId = null }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("全部")
                    }
                    triggers.forEach { trigger ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = selectedTriggerId == trigger.id,
                                onClick = { selectedTriggerId = trigger.id }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("${trigger.name} (ID: ${trigger.id})")
                        }
                    }
                }

                Divider()

                // 用户筛选
                Text("用户：", style = MaterialTheme.typography.bodyMedium)
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedUserId == null,
                            onClick = { selectedUserId = null }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("全部")
                    }
                    users.forEach { user ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = selectedUserId == user.userId,
                                onClick = { selectedUserId = user.userId }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("${user.name}")
                        }
                    }
                }

                Divider()

                // 状态筛选
                Text("状态：", style = MaterialTheme.typography.bodyMedium)
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedStatus == null,
                            onClick = { selectedStatus = null }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("全部")
                    }
                    ExecutionStatus.values().forEach { status ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = selectedStatus == status,
                                onClick = { selectedStatus = status }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(status.displayName)
                        }
                    }
                }

                Divider()

                // 事件ID筛选
                Text("事件ID（模糊匹配）：", style = MaterialTheme.typography.bodyMedium)
                OutlinedTextField(
                    value = eventIdInput,
                    onValueChange = { eventIdInput = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("输入事件ID关键字") }
                )

                Divider()

                // 时间区间筛选
                Text("时间区间：", style = MaterialTheme.typography.bodyMedium)
                OutlinedTextField(
                    value = startTimeInput,
                    onValueChange = { startTimeInput = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("开始时间") },
                    placeholder = { Text("例如: 2024-01-01 00:00:00") }
                )
                OutlinedTextField(
                    value = endTimeInput,
                    onValueChange = { endTimeInput = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("结束时间") },
                    placeholder = { Text("例如: 2024-12-31 23:59:59") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        selectedTriggerId,
                        selectedUserId,
                        selectedStatus,
                        eventIdInput.ifBlank { null },
                        startTimeInput.ifBlank { null },
                        endTimeInput.ifBlank { null }
                    )
                }
            ) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
