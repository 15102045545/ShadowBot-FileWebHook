/**
 * 系统设置页面
 *
 * 本文件提供系统设置的配置界面
 * 包含服务控制、基本配置和身份配置三个部分
 */

package ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import data.model.AppSettings
import kotlinx.coroutines.launch
import ui.components.FormTextField
import ui.theme.ShadowBotError
import ui.theme.ShadowBotSuccess

/**
 * 系统设置页面组件
 *
 * 提供系统配置的完整管理界面：
 * - 服务状态卡片：显示/控制 HTTP 服务器
 * - 基本配置：触发器文件路径、队列长度、HTTP 端口
 * - 身份配置：FileWebHook 名称和密钥
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
    val isLoading by viewModel.isLoading
    val serverRunning by viewModel.serverRunning
    val scope = rememberCoroutineScope()

    // 表单状态（从当前设置初始化）
    var triggerFilesPath by remember(settings) { mutableStateOf(settings.triggerFilesPath) }
    var queueMaxLength by remember(settings) { mutableStateOf(settings.globalQueueMaxLength.toString()) }
    var fileWebHookName by remember(settings) { mutableStateOf(settings.fileWebHookName) }
    var fileWebHookSecretKey by remember(settings) { mutableStateOf(settings.fileWebHookSecretKey) }
    var httpPort by remember(settings) { mutableStateOf(settings.httpPort.toString()) }

    // UI 状态
    var snackbarMessage by remember { mutableStateOf<String?>(null) }
    var hasChanges by remember { mutableStateOf(false) }

    // 页面加载时获取数据
    LaunchedEffect(Unit) {
        viewModel.loadSettings()
    }

    // 追踪表单变更（用于启用/禁用保存按钮）
    LaunchedEffect(triggerFilesPath, queueMaxLength, fileWebHookName, fileWebHookSecretKey, httpPort) {
        hasChanges = triggerFilesPath != settings.triggerFilesPath ||
                queueMaxLength != settings.globalQueueMaxLength.toString() ||
                fileWebHookName != settings.fileWebHookName ||
                fileWebHookSecretKey != settings.fileWebHookSecretKey ||
                httpPort != settings.httpPort.toString()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // 页面标题
        Text(
            text = "软件配置",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

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

                    // 启动/停止/重启按钮
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (serverRunning) {
                            // 重启按钮
                            Button(
                                onClick = {
                                    scope.launch {
                                        viewModel.restartServer()
                                        snackbarMessage = "服务已重启"
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.tertiary
                                )
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("重启服务")
                            }
                            // 停止按钮
                            Button(
                                onClick = { viewModel.stopServer() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error
                                )
                            ) {
                                Icon(Icons.Default.Close, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("停止服务")
                            }
                        } else {
                            Button(
                                onClick = {
                                    scope.launch {
                                        viewModel.startServer()
                                        snackbarMessage = "服务已启动"
                                    }
                                }
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("启动服务")
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ==========================================
        // 基本配置卡片
        // ==========================================
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "基本配置",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 触发器文件路径
                FormTextField(
                    value = triggerFilesPath,
                    onValueChange = { triggerFilesPath = it },
                    label = "触发器文件路径",
                    supportingText = "影刀文件触发器监听的目录"
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 队列最大长度
                FormTextField(
                    value = queueMaxLength,
                    onValueChange = { queueMaxLength = it },
                    label = "队列最大长度",
                    supportingText = "全局任务队列的最大容量"
                )

                Spacer(modifier = Modifier.height(16.dp))

                // HTTP 端口
                FormTextField(
                    value = httpPort,
                    onValueChange = { httpPort = it },
                    label = "HTTP 端口",
                    supportingText = "服务监听端口，修改后需重启服务"
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ==========================================
        // 身份配置卡片
        // ==========================================
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "身份配置",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                // FileWebHook 名称
                FormTextField(
                    value = fileWebHookName,
                    onValueChange = { fileWebHookName = it },
                    label = "FileWebHook 名称 *",
                    supportingText = "本机标识，用于回调外部服务时的身份识别"
                )

                Spacer(modifier = Modifier.height(16.dp))

                // FileWebHook 密钥
                FormTextField(
                    value = fileWebHookSecretKey,
                    onValueChange = { fileWebHookSecretKey = it },
                    label = "FileWebHook 密钥 *",
                    supportingText = "回调外部服务时携带的密钥，用于验证身份"
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ==========================================
        // 保存按钮
        // ==========================================
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                onClick = {
                    scope.launch {
                        // 构建新的设置对象
                        val newSettings = AppSettings(
                            triggerFilesPath = triggerFilesPath,
                            globalQueueMaxLength = queueMaxLength.toIntOrNull() ?: 50,
                            fileWebHookName = fileWebHookName,
                            fileWebHookSecretKey = fileWebHookSecretKey,
                            httpPort = httpPort.toIntOrNull() ?: 8089
                        )
                        // 保存设置
                        val result = viewModel.saveSettings(newSettings)
                        snackbarMessage = if (result.isSuccess) "保存成功" else "保存失败"
                        hasChanges = false
                    }
                },
                enabled = hasChanges  // 只有有变更时才能保存
            ) {
                Text("保存设置")
            }
        }
    }
}
