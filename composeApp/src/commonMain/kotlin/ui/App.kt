/**
 * 应用主组件
 *
 * 本文件是 FileWebHook 应用的根 Composable
 * 负责整体布局和页面路由
 */

package ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import data.repository.PermissionRepository
import data.repository.SettingsRepository
import data.repository.TriggerRepository
import data.repository.UserRepository
import domain.queue.TaskQueue
import org.koin.compose.koinInject
import server.FileWebHookServer
import ui.components.Sidebar
import ui.navigation.Screen
import ui.screens.settings.SettingsScreen
import ui.screens.settings.SettingsViewModel
import ui.screens.trigger.TriggerListScreen
import ui.screens.trigger.TriggerViewModel
import ui.screens.user.UserListScreen
import ui.screens.user.UserViewModel
import ui.theme.FileWebHookTheme
import ui.theme.ShadowBotOutline

/**
 * 应用根组件
 *
 * 提供整体应用结构：
 * - 左侧：导航侧边栏
 * - 右侧：主内容区（根据当前页面显示不同内容）
 *
 * 使用 Koin 依赖注入获取仓库和服务实例
 */
@Composable
fun App() {
    FileWebHookTheme {
        // 当前选中的页面
        var currentScreen by remember { mutableStateOf(Screen.TRIGGERS) }

        // 从 Koin 注入依赖
        val userRepository = koinInject<UserRepository>()
        val triggerRepository = koinInject<TriggerRepository>()
        val permissionRepository = koinInject<PermissionRepository>()
        val settingsRepository = koinInject<SettingsRepository>()
        val server = koinInject<FileWebHookServer>()
        val taskQueue = koinInject<TaskQueue>()

        // 创建 ViewModel 实例（使用 remember 保持实例稳定）
        val triggerViewModel = remember { TriggerViewModel(triggerRepository) }
        val userViewModel = remember { UserViewModel(userRepository, triggerRepository, permissionRepository) }
        val settingsViewModel = remember { SettingsViewModel(settingsRepository, server, taskQueue) }

        // 主布局：左右分栏
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // 左侧：导航侧边栏
            Sidebar(
                currentScreen = currentScreen,
                onScreenSelected = { currentScreen = it }
            )

            // 分割线
            Divider(
                modifier = Modifier.fillMaxHeight().width(1.dp),
                color = ShadowBotOutline
            )

            // 右侧：主内容区
            Box(modifier = Modifier.weight(1f)) {
                // 根据当前页面显示对应内容
                when (currentScreen) {
                    Screen.TRIGGERS -> TriggerListScreen(viewModel = triggerViewModel)
                    Screen.USERS -> UserListScreen(viewModel = userViewModel)
                    Screen.SETTINGS -> SettingsScreen(viewModel = settingsViewModel)
                }
            }
        }
    }
}
