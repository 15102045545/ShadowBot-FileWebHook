/**
 * 剪贴板管理器 - Desktop 平台实现
 *
 * 本文件提供 Desktop 平台（Windows）的剪贴板操作实现
 * 使用 JNA (Java Native Access) 调用 Windows API 来操作剪贴板
 *
 * 主要功能：
 * 1. 读取系统剪贴板中的所有格式数据并序列化到文件
 * 2. 从文件读取序列化数据并恢复到系统剪贴板
 * 3. 支持影刀自定义剪贴板格式的处理
 *
 * 参考实现：
 * - ShadowbotAppFormwork_InstructDataCopy.py
 * - ShadowbotAppFormwork_InstructDataPaste.py
 */

package domain.clipboard

import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.platform.win32.WinDef.HWND
import com.sun.jna.win32.StdCallLibrary
import com.sun.jna.win32.W32APIOptions
import java.io.*
import java.nio.charset.StandardCharsets

/**
 * 自定义 User32 接口
 *
 * 定义剪贴板相关的 Windows API 函数
 */
private interface CustomUser32 : StdCallLibrary {
    companion object {
        val INSTANCE: CustomUser32 = Native.load("user32", CustomUser32::class.java, W32APIOptions.DEFAULT_OPTIONS)
    }

    fun OpenClipboard(hWndNewOwner: HWND?): Boolean
    fun CloseClipboard(): Boolean
    fun EmptyClipboard(): Boolean
    fun EnumClipboardFormats(format: Int): Int
    fun GetClipboardData(uFormat: Int): Pointer?
    fun SetClipboardData(uFormat: Int, hMem: Pointer?): Pointer?
    fun GetClipboardFormatNameW(format: Int, lpszFormatName: CharArray, cchMaxCount: Int): Int
    fun RegisterClipboardFormatW(lpszFormat: String): Int
}

/**
 * 自定义 Kernel32 接口
 *
 * 定义全局内存操作的 Windows API 函数
 */
private interface CustomKernel32 : StdCallLibrary {
    companion object {
        val INSTANCE: CustomKernel32 = Native.load("kernel32", CustomKernel32::class.java, W32APIOptions.DEFAULT_OPTIONS)
    }

    fun GlobalAlloc(uFlags: Int, dwBytes: Int): Pointer?
    fun GlobalLock(hMem: Pointer?): Pointer?
    fun GlobalUnlock(hMem: Pointer?): Boolean
    fun GlobalSize(hMem: Pointer?): Int
    fun GlobalFree(hMem: Pointer?): Pointer?
}

/**
 * Desktop 平台剪贴板管理器实现
 *
 * 使用 JNA 调用 Windows 原生 API 操作剪贴板
 * 支持所有剪贴板格式的读取和写入，包括影刀自定义格式
 */
actual class ClipboardManager actual constructor() {

    companion object {
        // Windows 自定义剪贴板格式的起始 ID
        // 格式 ID >= 0xC000 (49152) 表示自定义格式
        private const val CUSTOM_FORMAT_START = 0xC000

        // GlobalAlloc 标志
        private const val GMEM_MOVEABLE = 0x0002
        private const val GMEM_ZEROINIT = 0x0040
        private const val GHND = GMEM_MOVEABLE or GMEM_ZEROINIT
    }

    private val user32 = CustomUser32.INSTANCE
    private val kernel32 = CustomKernel32.INSTANCE

    /**
     * 从文件读取数据并写入剪贴板
     *
     * @param sourcePath 源数据文件路径
     * @param replacements 占位符替换映射表
     * @return 操作结果
     */
    actual fun restoreClipboardFromFile(
        sourcePath: String,
        replacements: Map<String, String>
    ): ClipboardResult {
        return try {
            val sourceFile = File(sourcePath)
            if (!sourceFile.exists()) {
                return ClipboardResult.Error("文件不存在: $sourcePath")
            }

            // 读取序列化的数据
            @Suppress("UNCHECKED_CAST")
            val clipboardDataList = ObjectInputStream(FileInputStream(sourceFile)).use { ois ->
                ois.readObject() as List<SerializableClipboardData>
            }

            if (clipboardDataList.isEmpty()) {
                return ClipboardResult.Error("文件中没有可恢复的数据")
            }

            // 打开剪贴板并清空
            if (!user32.OpenClipboard(null)) {
                return ClipboardResult.Error("无法打开剪贴板")
            }

            var successCount = 0
            try {
                user32.EmptyClipboard()

                for (formatData in clipboardDataList) {
                    try {
                        var data = formatData.data

                        // 如果有占位符替换，对包含 JSON 的格式进行处理
                        // 主要针对 ShadowBot.Flow.Blocks 格式（影刀主要数据格式）
                        if (replacements.isNotEmpty() && shouldReplaceInFormat(formatData.formatName)) {
                            data = replaceInData(data, replacements, formatData.formatName)
                        }

                        // 处理自定义格式 ID（>= 0xC000）
                        // 自定义格式 ID 是机器相关的，需要用格式名重新注册获取当前机器的正确 ID
                        var actualFormatId = formatData.formatId
                        if (formatData.formatId >= CUSTOM_FORMAT_START &&
                            formatData.formatName.isNotEmpty() &&
                            !formatData.formatName.startsWith("Format_")
                        ) {
                            try {
                                // 用格式名在当前机器上注册/获取正确的格式 ID
                                actualFormatId = user32.RegisterClipboardFormatW(formatData.formatName)
                            } catch (e: Exception) {
                                println("注册格式 ${formatData.formatName} 失败: ${e.message}, 使用原 ID")
                            }
                        }

                        // 分配全局内存
                        val hGlobal = kernel32.GlobalAlloc(GHND, data.size)
                        if (hGlobal != null) {
                            val lockedMem = kernel32.GlobalLock(hGlobal)
                            if (lockedMem != null) {
                                try {
                                    // 复制数据到全局内存
                                    lockedMem.write(0, data, 0, data.size)
                                } finally {
                                    kernel32.GlobalUnlock(hGlobal)
                                }

                                // 设置剪贴板数据
                                if (user32.SetClipboardData(actualFormatId, hGlobal) != null) {
                                    successCount++
                                } else {
                                    // SetClipboardData 失败，需要释放内存
                                    kernel32.GlobalFree(hGlobal)
                                }
                            } else {
                                kernel32.GlobalFree(hGlobal)
                            }
                        }
                    } catch (e: Exception) {
                        println("格式 ${formatData.formatId} 恢复失败: ${e.message}")
                    }
                }
            } finally {
                user32.CloseClipboard()
            }

            if (successCount > 0) {
                ClipboardResult.Success("成功恢复 $successCount/${clipboardDataList.size} 种格式到剪贴板")
            } else {
                ClipboardResult.Error("未能恢复任何数据到剪贴板")
            }
        } catch (e: Exception) {
            ClipboardResult.Error("恢复失败: ${e.message}")
        }
    }



    /**
     * 判断是否应该对该格式进行变量替换
     *
     * 只对包含影刀指令 JSON 数据的格式进行替换
     *
     * @param formatName 格式名称
     * @return 是否需要替换
     */
    private fun shouldReplaceInFormat(formatName: String): Boolean {
        return formatName == "ShadowBot.Flow.Blocks" ||
                formatName == "HTML Format"
    }

    /**
     * 在二进制数据中替换影刀指令的变量值
     *
     * 影刀指令中"设置变量"指令的存储格式为：
     * {
     *   "inputs": {"value": {"value": "10:"}},
     *   "outputs": {"variable": {"name": "triggerId", ...}}
     * }
     *
     * 替换策略：
     * 1. 找到 "name":"variableName" 的位置（变量名定义）
     * 2. 向前搜索最近的 "10:" 空值并替换为 "10:actualValue"
     *
     * 需要同时处理 UTF-8 和 UTF-16LE 两种编码格式
     *
     * @param data 原始数据
     * @param replacements 替换映射表 (变量名 -> 实际值)
     * @param formatName 格式名称
     * @return 替换后的数据
     */
    private fun replaceInData(
        data: ByteArray,
        replacements: Map<String, String>,
        formatName: String
    ): ByteArray {
        var result = data

        // 对于 HTML Format，需要处理 base64 编码的 JSON
        if (formatName == "HTML Format") {
            result = replaceInHtmlFormat(result, replacements)
        } else {
            // 对于 ShadowBot.Flow.Blocks，直接在二进制数据中替换
            for ((variableName, actualValue) in replacements) {
                // UTF-8 编码
                result = replaceVariableValue(result, variableName, actualValue, StandardCharsets.UTF_8)
            }
        }

        return result
    }

    /**
     * 在 HTML Format 格式中替换变量值
     *
     * HTML Format 包含 base64 编码的 JSON，需要：
     * 1. 提取 base64 编码的 data 字段
     * 2. 解码为 JSON
     * 3. 修改变量值
     * 4. 重新编码为 base64
     * 5. 替换回 HTML
     *
     * @param data HTML 格式数据
     * @param replacements 替换映射表
     * @return 替换后的数据
     */
    private fun replaceInHtmlFormat(data: ByteArray, replacements: Map<String, String>): ByteArray {
        return try {
            val htmlStr = data.toString(StandardCharsets.UTF_8)

            // 查找 "data":" 的位置，提取 base64 编码的 JSON
            val dataPrefix = "\"data\":\""
            val dataStart = htmlStr.indexOf(dataPrefix)
            if (dataStart < 0) return data

            val base64Start = dataStart + dataPrefix.length
            val base64End = htmlStr.indexOf("\"", base64Start)
            if (base64End < 0) return data

            val base64Data = htmlStr.substring(base64Start, base64End)

            // 解码 base64
            val jsonBytes = java.util.Base64.getDecoder().decode(base64Data)
            var jsonStr = jsonBytes.toString(StandardCharsets.UTF_8)

            // 对 JSON 进行变量替换
            for ((variableName, actualValue) in replacements) {
                // 在 JSON 中查找并替换
                // 找到 "name":"variableName" 的位置，然后向前找 "10:" 并替换
                jsonStr = replaceVariableInJson(jsonStr, variableName, actualValue)
            }

            // 重新编码为 base64
            val newBase64 = java.util.Base64.getEncoder().encodeToString(jsonStr.toByteArray(StandardCharsets.UTF_8))

            // 替换回 HTML
            val newHtml = htmlStr.substring(0, base64Start) + newBase64 + htmlStr.substring(base64End)

            newHtml.toByteArray(StandardCharsets.UTF_8)
        } catch (e: Exception) {
            // 替换失败，返回原数据
            println("HTML Format 替换失败: ${e.message}")
            data
        }
    }

    /**
     * 在 JSON 字符串中替换变量值
     *
     * @param json JSON 字符串
     * @param variableName 变量名
     * @param actualValue 实际值
     * @return 替换后的 JSON
     */
    private fun replaceVariableInJson(json: String, variableName: String, actualValue: String): String {
        // 查找 "name":"variableName" 的位置
        val namePattern = "\"name\":\"$variableName\""
        val namePos = json.indexOf(namePattern)
        if (namePos < 0) return json

        // 在 namePos 之前找到最近的 "10:" （空值）
        val oldValuePattern = "\"10:\""
        var lastPos = -1
        var searchStart = 0
        while (true) {
            val pos = json.indexOf(oldValuePattern, searchStart)
            if (pos < 0 || pos > namePos) break
            lastPos = pos
            searchStart = pos + 1
        }

        if (lastPos < 0) return json

        // 替换这个位置的值
        val newValue = "\"10:$actualValue\""
        return json.substring(0, lastPos) + newValue + json.substring(lastPos + oldValuePattern.length)
    }

    /**
     * 根据变量名替换影刀指令中的值
     *
     * 查找 "name":"variableName" 的位置，然后向前找到最近的 "10:" 并替换为 "10:actualValue"
     *
     * @param data 原始数据
     * @param variableName 变量名
     * @param actualValue 实际值
     * @param charset 字符编码
     * @return 替换后的数据
     */
    private fun replaceVariableValue(
        data: ByteArray,
        variableName: String,
        actualValue: String,
        charset: java.nio.charset.Charset
    ): ByteArray {
        return try {
            // 构建变量名模式: "name":"variableName"
            val namePattern = "\"name\":\"$variableName\"".toByteArray(charset)

            // 查找变量名的位置
            val namePos = findPattern(data, namePattern, 0)
            if (namePos < 0) {
                return data
            }

            // 构建旧值模式和新值
            // 旧值模式: "10:" （表示空值，后面紧跟引号）
            val oldValuePattern = "\"10:\"".toByteArray(charset)
            // 新值: "10:actualValue"
            val newValue = "\"10:$actualValue\"".toByteArray(charset)

            // 从变量名位置向前搜索最近的 "10:"
            // 因为 JSON 结构中 inputs 在 outputs 之前
            var lastOldValuePos = -1
            var searchStart = 0

            // 找到变量名之前最后一个 "10:" 的位置
            while (true) {
                val pos = findPattern(data, oldValuePattern, searchStart)
                if (pos < 0 || pos > namePos) {
                    break
                }
                lastOldValuePos = pos
                searchStart = pos + 1
            }

            if (lastOldValuePos < 0) {
                return data
            }

            // 替换这个位置的值
            replaceAtPosition(data, lastOldValuePos, oldValuePattern.size, newValue)
        } catch (e: Exception) {
            // 替换失败，返回原数据
            data
        }
    }

    /**
     * 在数据中查找模式的位置
     *
     * @param data 数据
     * @param pattern 要查找的模式
     * @param startFrom 起始位置
     * @return 模式的位置，找不到返回 -1
     */
    private fun findPattern(data: ByteArray, pattern: ByteArray, startFrom: Int): Int {
        if (pattern.isEmpty() || data.size < pattern.size) {
            return -1
        }

        for (i in startFrom..(data.size - pattern.size)) {
            var matched = true
            for (j in pattern.indices) {
                if (data[i + j] != pattern[j]) {
                    matched = false
                    break
                }
            }
            if (matched) {
                return i
            }
        }
        return -1
    }

    /**
     * 在指定位置替换数据
     *
     * @param data 原始数据
     * @param position 替换位置
     * @param oldLength 旧数据长度
     * @param newValue 新值
     * @return 替换后的数据
     */
    private fun replaceAtPosition(
        data: ByteArray,
        position: Int,
        oldLength: Int,
        newValue: ByteArray
    ): ByteArray {
        val result = ByteArrayOutputStream()
        result.write(data, 0, position)
        result.write(newValue)
        result.write(data, position + oldLength, data.size - position - oldLength)
        return result.toByteArray()
    }
}

/**
 * 可序列化的剪贴板数据
 *
 * 用于将剪贴板数据保存到文件
 */
private data class SerializableClipboardData(
    val formatId: Int,
    val formatName: String,
    val data: ByteArray
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 1L
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as SerializableClipboardData
        return formatId == other.formatId &&
                formatName == other.formatName &&
                data.contentEquals(other.data)
    }

    override fun hashCode(): Int {
        var result = formatId
        result = 31 * result + formatName.hashCode()
        result = 31 * result + data.contentHashCode()
        return result
    }
}
