/**
 * 开发者功能 ViewModel
 *
 * 本文件提供开发者功能页面的状态管理和业务逻辑
 * 主要功能：
 * 1. 将系统剪贴板中的影刀指令转储到文件（使用 Python 脚本）
 *
 * 注意：开发者功能（指令转储）仅在开发环境中可用，打包后的软件不包含此功能
 */

package ui.screens.developer

import androidx.compose.runtime.*
import domain.python.PythonExecutor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 开发者功能 ViewModel 类
 *
 * 管理开发者功能页面的 UI 状态和业务逻辑
 *
 * @property pythonExecutor Python 脚本执行器
 */
class DeveloperViewModel(
    private val pythonExecutor: PythonExecutor
) {
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
     * 检查是否在开发环境中运行
     *
     * @return true 如果是开发环境，false 如果是生产环境（打包后）
     */
    fun isDevEnvironment(): Boolean {
        return pythonExecutor.isDevEnvironment()
    }

    /**
     * 转储系统剪贴板中的影刀指令到文件
     *
     * 使用 Python 脚本将剪贴板中的所有格式数据（包括影刀自定义格式）
     * 序列化保存到 BaseFileWebHookAppFramework.pkl 文件
     *
     * 开发环境：保存到 desktopMain/resources/scripts 目录，打包时会被包含到安装包
     * 生产环境：此功能不可用
     */
    suspend fun dumpClipboardInstruction() {
        _isLoading.value = true
        try {
            // 获取开发环境的保存路径
            val savePath = pythonExecutor.getDevResourcesSavePath()

            if (savePath == null) {
                _resultMessage.value = "指令转储功能仅在开发环境中可用"
                _isSuccess.value = false
                _showResultDialog.value = true
                return
            }

            val result = withContext(Dispatchers.IO) {
                pythonExecutor.dumpClipboardToFile(savePath)
            }

            _resultMessage.value = result.message
            _isSuccess.value = result.success
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
