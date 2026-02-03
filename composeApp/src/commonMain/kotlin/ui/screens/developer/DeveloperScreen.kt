/**
 * 开发者功能页面
 *
 * 本文件提供开发者专用功能界面
 * 主要功能：
 * 1. 将系统剪贴板中的影刀指令转储到 BaseFileWebHookAppFramework.pkl 文件
 */

package ui.screens.developer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentPaste
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
 * - 指令转储：将剪贴板中的影刀指令保存到文件
 *
 * @param viewModel 开发者功能 ViewModel
 * @param projectRootPath 项目根目录路径（用于保存转储文件）
 * @param modifier 修饰符
 */
@Composable
fun DeveloperScreen(
    viewModel: DeveloperViewModel,
    projectRootPath: String,
    modifier: Modifier = Modifier
) {
    // 从 ViewModel 获取状态
    val isLoading by viewModel.isLoading
    val resultMessage by viewModel.resultMessage
    val isSuccess by viewModel.isSuccess
    val showResultDialog by viewModel.showResultDialog
    val scope = rememberCoroutineScope()

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

        // ==========================================
        // 指令转储卡片
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
                            scope.launch {
                                viewModel.dumpClipboardInstruction(projectRootPath)
                            }
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
                        转储后的指令文件将保存到：
                        composeApp/src/commonMain/kotlin/shadowbot/BaseFileWebHookAppFramework.pkl

                        该文件包含 FileWebHook-App-Framework 框架的元指令，
                        用户在触发器管理页面点击"复制框架指令"时，
                        系统会读取此文件并动态替换占位符后写入剪贴板。

                        占位符包括：
                        • ${"$"}{triggerId} - 触发器ID
                        • ${"$"}{triggerFilesPath} - 触发器文件路径
                        • ${"$"}{serverIpAndPort} - 服务器IP和端口
                    """.trimIndent(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}
