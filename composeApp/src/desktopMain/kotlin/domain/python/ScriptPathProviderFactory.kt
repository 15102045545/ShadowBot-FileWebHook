/**
 * 脚本路径提供者工厂 - Desktop 平台实现
 *
 * 本文件提供 Desktop 平台特定的脚本路径提供者创建实现
 */

package domain.python

/**
 * 创建 Desktop 平台的脚本路径提供者
 *
 * @return Desktop 平台的脚本路径提供者实例
 */
actual fun createScriptPathProvider(): ScriptPathProvider = DesktopScriptPathProvider()
