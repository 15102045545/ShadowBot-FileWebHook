/**
 * Desktop 平台脚本路径提供者
 *
 * 本文件提供 Desktop 平台特定的脚本路径获取实现
 * 优先从 JAR 资源提取脚本到本地目录，支持开发环境和生产环境
 */

package domain.python

/**
 * Desktop 平台脚本路径提供者
 *
 * 实现 ScriptPathProvider 接口，根据运行环境自动选择脚本路径：
 * - 开发环境：使用源代码目录中的脚本
 * - 生产环境：从 JAR 资源提取到本地目录后使用
 */
class DesktopScriptPathProvider : ScriptPathProvider {

    override fun getScriptPath(scriptName: String): String {
        // 开发环境使用源代码目录
        if (ScriptResourceExtractor.isDevEnvironment()) {
            val devDir = ScriptResourceExtractor.getDevScriptsDirectory()
            if (devDir != null) {
                return java.io.File(devDir, scriptName).absolutePath
            }
        }

        // 生产环境从 JAR 资源提取
        return ScriptResourceExtractor.getScriptPath(scriptName)
    }

    override fun getBaseFrameworkPath(): String {
        // 开发环境使用源代码目录
        if (ScriptResourceExtractor.isDevEnvironment()) {
            val devDir = ScriptResourceExtractor.getDevScriptsDirectory()
            if (devDir != null) {
                return java.io.File(devDir, "BaseFileWebHookAppFramework.pkl").absolutePath
            }
        }

        // 生产环境从 JAR 资源提取
        return ScriptResourceExtractor.getBaseFrameworkPath()
    }
}
