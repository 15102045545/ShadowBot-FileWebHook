/**
 * 脚本路径提供者工厂 - 公共接口
 *
 * 本文件定义了脚本路径提供者创建的 expect 声明
 * 具体实现在各平台的 actual 实现中（如 desktopMain）
 */

package domain.python

/**
 * 创建平台特定的脚本路径提供者
 *
 * 这是 Kotlin Multiplatform 的 expect/actual 模式
 *
 * @return 平台特定的脚本路径提供者实例
 */
expect fun createScriptPathProvider(): ScriptPathProvider
