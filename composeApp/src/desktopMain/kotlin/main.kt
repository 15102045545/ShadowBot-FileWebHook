/**
 * Desktop 应用入口
 *
 * 本文件是 FileWebHook 桌面应用的启动入口
 * 负责初始化 Koin 依赖注入和创建应用窗口
 */

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import di.appModules
import org.koin.core.context.startKoin
import ui.App

/**
 * 应用主函数
 *
 * 执行以下初始化步骤：
 * 1. 启动 Koin 依赖注入框架
 * 2. 配置窗口大小和状态
 * 3. 创建应用主窗口
 */
fun main() = application {
    // 初始化 Koin 依赖注入
    // 加载所有模块（数据层、领域层、服务层）
    startKoin {
        modules(appModules)
    }

    // 配置窗口状态
    // 默认全屏显示
    val windowState = rememberWindowState(
        placement = WindowPlacement.Maximized
    )

    // 创建应用窗口
    Window(
        onCloseRequest = ::exitApplication,  // 关闭窗口时退出应用
        state = windowState,
        title = "FileWebHook - ShadowBot HTTP触发器"  // 窗口标题
    ) {
        // 渲染应用主组件
        App()
    }
}
