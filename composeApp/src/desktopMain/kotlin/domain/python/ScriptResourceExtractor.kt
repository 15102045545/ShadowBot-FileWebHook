/**
 * Python 脚本资源提取器
 *
 * 本文件提供从 JAR 资源中提取 Python 脚本到本地文件系统的功能
 * 解决打包后脚本文件无法通过相对路径访问的问题
 *
 * 关键设计：
 * - 每次应用启动时，从 JAR 资源强制覆盖本地的脚本和元指令文件
 * - 这确保用户在安装/升级后始终使用软件内置的最新版本
 * - 开发者功能（指令转储）仅在开发环境可用，转储后的文件会打包到安装包中
 */

package domain.python

import java.io.File
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.StandardCopyOption

/**
 * 脚本资源提取器
 *
 * 负责从 JAR 包中提取 Python 脚本到本地目录
 * 每次应用启动时都会重新提取并覆盖，确保用户使用最新版本
 *
 * 脚本文件存储位置：{user.home}/.filewebhook/scripts/
 */
object ScriptResourceExtractor {
    /** 脚本文件目录名 */
    private const val SCRIPTS_DIR = "scripts"

    /** 应用数据目录名 */
    private const val APP_DATA_DIR = ".filewebhook"

    /** 资源路径前缀 */
    private const val RESOURCE_PATH = "/scripts"

    /** 需要提取的 Python 脚本列表 */
    private val PYTHON_SCRIPTS = listOf(
        "dump_clipboard_to_file.py",
        "load_instructions_to_clipboard.py",
        "modify_shadowbot_instructions.py"
    )

    /** BaseFileWebHookAppFramework 元指令文件 */
    private const val BASE_FRAMEWORK_FILE = "BaseFileWebHookAppFramework.pkl"

    /** 脚本目录缓存（提取后的本地路径） */
    @Volatile
    private var scriptsDirectory: File? = null

    /** 初始化锁 */
    private val initLock = Any()

    /** 标记本次会话是否已执行过资源提取 */
    @Volatile
    private var resourcesExtractedThisSession = false

    /**
     * 获取脚本目录路径
     *
     * 如果脚本尚未提取，会先执行提取操作
     *
     * @return 脚本目录的绝对路径
     */
    fun getScriptsDirectory(): String {
        return ensureScriptsExtracted().absolutePath
    }

    /**
     * 获取指定脚本的完整路径
     *
     * @param scriptName 脚本文件名
     * @return 脚本文件的绝对路径
     */
    fun getScriptPath(scriptName: String): String {
        return File(ensureScriptsExtracted(), scriptName).absolutePath
    }

    /**
     * 获取 BaseFileWebHookAppFramework.pkl 的路径
     *
     * @return 元指令文件的绝对路径
     */
    fun getBaseFrameworkPath(): String {
        return File(ensureScriptsExtracted(), BASE_FRAMEWORK_FILE).absolutePath
    }

    /**
     * 确保脚本已提取到本地
     *
     * 使用双重检查锁定模式保证线程安全
     * 每次应用启动的首次调用都会重新提取资源（强制覆盖），确保使用最新版本
     *
     * @return 脚本目录
     */
    private fun ensureScriptsExtracted(): File {
        // 如果本次会话已提取过，直接返回缓存的目录
        if (resourcesExtractedThisSession && scriptsDirectory != null) {
            return scriptsDirectory!!
        }

        synchronized(initLock) {
            // 再次检查，避免重复提取
            if (resourcesExtractedThisSession && scriptsDirectory != null) {
                return scriptsDirectory!!
            }

            val dir = getOrCreateScriptsDir()
            // 每次启动时都重新提取资源，强制覆盖本地文件
            extractResources(dir)
            scriptsDirectory = dir
            resourcesExtractedThisSession = true
            return dir
        }
    }

    /**
     * 获取或创建脚本目录
     *
     * 路径: {user.home}/.filewebhook/scripts/
     *
     * @return 脚本目录
     */
    private fun getOrCreateScriptsDir(): File {
        val userHome = System.getProperty("user.home")
        val appDataDir = File(userHome, APP_DATA_DIR)
        val scriptsDir = File(appDataDir, SCRIPTS_DIR)

        if (!scriptsDir.exists()) {
            scriptsDir.mkdirs()
        }

        return scriptsDir
    }

    /**
     * 从 JAR 资源中提取文件到指定目录
     *
     * 每次调用都会强制覆盖已存在的文件，确保用户使用最新版本
     *
     * @param targetDir 目标目录
     */
    private fun extractResources(targetDir: File) {
        // 提取 Python 脚本（强制覆盖）
        for (scriptName in PYTHON_SCRIPTS) {
            extractResource("$RESOURCE_PATH/$scriptName", File(targetDir, scriptName))
        }

        // 提取 BaseFileWebHookAppFramework.pkl（强制覆盖）
        // 这确保每次软件更新后，用户都使用最新的内置元指令文件
        extractResource("$RESOURCE_PATH/$BASE_FRAMEWORK_FILE", File(targetDir, BASE_FRAMEWORK_FILE))
    }

    /**
     * 提取单个资源文件
     *
     * 使用 REPLACE_EXISTING 选项强制覆盖已存在的文件
     *
     * @param resourcePath 资源路径（JAR 内部路径）
     * @param targetFile 目标文件
     */
    private fun extractResource(resourcePath: String, targetFile: File) {
        val inputStream: InputStream? = javaClass.getResourceAsStream(resourcePath)

        if (inputStream != null) {
            inputStream.use { input ->
                Files.copy(input, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
            }
        } else {
            // 资源不存在时记录警告，但不中断执行
            // 这允许在开发环境中正常运行
            System.err.println("警告: 无法找到资源 $resourcePath")
        }
    }

    /**
     * 检查是否在开发环境中运行
     *
     * 通过检查项目根目录下的源代码目录来判断
     *
     * @return true 如果是开发环境
     */
    fun isDevEnvironment(): Boolean {
        val userDir = System.getProperty("user.dir") ?: return false
        val sourcePath = File(userDir, "composeApp/src/commonMain/kotlin/shadowbot")
        return sourcePath.exists() && sourcePath.isDirectory
    }

    /**
     * 获取开发环境的脚本目录路径（用于执行脚本）
     *
     * @return 开发环境脚本目录路径，如果不是开发环境则返回 null
     */
    fun getDevScriptsDirectory(): String? {
        if (!isDevEnvironment()) return null

        val userDir = System.getProperty("user.dir") ?: return null
        return File(userDir, "composeApp/src/commonMain/kotlin/shadowbot").absolutePath
    }

    /**
     * 获取开发环境的资源目录路径（用于开发者指令转储）
     *
     * 开发者转储的元指令文件会保存到此目录，打包时会被包含到安装包中
     *
     * @return 资源目录路径，如果不是开发环境则返回 null
     */
    fun getDevResourcesScriptsDirectory(): String? {
        if (!isDevEnvironment()) return null

        val userDir = System.getProperty("user.dir") ?: return null
        return File(userDir, "composeApp/src/desktopMain/resources/scripts").absolutePath
    }
}
