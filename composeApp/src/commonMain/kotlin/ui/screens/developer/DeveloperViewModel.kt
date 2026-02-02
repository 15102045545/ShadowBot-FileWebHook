/**
 * 开发者功能 ViewModel
 *
 * 本文件提供开发者功能页面的状态管理和业务逻辑
 * 主要功能：
 * 1. 将系统剪贴板中的影刀指令转储到文件
 */

package ui.screens.developer

import androidx.compose.runtime.*
import domain.clipboard.ClipboardManager
import domain.clipboard.ClipboardResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 开发者功能 ViewModel 类
 *
 * 管理开发者功能页面的 UI 状态和业务逻辑
 *
 * @property clipboardManager 剪贴板管理器
 */
class DeveloperViewModel(
    private val clipboardManager: ClipboardManager
) {
    companion object {
        /**
         * BaseFileWebHookAppFramework 文件保存路径
         * 相对于项目根目录
         */
        private const val BASE_FRAMEWORK_RELATIVE_PATH =
            "composeApp/src/commonMain/kotlin/shadowbot/BaseFileWebHookAppFramework"
    }

    /** 加载中状态 */
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    /** 操作结果消息 */
    private val _resultMessage = mutableStateOf<String?>(null)
    val resultMessage: State<String?> = _resultMessage

    /** 操作是否成功 */
    private val _isSuccess = mutableStateOf(false)
    val isSuccess: State<Boolean> = _isSuccess

    /** 是否显示结果对话框 */
    private val _showResultDialog = mutableStateOf(false)
    val showResultDialog: State<Boolean> = _showResultDialog

    /**
     * 转储系统剪贴板中的影刀指令到文件
     *
     * 将剪贴板中的所有格式数据（包括影刀自定义格式）
     * 序列化保存到 BaseFileWebHookAppFramework 文件
     *
     * @param projectRootPath 项目根目录路径
     */
    suspend fun dumpClipboardInstruction(projectRootPath: String) {
        _isLoading.value = true
        try {
            val savePath = "$projectRootPath/$BASE_FRAMEWORK_RELATIVE_PATH"

            val result = withContext(Dispatchers.IO) {
                clipboardManager.dumpClipboardToFile(savePath)
            }

            when (result) {
                is ClipboardResult.Success -> {
                    _resultMessage.value = result.message
                    _isSuccess.value = true
                }
                is ClipboardResult.Error -> {
                    _resultMessage.value = result.message
                    _isSuccess.value = false
                }
            }
            _showResultDialog.value = true
        } catch (e: Exception) {
            _resultMessage.value = "转储失败: ${e.message}"
            _isSuccess.value = false
            _showResultDialog.value = true
        } finally {
            _isLoading.value = false
        }
    }

    /**
     * 关闭结果对话框
     */
    fun dismissResultDialog() {
        _showResultDialog.value = false
    }
}
