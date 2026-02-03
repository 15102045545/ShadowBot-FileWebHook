/**
 * Python 脚本执行器
 *
 * 本文件提供执行 Python 脚本的能力
 * 用于调用影刀指令相关的 Python 脚本
 */

package domain.python

import data.model.AppSettings
import data.repository.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Python 脚本执行结果
 *
 * @property success 是否执行成功
 * @property message 结果消息
 * @property data 额外数据（可选）
 */
data class PythonResult(
    val success: Boolean,
    val message: String,
    val data: Map<String, Any?> = emptyMap()
)

/**
 * 脚本路径提供者接口
 *
 * 用于获取脚本文件路径，允许不同平台提供不同实现
 */
interface ScriptPathProvider {
    /**
     * 获取指定脚本的完整路径
     *
     * @param scriptName 脚本文件名
     * @return 脚本文件的绝对路径
     */
    fun getScriptPath(scriptName: String): String

    /**
     * 获取 BaseFileWebHookAppFramework.pkl 的路径
     *
     * @return 元指令文件的绝对路径
     */
    fun getBaseFrameworkPath(): String
}

/**
 * Python 脚本执行器
 *
 * 管理 Python 解释器路径，执行 Python 脚本并解析输出
 *
 * @property settingsRepository 设置仓库，用于获取 Python 解释器路径配置
 * @property pythonFinder Python 解释器查找器
 * @property scriptPathProvider 脚本路径提供者
 */
class PythonExecutor(
    private val settingsRepository: SettingsRepository,
    private val pythonFinder: ShadowBotPythonFinder = ShadowBotPythonFinder(),
    private val scriptPathProvider: ScriptPathProvider? = null
) {
    companion object {
        /** Python 脚本所在目录（相对于项目根目录，仅开发环境使用） */
        private const val SCRIPTS_RELATIVE_PATH = "composeApp/src/commonMain/kotlin/shadowbot"

        /** 脚本执行超时时间（秒） */
        private const val SCRIPT_TIMEOUT_SECONDS = 60L
    }

    /** JSON 解析器 */
    private val json = Json { ignoreUnknownKeys = true }

    /**
     * 获取 Python 解释器路径
     *
     * 优先使用用户配置的路径，如果未配置则自动检测影刀内置 Python
     *
     * @return Python 解释器路径，如果找不到返回 null
     */
    suspend fun getPythonPath(): String? = withContext(Dispatchers.IO) {
        val settings = settingsRepository.getSettings()

        // 如果用户配置了路径且有效，则使用用户配置
        if (settings.pythonInterpreterPath.isNotBlank()) {
            if (pythonFinder.validatePythonPath(settings.pythonInterpreterPath)) {
                return@withContext settings.pythonInterpreterPath
            }
        }

        // 自动检测影刀 Python
        val result = pythonFinder.findShadowBotPython()
        if (result.success) {
            return@withContext result.pythonPath
        }

        null
    }

    /**
     * 执行 Python 脚本
     *
     * @param scriptName 脚本文件名（不含路径）
     * @param args 命令行参数
     * @return 执行结果
     */
    suspend fun execute(scriptName: String, vararg args: String): PythonResult = withContext(Dispatchers.IO) {
        // 获取 Python 解释器路径
        val pythonPath = getPythonPath()
            ?: return@withContext PythonResult(
                success = false,
                message = "未找到 Python 解释器，请在设置中配置或安装影刀软件"
            )

        // 构建脚本完整路径
        val scriptPath = getScriptFilePath(scriptName)

        // 检查脚本是否存在
        if (!File(scriptPath).exists()) {
            return@withContext PythonResult(
                success = false,
                message = "脚本文件不存在: $scriptPath"
            )
        }

        try {
            // 构建命令
            val command = mutableListOf(pythonPath, scriptPath)
            command.addAll(args)

            // 执行命令
            val processBuilder = ProcessBuilder(command)
            processBuilder.redirectErrorStream(true)
            // 设置 PYTHONIOENCODING 确保 Python 使用 UTF-8 输出
            processBuilder.environment()["PYTHONIOENCODING"] = "utf-8"

            val process = processBuilder.start()
            // 使用 UTF-8 编码读取 Python 脚本的输出（Python 脚本输出为 UTF-8）
            val output = process.inputStream.bufferedReader(Charsets.UTF_8).readText()
            val exitCode = process.waitFor()

            // 解析 JSON 输出
            parseJsonOutput(output, exitCode)

        } catch (e: Exception) {
            PythonResult(
                success = false,
                message = "执行脚本失败: ${e.message}"
            )
        }
    }

    /**
     * 获取脚本文件的完整路径
     *
     * 优先使用 scriptPathProvider（用于打包后的应用）
     * 如果没有提供，则使用开发环境的相对路径
     *
     * @param scriptName 脚本文件名
     * @return 脚本文件的绝对路径
     */
    private fun getScriptFilePath(scriptName: String): String {
        // 如果提供了脚本路径提供者，使用它获取路径
        scriptPathProvider?.let {
            return it.getScriptPath(scriptName)
        }

        // 开发环境：使用相对路径
        val projectRoot = System.getProperty("user.dir") ?: ""
        return File(projectRoot, "$SCRIPTS_RELATIVE_PATH/$scriptName").absolutePath
    }

    /**
     * 获取 BaseFileWebHookAppFramework.pkl 的路径
     *
     * 优先使用 scriptPathProvider（用于打包后的应用），
     * 否则使用开发环境的相对路径
     *
     * @return 元指令文件的绝对路径
     */
    fun getBaseFrameworkFilePath(): String {
        // 如果提供了脚本路径提供者，使用它获取路径
        scriptPathProvider?.let {
            return it.getBaseFrameworkPath()
        }

        // 开发环境：使用相对路径
        val projectRoot = System.getProperty("user.dir") ?: ""
        return "$projectRoot/$SCRIPTS_RELATIVE_PATH/BaseFileWebHookAppFramework.pkl"
    }

    /**
     * 检查是否在开发环境中运行
     *
     * @return true 如果是开发环境
     */
    fun isDevEnvironment(): Boolean {
        val userDir = System.getProperty("user.dir") ?: return false
        val sourcePath = File(userDir, "composeApp/src/commonMain/kotlin/shadowbot")
        return sourcePath.exists() && sourcePath.isDirectory
    }

    /**
     * 获取开发环境的资源保存路径（用于开发者指令转储）
     *
     * 开发者转储的元指令文件会保存到 desktopMain/resources/scripts 目录，
     * 这样打包时会被包含到安装包中
     *
     * @return 保存路径，如果不是开发环境则返回 null
     */
    fun getDevResourcesSavePath(): String? {
        if (!isDevEnvironment()) return null

        val userDir = System.getProperty("user.dir") ?: return null
        return File(userDir, "composeApp/src/desktopMain/resources/scripts/BaseFileWebHookAppFramework.pkl").absolutePath
    }

    /**
     * 查找影刀 Python 解释器
     *
     * @return 查找结果
     */
    fun findShadowBotPython(): PythonFinderResult {
        return pythonFinder.findShadowBotPython()
    }

    /**
     * 转储剪贴板指令到文件
     *
     * @param outputPath 输出文件路径
     * @return 执行结果
     */
    suspend fun dumpClipboardToFile(outputPath: String): PythonResult {
        return execute(
            "dump_clipboard_to_file.py",
            "--output", outputPath
        )
    }

    /**
     * 修改指令并保存
     *
     * 使用 modify_shadowbot_instructions.py 脚本修改元指令文件中的变量值
     * 该脚本支持同时更新 ShadowBot.Flow.Blocks 和 HTML Format 中的 base64 数据
     *
     * @param inputPath 输入文件路径（元指令文件 .pkl）
     * @param outputPath 输出文件路径（修改后的指令文件 .pkl）
     * @param triggerId 触发器 ID
     * @param triggerFilesPath 触发器文件路径
     * @param serverIpAndPort 服务器 IP 和端口
     * @return 执行结果
     */
    suspend fun modifyAndSaveInstructions(
        inputPath: String,
        outputPath: String,
        triggerId: Long,
        triggerFilesPath: String,
        serverIpAndPort: String
    ): PythonResult {
        return execute(
            "modify_shadowbot_instructions.py",
            "-i", inputPath,
            "-o", outputPath,
            "-var", "triggerId", "-val", triggerId.toString(),
            "-var", "triggerFilesPath", "-val", triggerFilesPath,
            "-var", "serverIpAndPort", "-val", serverIpAndPort
        )
    }

    /**
     * 加载指令到剪贴板
     *
     * @param inputPath 输入文件路径
     * @return 执行结果
     */
    suspend fun loadInstructionsToClipboard(inputPath: String): PythonResult {
        return execute(
            "load_instructions_to_clipboard.py",
            "--input", inputPath
        )
    }

    /**
     * 解析 Python 脚本的 JSON 输出
     *
     * @param output 脚本输出内容
     * @param exitCode 进程退出码
     * @return 解析后的结果
     */
    private fun parseJsonOutput(output: String, exitCode: Int): PythonResult {
        return try {
            // 尝试解析 JSON
            val jsonObject = json.parseToJsonElement(output.trim()).jsonObject

            val success = jsonObject["success"]?.jsonPrimitive?.boolean ?: false
            val message = jsonObject["message"]?.jsonPrimitive?.content ?: ""

            // 提取其他数据
            val data = mutableMapOf<String, Any?>()
            jsonObject.forEach { (key, value) ->
                if (key != "success" && key != "message") {
                    data[key] = when {
                        value.jsonPrimitive.isString -> value.jsonPrimitive.content
                        value.jsonPrimitive.content.toIntOrNull() != null -> value.jsonPrimitive.int
                        value.jsonPrimitive.content.toBooleanStrictOrNull() != null -> value.jsonPrimitive.boolean
                        else -> value.jsonPrimitive.content
                    }
                }
            }

            PythonResult(success = success, message = message, data = data)

        } catch (e: Exception) {
            // JSON 解析失败，返回原始输出
            PythonResult(
                success = exitCode == 0,
                message = if (exitCode == 0) output else "脚本执行失败 (退出码: $exitCode): $output"
            )
        }
    }
}
