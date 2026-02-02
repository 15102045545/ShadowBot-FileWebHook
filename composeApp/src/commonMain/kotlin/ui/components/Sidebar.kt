/**
 * 侧边栏导航组件
 *
 * 本文件提供应用的左侧导航栏组件
 * 包含 Logo 和导航菜单项
 */

package ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.History
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ui.navigation.Screen
import ui.theme.ShadowBotPink

/**
 * 侧边栏组件
 *
 * 显示应用 Logo 和导航菜单
 *
 * @param currentScreen 当前选中的页面
 * @param onScreenSelected 页面切换回调
 * @param modifier 修饰符
 */
@Composable
fun Sidebar(
    currentScreen: Screen,
    onScreenSelected: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(200.dp)
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        // Logo 标题
        Text(
            text = "FileWebHook",
            style = MaterialTheme.typography.titleLarge,
            color = ShadowBotPink,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // 导航菜单项：触发器管理
        SidebarItem(
            icon = Icons.Default.PlayArrow,
            label = Screen.TRIGGERS.title,
            selected = currentScreen == Screen.TRIGGERS,
            onClick = { onScreenSelected(Screen.TRIGGERS) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 导航菜单项：队列管理
        SidebarItem(
            icon = Icons.AutoMirrored.Filled.List,
            label = Screen.QUEUE_MANAGEMENT.title,
            selected = currentScreen == Screen.QUEUE_MANAGEMENT,
            onClick = { onScreenSelected(Screen.QUEUE_MANAGEMENT) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 导航菜单项：触发器记录管理
        SidebarItem(
            icon = Icons.Default.History,
            label = Screen.EXECUTION_LOGS.title,
            selected = currentScreen == Screen.EXECUTION_LOGS,
            onClick = { onScreenSelected(Screen.EXECUTION_LOGS) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 导航菜单项：用户管理
        SidebarItem(
            icon = Icons.Default.Person,
            label = Screen.USERS.title,
            selected = currentScreen == Screen.USERS,
            onClick = { onScreenSelected(Screen.USERS) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 导航菜单项：系统设置
        SidebarItem(
            icon = Icons.Default.Settings,
            label = Screen.SETTINGS.title,
            selected = currentScreen == Screen.SETTINGS,
            onClick = { onScreenSelected(Screen.SETTINGS) }
        )
    }
}

/**
 * 侧边栏菜单项组件
 *
 * 单个导航菜单项，包含图标和文字
 * 选中状态下使用高亮背景色
 *
 * @param icon 菜单图标
 * @param label 菜单文字
 * @param selected 是否选中
 * @param onClick 点击回调
 */
@Composable
private fun SidebarItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    // 根据选中状态设置背景色
    val backgroundColor = if (selected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surface
    }

    // 根据选中状态设置内容色
    val contentColor = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 图标
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = contentColor,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        // 文字
        Text(
            text = label,
            color = contentColor,
            fontSize = 14.sp
        )
    }
}
