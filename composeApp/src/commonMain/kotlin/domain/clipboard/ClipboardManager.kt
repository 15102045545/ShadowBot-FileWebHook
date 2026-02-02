/**
 * 剪贴板管理器 - 公共接口
 *
 * 本文件定义了剪贴板操作的 expect 声明
 * 具体实现在各平台的 actual 实现中（如 desktopMain）
 *
 * 用于实现影刀指令的跨平台传递功能
 */

package domain.clipboard

/**
 * 剪贴板格式数据
 *
 * 存储单个剪贴板格式的完整信息
 *
 * @property formatId 格式ID（Windows系统分配）
 * @property formatName 格式名称（自定义格式时使用）
 * @property data 格式数据（二进制）
 */
data class ClipboardFormatData(
    val formatId: Int,
    val formatName: String,
    val data: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as ClipboardFormatData
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

/**
 * 剪贴板操作结果
 */
sealed class ClipboardResult {
    /** 操作成功 */
    data class Success(val message: String) : ClipboardResult()

    /** 操作失败 */
    data class Error(val message: String) : ClipboardResult()
}

/**
 * 剪贴板管理器
 *
 * 用于读取和写入系统剪贴板数据
 * 支持影刀自定义剪贴板格式的处理
 * 这是 Kotlin Multiplatform 的 expect/actual 模式
 */
expect class ClipboardManager() {
    /**
     * 读取剪贴板中的所有格式数据并保存到文件
     *
     * 功能等同于 Python 脚本 ShadowbotAppFormwork_InstructDataCopy.py
     *
     * @param savePath 保存文件的路径
     * @return 操作结果
     */
    fun dumpClipboardToFile(savePath: String): ClipboardResult

    /**
     * 从文件读取数据并写入剪贴板
     *
     * 功能等同于 Python 脚本 ShadowbotAppFormwork_InstructDataPaste.py
     *
     * @param sourcePath 源数据文件路径
     * @param replacements 占位符替换映射表（用于动态修改指令参数）
     * @return 操作结果
     */
    fun restoreClipboardFromFile(
        sourcePath: String,
        replacements: Map<String, String> = emptyMap()
    ): ClipboardResult

    /**
     * 从源文件读取数据，替换变量后写入目标文件，最后从目标文件恢复到剪贴板
     *
     * 流程：读取元指令 → 替换变量 → 写入临时文件 → 从临时文件读取 → 写入剪贴板
     *
     * @param sourcePath 源数据文件路径（元指令文件）
     * @param targetPath 目标文件路径（填充后的指令文件）
     * @param replacements 变量替换映射表
     * @return 操作结果
     */
    fun copyWithReplacementsViaFile(
        sourcePath: String,
        targetPath: String,
        replacements: Map<String, String>
    ): ClipboardResult
}
