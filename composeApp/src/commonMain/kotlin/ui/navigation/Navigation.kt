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

    /** 触发器记录管理页面 */
    EXECUTION_LOGS("触发器记录管理"),

    /** 用户管理页面 */
    USERS("用户管理"),

    /** 用户权限管理页面 */
    USER_PERMISSIONS("用户权限管理"),

    /** 核心状态页面 */
    SETTINGS("核心状态"),

    /** 开发者功能页面 */
    DEVELOPER("开发者功能")
}

/**
 * 侧边栏菜单分组
 *
 * 定义可展开的菜单分组
 */
enum class MenuGroup(val title: String) {
    /** 触发器分组 */
    TRIGGER("触发器"),
    /** 用户分组 */
    USER("用户")
}
