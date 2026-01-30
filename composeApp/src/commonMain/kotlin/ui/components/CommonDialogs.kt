/**
 * 通用对话框组件
 *
 * 本文件提供可复用的对话框组件
 * 包括表单对话框、确认对话框和表单输入框
 */

package ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

/**
 * 表单对话框组件
 *
 * 用于创建/编辑操作的通用表单对话框
 * 包含标题、内容区域和确认/取消按钮
 *
 * @param title 对话框标题
 * @param onDismiss 取消/关闭回调
 * @param onConfirm 确认回调
 * @param confirmEnabled 确认按钮是否可用
 * @param confirmText 确认按钮文字
 * @param dismissText 取消按钮文字
 * @param content 对话框内容（表单字段）
 */
@Composable
fun FormDialog(
    title: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    confirmEnabled: Boolean = true,
    confirmText: String = "确定",
    dismissText: String = "取消",
    content: @Composable ColumnScope.() -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .width(400.dp)
                    .padding(24.dp)
            ) {
                // 标题
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 表单内容
                content()

                Spacer(modifier = Modifier.height(24.dp))

                // 按钮区域
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    // 取消按钮
                    TextButton(onClick = onDismiss) {
                        Text(dismissText)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    // 确认按钮
                    Button(
                        onClick = onConfirm,
                        enabled = confirmEnabled
                    ) {
                        Text(confirmText)
                    }
                }
            }
        }
    }
}

/**
 * 确认对话框组件
 *
 * 用于删除等危险操作的确认提示
 *
 * @param title 对话框标题
 * @param message 提示消息
 * @param onDismiss 取消回调
 * @param onConfirm 确认回调
 * @param confirmText 确认按钮文字
 * @param dismissText 取消按钮文字
 * @param isDestructive 是否为危险操作（影响按钮颜色）
 */
@Composable
fun ConfirmDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    confirmText: String = "确定",
    dismissText: String = "取消",
    isDestructive: Boolean = false
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            // 危险操作使用红色按钮
            Button(
                onClick = onConfirm,
                colors = if (isDestructive) {
                    ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                } else {
                    ButtonDefaults.buttonColors()
                }
            ) {
                Text(confirmText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(dismissText)
            }
        }
    )
}

/**
 * 表单输入框组件
 *
 * 标准化的表单输入框样式
 *
 * @param value 当前值
 * @param onValueChange 值变更回调
 * @param label 输入框标签
 * @param modifier 修饰符
 * @param singleLine 是否单行
 * @param isError 是否显示错误状态
 * @param supportingText 辅助文字（如错误提示）
 */
@Composable
fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    isError: Boolean = false,
    supportingText: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = singleLine,
        isError = isError,
        supportingText = supportingText?.let { { Text(it) } },
        modifier = modifier.fillMaxWidth()
    )
}
