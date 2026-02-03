/**
 * 侧边栏导航组件
 *
 * 本文件提供应用的左侧导航栏组件
 * 包含 Logo 和支持分组的导航菜单项
 */

package ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ui.navigation.MenuGroup
import ui.navigation.Screen
import ui.theme.ShadowBotPink

/**
 * 侧边栏组件
 *
 * 显示应用 Logo 和导航菜单
 * 支持分组展开/收起的子菜单结构
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
    // 分组展开状态
    var expandedGroups by remember {
        mutableStateOf(
            setOf(
                // 根据当前页面初始化展开状态
                when (currentScreen) {
                    Screen.TRIGGERS, Screen.EXECUTION_LOGS -> MenuGroup.TRIGGER
                    Screen.USERS, Screen.USER_PERMISSIONS -> MenuGroup.USER
                    else -> null
                }
            ).filterNotNull().toSet()
        )
    }

    // 当页面切换时，自动展开对应分组
    LaunchedEffect(currentScreen) {
        val targetGroup = when (currentScreen) {
            Screen.TRIGGERS, Screen.EXECUTION_LOGS -> MenuGroup.TRIGGER
            Screen.USERS, Screen.USER_PERMISSIONS -> MenuGroup.USER
            else -> null
        }
        if (targetGroup != null && targetGroup !in expandedGroups) {
            expandedGroups = expandedGroups + targetGroup
        }
    }

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

        // ==========================================
        // 触发器分组
        // ==========================================
        SidebarMenuGroup(
            group = MenuGroup.TRIGGER,
            icon = Icons.Default.PlayArrow,
            isExpanded = MenuGroup.TRIGGER in expandedGroups,
            isAnyChildSelected = currentScreen == Screen.TRIGGERS || currentScreen == Screen.EXECUTION_LOGS,
            onToggle = {
                expandedGroups = if (MenuGroup.TRIGGER in expandedGroups) {
                    expandedGroups - MenuGroup.TRIGGER
                } else {
                    expandedGroups + MenuGroup.TRIGGER
                }
            }
        ) {
            // 子菜单：触发器管理
            SidebarSubItem(
                label = Screen.TRIGGERS.title,
                selected = currentScreen == Screen.TRIGGERS,
                onClick = { onScreenSelected(Screen.TRIGGERS) }
            )
            // 子菜单：触发器记录管理
            SidebarSubItem(
                label = Screen.EXECUTION_LOGS.title,
                selected = currentScreen == Screen.EXECUTION_LOGS,
                onClick = { onScreenSelected(Screen.EXECUTION_LOGS) }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // ==========================================
        // 用户分组
        // ==========================================
        SidebarMenuGroup(
            group = MenuGroup.USER,
            icon = Icons.Default.Person,
            isExpanded = MenuGroup.USER in expandedGroups,
            isAnyChildSelected = currentScreen == Screen.USERS || currentScreen == Screen.USER_PERMISSIONS,
            onToggle = {
                expandedGroups = if (MenuGroup.USER in expandedGroups) {
                    expandedGroups - MenuGroup.USER
                } else {
                    expandedGroups + MenuGroup.USER
                }
            }
        ) {
            // 子菜单：用户管理
            SidebarSubItem(
                label = Screen.USERS.title,
                selected = currentScreen == Screen.USERS,
                onClick = { onScreenSelected(Screen.USERS) }
            )
            // 子菜单：用户权限管理
            SidebarSubItem(
                label = Screen.USER_PERMISSIONS.title,
                selected = currentScreen == Screen.USER_PERMISSIONS,
                onClick = { onScreenSelected(Screen.USER_PERMISSIONS) }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 导航菜单项：核心状态（独立菜单）
        SidebarItem(
            icon = Icons.Default.Favorite,
            label = Screen.SETTINGS.title,
            selected = currentScreen == Screen.SETTINGS,
            onClick = { onScreenSelected(Screen.SETTINGS) }
        )

        // 弹性空间，将开发者功能推到底部
        Spacer(modifier = Modifier.weight(1f))

        // 导航菜单项：开发者功能（位于左下角）
        SidebarItem(
            icon = Icons.Default.Build,
            label = Screen.DEVELOPER.title,
            selected = currentScreen == Screen.DEVELOPER,
            onClick = { onScreenSelected(Screen.DEVELOPER) }
        )
    }
}

/**
 * 侧边栏分组菜单组件
 *
 * 可展开/收起的菜单分组，包含多个子菜单项
 *
 * @param group 菜单分组
 * @param icon 分组图标
 * @param isExpanded 是否展开
 * @param isAnyChildSelected 是否有子菜单被选中
 * @param onToggle 展开/收起回调
 * @param content 子菜单内容
 */
@Composable
private fun SidebarMenuGroup(
    group: MenuGroup,
    icon: ImageVector,
    isExpanded: Boolean,
    isAnyChildSelected: Boolean,
    onToggle: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    // 箭头旋转动画
    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f
    )

    // 根据展开状态和子菜单选中状态设置样式
    val backgroundColor = if (isAnyChildSelected && !isExpanded) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
    } else {
        MaterialTheme.colorScheme.surface
    }

    val contentColor = if (isAnyChildSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Column {
        // 分组标题行
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(backgroundColor)
                .clickable(onClick = onToggle)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 图标
            Icon(
                imageVector = icon,
                contentDescription = group.title,
                tint = contentColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            // 分组标题
            Text(
                text = group.title,
                color = contentColor,
                fontSize = 14.sp,
                modifier = Modifier.weight(1f)
            )
            // 展开/收起箭头
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = if (isExpanded) "收起" else "展开",
                tint = contentColor,
                modifier = Modifier
                    .size(16.dp)
                    .rotate(rotationAngle)
            )
        }

        // 子菜单区域（带动画）
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column(
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            ) {
                content()
            }
        }
    }
}

/**
 * 侧边栏子菜单项组件
 *
 * 分组内的子菜单项，带缩进
 *
 * @param label 菜单文字
 * @param selected 是否选中
 * @param onClick 点击回调
 */
@Composable
private fun SidebarSubItem(
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
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 小圆点指示器
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(contentColor)
        )
        Spacer(modifier = Modifier.width(12.dp))
        // 文字
        Text(
            text = label,
            color = contentColor,
            fontSize = 13.sp
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
