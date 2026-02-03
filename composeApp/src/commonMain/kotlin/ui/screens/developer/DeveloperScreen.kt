/**
 * 开发者功能页面
 *
 * 本文件提供开发者专用功能界面
 * 主要功能：
 * 1. 将系统剪贴板中的影刀指令转储到 BaseFileWebHookAppFramework.pkl 文件
 *
 * 注意：指令转储功能仅在开发环境（IDEA 运行）中可用，打包后的软件不包含此功能
 */

package ui.screens.developer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/**
 * 开发者功能页面组件
 *
 * 提供开发者专用功能界面：
 * - 指令转储：将剪贴板中的影刀指令保存到文件（仅开发环境可用）
 *
 * @param viewModel 开发者功能 ViewModel
 * @param modifier 修饰符
 */
@Composable
fun DeveloperScreen(
    viewModel: DeveloperViewModel,
    modifier: Modifier = Modifier
) {
    // 从 ViewModel 获取状态
    val isLoading by viewModel.isLoading
    val resultMessage by viewModel.resultMessage
    val isSuccess by viewModel.isSuccess
    val showResultDialog by viewModel.showResultDialog
    val scope = rememberCoroutineScope()

    // 检查是否为开发环境
    val isDevEnvironment = remember { viewModel.isDevEnvironment() }

    // 确认对话框状态
    var showConfirmDialog by remember { mutableStateOf(false) }

    // 确认对话框
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = {
                Text(text = "确认操作")
            },
            text = {
                Text(
                    text = "⚠\uFE0F你正在执行1个属于开发者的操作⚠\uFE0F" +
                        "\n该操作将读取您的剪切板并覆盖现有的元FileWebHook-APP-Framework指令文件，你确认要继续吗？\n\n" +
                            "可能带来的问题：\n" +
                            "1. 如果你的剪切板内容不是有效的FileWebHook-APP-Framework指令，或与当前FileWebHook版本不兼容，那么会导致触发器管理页面的复制指令功能无法使用"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirmDialog = false
                        scope.launch {
                            viewModel.dumpClipboardInstruction()
                        }
                    }
                ) {
                    Text("确认")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("取消")
                }
            }
        )
    }

    // 结果对话框
    if (showResultDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissResultDialog() },
            title = {
                Text(text = if (isSuccess) "转储成功" else "转储失败")
            },
            text = {
                Text(text = resultMessage ?: "")
            },
            confirmButton = {
                TextButton(onClick = { viewModel.dismissResultDialog() }) {
                    Text("确定")
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
        // 页面标题
        Text(
            text = "开发者功能",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 页面说明
        Text(
            text = "此页面仅供开发者使用，普通用户无需关注",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 如果不是开发环境，显示提示信息
        if (!isDevEnvironment) {
            // ==========================================
            // 非开发环境提示卡片
            // ==========================================
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "指令转储功能不可用",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "指令转储功能仅在开发环境（例如 IDEA 运行）中可用。\n打包后的软件不包含此功能。",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        } else {
            // ==========================================
            // 指令转储卡片（仅开发环境显示）
            // ==========================================
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "影刀指令转储",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "将系统剪贴板中的 FileWebHook-App-Framework 框架指令转储到项目文件中。",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 使用说明
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "使用方法：",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "1. 在影刀中复制 FileWebHook-App-Framework 框架指令",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "2. 点击下方\"转储指令\"按钮",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "3. 等待提示转储成功",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 转储按钮
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                // 显示确认对话框
                                showConfirmDialog = true
                            },
                            enabled = !isLoading
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("转储中...")
                            } else {
                                Icon(Icons.Default.ContentPaste, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("转储指令")
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ==========================================
            // 说明卡片
            // ==========================================
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "关于指令转储",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = """
                            转储后的指令文件将保存到项目资源目录：
                            composeApp/src/desktopMain/resources/scripts/BaseFileWebHookAppFramework.pkl

                            打包时，该文件会被包含到安装包中。
                            用户安装软件后，系统会自动从安装包提取最新的元指令文件。

                            用户在触发器管理页面点击"复制框架指令"时，
                            系统会读取此文件并动态替换部分属性值后写入剪贴板。

                            部分属性值：
                            • triggerId - 触发器ID
                            • triggerFilesPath - 触发器文件路径
                            • serverIpAndPort - 服务器IP和端口
                        """.trimIndent(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
}
