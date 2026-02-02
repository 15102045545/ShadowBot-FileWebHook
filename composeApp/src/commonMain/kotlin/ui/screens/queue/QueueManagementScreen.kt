/**
 * 队列管理页面
 *
 * 本文件提供队列监控的主界面
 * 实时显示当前队列中的任务信息，每秒自动刷新
 */

package ui.screens.queue

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import domain.queue.TriggerTask
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import ui.components.*

/**
 * 队列管理页面组件
 *
 * 显示当前任务队列状态，包括：
 * - 正在执行的任务（如果有）
 * - 待处理的任务列表
 *
 * 数据通过 StateFlow 实时更新
 *
 * @param viewModel 队列管理 ViewModel
 * @param modifier 修饰符
 */
@Composable
fun QueueManagementScreen(
    viewModel: QueueManagementViewModel,
    modifier: Modifier = Modifier
) {
    // 从 ViewModel 获取状态
    val pendingTasks by viewModel.pendingTasks
    val currentTask by viewModel.currentTask
    val queueSize by viewModel.queueSize

    // 页面进入时开始收集数据，离开时停止
    DisposableEffect(Unit) {
        viewModel.startCollecting()
        onDispose {
            viewModel.stopCollecting()
        }
    }

    Column(modifier = modifier.fillMaxSize().padding(24.dp)) {
        // 页面顶部：标题和队列大小
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "队列管理",
                style = MaterialTheme.typography.headlineMedium
            )

            // 队列状态指示
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = "队列中任务数: $queueSize",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 当前执行中的任务
        Text(
            text = "当前执行中",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (currentTask != null) {
            CurrentTaskCard(task = currentTask!!)
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "当前没有正在执行的任务",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 待处理任务列表
        Text(
            text = "待处理队列",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (pendingTasks.isEmpty()) {
            // 空状态提示
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "队列为空，没有等待处理的任务",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            // 待处理任务表格
            DataTable(
                items = pendingTasks,
                columns = listOf(
                    // 序号列
                    TableColumn(header = "序号", weight = 0.5f) { task ->
                        val index = pendingTasks.indexOf(task) + 1
                        TableCellText(index.toString())
                    },
                    // 事件 ID 列
                    TableColumn(header = "事件ID", weight = 1.5f) { task ->
                        TableCellText(task.eventId)
                    },
                    // 触发器 ID 列
                    TableColumn(header = "触发器ID", weight = 0.8f) { task ->
                        TableCellText(task.triggerId.toString())
                    },
                    // 用户 ID 列
                    TableColumn(header = "用户ID", weight = 1f) { task ->
                        TableCellText(task.userId)
                    },
                    // 请求时间列
                    TableColumn(header = "请求时间", weight = 1.2f) { task ->
                        TableCellText(task.requestTime.take(19).replace("T", " "))
                    },
                    // 入队时间列
                    TableColumn(header = "入队时间", weight = 1.2f) { task ->
                        TableCellText(formatInstant(task.enqueueTime))
                    },
                    // 回调地址列
                    TableColumn(header = "回调地址", weight = 1.5f) { task ->
                        TableCellText(task.callbackUrl)
                    }
                )
            )
        }
    }
}

/**
 * 当前任务卡片组件
 *
 * 显示当前正在执行的任务详细信息
 *
 * @param task 当前任务
 */
@Composable
private fun CurrentTaskCard(task: TriggerTask) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 第一行：事件 ID 和状态
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "事件ID: ${task.eventId}",
                    style = MaterialTheme.typography.titleSmall
                )
                StatusChip(text = "执行中", isSuccess = true)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 详细信息网格
            Row(modifier = Modifier.fillMaxWidth()) {
                // 左侧列
                Column(modifier = Modifier.weight(1f)) {
                    TaskInfoRow(label = "触发器ID", value = task.triggerId.toString())
                    Spacer(modifier = Modifier.height(8.dp))
                    TaskInfoRow(label = "用户ID", value = task.userId)
                }
                // 右侧列
                Column(modifier = Modifier.weight(1f)) {
                    TaskInfoRow(label = "请求时间", value = task.requestTime.take(19).replace("T", " "))
                    Spacer(modifier = Modifier.height(8.dp))
                    TaskInfoRow(label = "入队时间", value = formatInstant(task.enqueueTime))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 回调地址（独占一行）
            TaskInfoRow(label = "回调地址", value = task.callbackUrl)
        }
    }
}

/**
 * 任务信息行组件
 *
 * 显示单个键值对信息
 *
 * @param label 标签
 * @param value 值
 */
@Composable
private fun TaskInfoRow(label: String, value: String) {
    Row {
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.bodySmall,
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
