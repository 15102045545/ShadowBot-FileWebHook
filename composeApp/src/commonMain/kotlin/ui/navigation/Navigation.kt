/**
 * 导航定义
 *
 * 本文件定义应用的页面导航结构
 */

package ui.navigation

/**
 * 页面枚举
 *
 * 定义应用中的所有主要页面
 *
 * @property title 页面标题（显示在侧边栏和页面顶部）
 */
enum class Screen(val title: String) {
    /** 触发器管理页面 */
    TRIGGERS("触发器管理"),

    /** 队列管理页面 */
    QUEUE_MANAGEMENT("队列管理"),

    /** 触发器记录管理页面 */
    EXECUTION_LOGS("触发器记录管理"),

    /** 用户管理页面 */
    USERS("用户管理"),

    /** 软件配置页面 */
    SETTINGS("软件配置")
}
