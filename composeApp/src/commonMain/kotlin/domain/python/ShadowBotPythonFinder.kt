/**
 * 影刀 Python 解释器查找器
 *
 * 本文件提供在 Windows 系统中查找影刀软件内置 Python 解释器的功能
 * 必须使用 Kotlin 实现，因为需要先找到 Python 才能执行 Python 脚本
 */

package domain.python

import java.io.File

/**
 * Python 解释器查找结果
 *
 * @property success 是否成功找到
 * @property pythonPath Python 解释器的完整路径
 * @property version Python 版本号（如 "3.10"）
 * @property shadowbotVersion 影刀版本号（如 "5.32.44"）
 * @property message 结果消息
 */
data class PythonFinderResult(
    val success: Boolean,
    val pythonPath: String,
    val version: String,
    val shadowbotVersion: String,
    val message: String
)

/**
 * 影刀 Python 解释器查找器
 *
 * 自动扫描系统中的影刀安装目录，定位 Python 解释器路径
 *
 * 搜索策略：
 * 1. 检查 C:\Program Files (x86)\ShadowBot\ 目录
 * 2. 检查 C:\Program Files\ShadowBot\ 目录
 * 3. 检查 %LOCALAPPDATA%\ShadowBot\ 目录
 * 4. 找到最新版本的安装目录
 * 5. 定位 pythonXXX 子目录中的 python.exe
 */
class ShadowBotPythonFinder {

    companion object {
        /** 版本号正则表达式：匹配 shadowbot-X.Y.Z 格式 */
        private val VERSION_REGEX = Regex("""shadowbot-(\d+)\.(\d+)\.(\d+)""")

        /** Python 目录正则表达式：匹配 pythonXXX 格式 */
        private val PYTHON_DIR_REGEX = Regex("""python(\d+)""")
    }

    /**
     * 查找影刀软件的 Python 解释器
     *
     * @return PythonFinderResult 查找结果
     */
    fun findShadowBotPython(): PythonFinderResult {
        // 可能的影刀安装根目录
        val possibleRoots = listOf(
            File("C:\\Program Files (x86)\\ShadowBot"),
            File("C:\\Program Files\\ShadowBot"),
            File(System.getenv("LOCALAPPDATA") ?: "", "ShadowBot"),
            File(System.getenv("APPDATA") ?: "", "ShadowBot")
        )

        // 收集所有影刀安装目录
        val shadowbotDirs = mutableListOf<File>()

        for (root in possibleRoots) {
            if (root.exists() && root.isDirectory) {
                try {
                    root.listFiles()?.forEach { item ->
                        if (item.isDirectory && item.name.startsWith("shadowbot-")) {
                            shadowbotDirs.add(item)
                        }
                    }
                } catch (e: SecurityException) {
                    // 权限问题，跳过此目录
                    continue
                }
            }
        }

        if (shadowbotDirs.isEmpty()) {
            return PythonFinderResult(
                success = false,
                pythonPath = "",
                version = "",
                shadowbotVersion = "",
                message = "未找到影刀安装目录"
            )
        }

        // 按版本号排序，获取最新版本
        shadowbotDirs.sortWith { a, b ->
            val verA = extractVersion(a.name)
            val verB = extractVersion(b.name)
            // 降序排序：先比较主版本号，再比较次版本号，最后比较修订号
            when {
                verA.first != verB.first -> verB.first.compareTo(verA.first)
                verA.second != verB.second -> verB.second.compareTo(verA.second)
                else -> verB.third.compareTo(verA.third)
            }
        }

        // 在每个版本目录中查找 Python
        for (shadowbotDir in shadowbotDirs) {
            try {
                val pythonDirs = shadowbotDir.listFiles()?.filter { item ->
                    item.isDirectory && item.name.startsWith("python")
                } ?: continue

                for (pythonDir in pythonDirs) {
                    val pythonExe = File(pythonDir, "python.exe")
                    if (pythonExe.exists() && pythonExe.isFile) {
                        // 提取 Python 版本
                        val pythonVersion = extractPythonVersion(pythonDir.name)
                        val shadowbotVersion = extractShadowbotVersion(shadowbotDir.name)

                        return PythonFinderResult(
                            success = true,
                            pythonPath = pythonExe.absolutePath.replace("\\", "/"),
                            version = pythonVersion,
                            shadowbotVersion = shadowbotVersion,
                            message = "成功找到影刀 Python 解释器 (版本 $pythonVersion)"
                        )
                    }
                }
            } catch (e: SecurityException) {
                // 权限问题，尝试下一个目录
                continue
            }
        }

        return PythonFinderResult(
            success = false,
            pythonPath = "",
            version = "",
            shadowbotVersion = "",
            message = "在影刀安装目录中未找到 Python 解释器"
        )
    }

    /**
     * 验证指定路径的 Python 解释器是否有效
     *
     * @param pythonPath Python 解释器路径
     * @return 是否有效
     */
    fun validatePythonPath(pythonPath: String): Boolean {
        if (pythonPath.isBlank()) return false

        val pythonFile = File(pythonPath)
        return pythonFile.exists() &&
                pythonFile.isFile &&
                pythonFile.name.equals("python.exe", ignoreCase = true)
    }

    /**
     * 从目录名提取版本号元组用于排序
     *
     * @param dirName 目录名（如 "shadowbot-5.32.44"）
     * @return 版本号元组（如 Triple(5, 32, 44)）
     */
    private fun extractVersion(dirName: String): Triple<Int, Int, Int> {
        val match = VERSION_REGEX.find(dirName)
        return if (match != null) {
            Triple(
                match.groupValues[1].toIntOrNull() ?: 0,
                match.groupValues[2].toIntOrNull() ?: 0,
                match.groupValues[3].toIntOrNull() ?: 0
            )
        } else {
            Triple(0, 0, 0)
        }
    }

    /**
     * 从目录名提取影刀版本字符串
     *
     * @param dirName 目录名（如 "shadowbot-5.32.44"）
     * @return 版本字符串（如 "5.32.44"）
     */
    private fun extractShadowbotVersion(dirName: String): String {
        return dirName.removePrefix("shadowbot-")
    }

    /**
     * 从 Python 目录名提取版本号
     *
     * @param dirName 目录名（如 "python310"）
     * @return 版本字符串（如 "3.10"）
     */
    private fun extractPythonVersion(dirName: String): String {
        val match = PYTHON_DIR_REGEX.find(dirName)
        return if (match != null) {
            val verNum = match.groupValues[1]
            if (verNum.length >= 2) {
                "${verNum[0]}.${verNum.substring(1)}"
            } else {
                verNum
            }
        } else {
            ""
        }
    }
}
