/**
 * 剪贴板管理器 - 公共接口
 *
 * 本文件定义了剪贴板操作的 expect 声明
 * 核心剪贴板操作已迁移到 Python 脚本实现
 *
 * 注意：此文件保留是为了兼容性，新功能应使用 PythonExecutor
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
 * 注意：核心剪贴板操作已迁移到 Python 脚本
 * 此类保留是为了向后兼容，新代码应使用 PythonExecutor
 *
 * @see domain.python.PythonExecutor
 */
expect class ClipboardManager() {

    /**
     * 从文件读取数据并写入剪贴板
     *
     * @deprecated 请使用 PythonExecutor.loadInstructionsToClipboard()
     * @param sourcePath 源数据文件路径
     * @param replacements 占位符替换映射表（用于动态修改指令参数）
     * @return 操作结果
     */
    fun restoreClipboardFromFile(
        sourcePath: String,
        replacements: Map<String, String> = emptyMap()
    ): ClipboardResult

}
