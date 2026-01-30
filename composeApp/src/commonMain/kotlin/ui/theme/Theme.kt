/**
 * 应用主题配置
 *
 * 本文件定义 FileWebHook 应用的 Material 3 主题
 * 使用影刀（ShadowBot）风格的配色方案
 */

package ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ============================================
// 影刀风格颜色定义
// ============================================

/** 主色调：影刀粉红色 */
val ShadowBotPink = Color(0xFFFC6868)

/** 主色调浅色变体 */
val ShadowBotPinkLight = Color(0xFFFFE0E0)

/** 背景色：纯白 */
val ShadowBotBackground = Color(0xFFFFFFFF)

/** 表面色：浅灰 */
val ShadowBotSurface = Color(0xFFF5F7FA)

/** 表面上的内容色：深灰 */
val ShadowBotOnSurface = Color(0xFF1F2329)

/** 表面上的次要内容色：中灰 */
val ShadowBotOnSurfaceVariant = Color(0xFF646A73)

/** 边框/分割线颜色 */
val ShadowBotOutline = Color(0xFFDEE0E3)

/** 错误色：红色 */
val ShadowBotError = Color(0xFFE53935)

/** 成功色：绿色 */
val ShadowBotSuccess = Color(0xFF4CAF50)

/**
 * 影刀风格 Material 3 配色方案
 *
 * 基于浅色主题，使用影刀品牌粉红色作为主色调
 */
private val ShadowBotColorScheme = lightColorScheme(
    primary = ShadowBotPink,                    // 主色
    onPrimary = Color.White,                    // 主色上的内容色
    primaryContainer = ShadowBotPinkLight,      // 主色容器
    onPrimaryContainer = ShadowBotPink,         // 主色容器上的内容色
    secondary = ShadowBotOnSurfaceVariant,      // 次要色
    onSecondary = Color.White,                  // 次要色上的内容色
    background = ShadowBotBackground,           // 背景色
    onBackground = ShadowBotOnSurface,          // 背景上的内容色
    surface = ShadowBotSurface,                 // 表面色
    onSurface = ShadowBotOnSurface,             // 表面上的内容色
    surfaceVariant = ShadowBotSurface,          // 表面变体色
    onSurfaceVariant = ShadowBotOnSurfaceVariant, // 表面变体上的内容色
    outline = ShadowBotOutline,                 // 边框色
    error = ShadowBotError,                     // 错误色
    onError = Color.White                       // 错误色上的内容色
)

/**
 * FileWebHook 应用主题
 *
 * 为应用提供统一的视觉风格
 *
 * @param content 主题包裹的内容
 */
@Composable
fun FileWebHookTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ShadowBotColorScheme,
        content = content
    )
}
