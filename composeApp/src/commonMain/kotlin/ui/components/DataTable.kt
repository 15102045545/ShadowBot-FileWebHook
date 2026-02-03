/**
 * 数据表格组件
 *
 * 本文件提供可复用的数据表格组件
 * 用于触发器列表、用户列表等数据展示
 */

package ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ui.theme.ShadowBotOutline

/**
 * 表格列定义
 *
 * 定义表格的单列配置
 *
 * @param T 数据项类型
 * @property header 列头文字
 * @property weight 列宽度权重（相对宽度）
 * @property content 单元格内容渲染函数
 */
data class TableColumn<T>(
    val header: String,
    val weight: Float = 1f,
    val content: @Composable (T) -> Unit
)

/**
 * 数据表格组件
 *
 * 通用的数据表格，支持自定义列和内容渲染
 *
 * @param T 数据项类型
 * @param items 数据列表
 * @param columns 列定义列表
 * @param modifier 修饰符
 * @param onRowClick 行点击回调（可选）
 */
@Composable
fun <T> DataTable(
    items: List<T>,
    columns: List<TableColumn<T>>,
    modifier: Modifier = Modifier,
    onRowClick: ((T) -> Unit)? = null
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, ShadowBotOutline, RoundedCornerShape(8.dp))
    ) {
        // 表头行
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            columns.forEach { column ->
                Box(
                    modifier = Modifier.weight(column.weight),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = column.header,
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // 表头分割线
        HorizontalDivider(Modifier, DividerDefaults.Thickness, color = ShadowBotOutline)

        // 表体（使用 LazyColumn 支持大数据量滚动）
        LazyColumn {
            items(items) { item ->
                // 数据行
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    columns.forEach { column ->
                        Box(
                            modifier = Modifier.weight(column.weight),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            column.content(item)
                        }
                    }
                }
                // 行分割线
                Divider(color = ShadowBotOutline)
            }
        }
    }
}

/**
 * 表格单元格文本组件
 *
 * 标准化的单元格文本样式，支持文本截断
 *
 * @param text 显示文本
 * @param modifier 修饰符
 */
@Composable
fun TableCellText(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        fontSize = 13.sp,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
    )
}

/**
 * 状态标签组件
 *
 * 用于显示状态（如成功/失败）的小标签
 *
 * @param text 标签文字
 * @param isSuccess 是否为成功状态（影响颜色）
 */
@Composable
fun StatusChip(
    text: String,
    isSuccess: Boolean = true
) {
    // 根据状态选择颜色
    val backgroundColor = if (isSuccess) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.errorContainer
    }

    val textColor = if (isSuccess) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.error
    }

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
