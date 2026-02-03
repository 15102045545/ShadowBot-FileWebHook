# Python+Kotlin 重构执行计划

## 1. 概述

### 1.1 需求背景
FileWebHook 项目需要实现"影刀指令的跨机器传递、动态修改及复制"功能。目前该功能由 Kotlin 代码实现，但 Kotlin 在处理复杂自定义格式的剪贴板文件时不够灵活。本次重构将核心逻辑迁移到 Python，利用 Python 强大的脚本处理能力，同时保留 Kotlin 的 UI 和控制逻辑。

### 1.2 重构目标
- **Python 负责**: 指令文件转储、动态修改、剪贴板读写
- **Kotlin 负责**: UI 展示、控制逻辑、Python 脚本调用

### 1.3 核心流程
```
影刀应用 → 复制指令 → 系统剪贴板 → Python读取 → BaseFileWebHookAppFramework
                                                              ↓
用户点击"复制框架指令" → Python修改变量 → ShadowbotAppFormwork_InstructData → Python写入剪贴板
                                                                                      ↓
                                                                              用户粘贴到影刀
```

---

## 2. 文件结构规划

### 2.1 Python 脚本 (composeApp/src/commonMain/kotlin/shadowbot/)
```
shadowbot/
├── BaseFileWebHookAppFramework          # 元指令文件（已存在，由转储功能生成）
├── Python-Kotlin-Refactoring-Plan.md    # 本执行计划文档
├── dump_clipboard_to_file.py            # [新建] 剪贴板转储到文件
├── modify_and_save_instructions.py      # [新建] 修改指令并保存到指定路径
├── load_instructions_to_clipboard.py    # [新建] 从文件加载指令到剪贴板
├── parse_shadowbot_blocks.py            # [已存在] 解析影刀指令(参考)
├── modify_instructions.py               # [已存在] 修改指令(参考)
└── *.py (其他参考脚本)
```

### 2.2 Kotlin 文件
```
domain/
├── python/
│   ├── ShadowBotPythonFinder.kt        # [新建] 影刀Python解释器查找器(Kotlin实现)
│   └── PythonExecutor.kt               # [新建] Python脚本执行器
├── clipboard/
│   └── ClipboardManager.kt             # [简化] 移除复杂逻辑，仅保留接口定义
data/
├── model/
│   └── AppSettings.kt                  # [修改] 添加 pythonInterpreterPath 字段
├── repository/
│   └── SettingsRepository.kt           # [修改] 支持Python路径配置
ui/screens/
├── settings/
│   ├── SettingsScreen.kt               # [修改] 添加Python路径配置UI
│   └── SettingsViewModel.kt            # [修改] 添加Python路径管理
├── developer/
│   └── DeveloperViewModel.kt           # [修改] 使用Python脚本转储
└── trigger/
    └── TriggerViewModel.kt             # [修改] 使用Python脚本复制框架指令
```

---

## 3. 详细实现步骤

### 步骤 1: 查找影刀 Python 解释器 (Kotlin 实现)

**文件**: `domain/python/ShadowBotPythonFinder.kt`

**功能**:
- 使用 Kotlin 文件 API 自动扫描 `C:\Program Files (x86)\ShadowBot\` 目录
- 找到最新版本的影刀安装目录
- 定位 `python310` 或类似的 Python 解释器目录
- 返回 `python.exe` 的完整路径

**注意**: 必须使用 Kotlin 而非 Python 实现，因为需要先找到 Python 才能执行 Python 脚本

**核心方法**:
```kotlin
data class PythonFinderResult(
    val success: Boolean,
    val pythonPath: String,
    val version: String,
    val message: String
)

class ShadowBotPythonFinder {
    fun findShadowBotPython(): PythonFinderResult
}
```

### 步骤 2: 指令转储功能

**文件**: `dump_clipboard_to_file.py`

**功能**:
- 读取系统剪贴板中的所有格式数据
- 使用 pickle 序列化保存到指定文件
- 对应原 `ShadowbotAppFormwork_InstructDataCopy.py`

**输入参数** (命令行):
- `--output`: 输出文件路径 (默认: `BaseFileWebHookAppFramework`)

**输出**: JSON格式的结果
```json
{
  "success": true,
  "message": "成功保存 8 种格式到文件",
  "formatCount": 8,
  "outputPath": "..."
}
```

### 步骤 3: 指令修改并保存功能

**文件**: `modify_and_save_instructions.py`

**功能**:
- 读取 `BaseFileWebHookAppFramework` 元指令文件
- 解析 `ShadowBot.Flow.Blocks` 格式中的 JSON
- 找到 `triggerId`、`triggerFilesPath`、`serverIpAndPort` 三个变量
- 修改其值为传入的参数
- 将修改后的数据保存到指定路径的 `ShadowbotAppFormwork_InstructData` 文件

**输入参数** (命令行):
- `--source`: 元指令文件路径
- `--output`: 输出目录路径
- `--trigger-id`: 触发器ID
- `--trigger-files-path`: 触发器文件路径
- `--server-ip-port`: 服务器IP和端口 (如 `http://127.0.0.1:8089`)

**输出**: JSON格式的结果
```json
{
  "success": true,
  "message": "指令修改并保存成功",
  "outputPath": "..."
}
```

### 步骤 4: 指令加载到剪贴板功能

**文件**: `load_instructions_to_clipboard.py`

**功能**:
- 读取指定路径的 `ShadowbotAppFormwork_InstructData` 文件
- 将数据恢复到系统剪贴板
- 处理自定义剪贴板格式ID的映射
- 对应原 `ShadowbotAppFormwork_InstructDataPaste.py`

**输入参数** (命令行):
- `--input`: 输入文件路径

**输出**: JSON格式的结果
```json
{
  "success": true,
  "message": "成功恢复 8/8 种格式到剪贴板",
  "successCount": 8,
  "totalCount": 8
}
```

### 步骤 5: Kotlin PythonExecutor 服务

**文件**: `domain/python/PythonExecutor.kt`

**功能**:
- 管理 Python 解释器路径
- 执行 Python 脚本并捕获输出
- 解析 JSON 格式的返回结果
- 处理执行错误

**核心方法**:
```kotlin
class PythonExecutor(
    private val settingsRepository: SettingsRepository
) {
    // 获取Python解释器路径
    suspend fun getPythonPath(): String

    // 执行Python脚本
    suspend fun execute(
        scriptName: String,
        vararg args: String
    ): PythonResult

    // 查找影刀Python解释器
    suspend fun findShadowBotPython(): PythonResult

    // 转储剪贴板指令
    suspend fun dumpClipboardToFile(outputPath: String): PythonResult

    // 修改并保存指令
    suspend fun modifyAndSaveInstructions(
        sourcePath: String,
        outputDir: String,
        triggerId: Long,
        triggerFilesPath: String,
        serverIpAndPort: String
    ): PythonResult

    // 加载指令到剪贴板
    suspend fun loadInstructionsToClipboard(inputPath: String): PythonResult
}

data class PythonResult(
    val success: Boolean,
    val message: String,
    val data: Map<String, Any>? = null
)
```

### 步骤 6: 更新 AppSettings 数据模型

**文件**: `data/model/AppSettings.kt`

**修改内容**:
- 添加 `pythonInterpreterPath: String` 字段
- 更新 DEFAULT 默认值为空字符串 (表示自动检测)

### 步骤 7: 更新 SettingsRepository

**文件**: `data/repository/SettingsRepository.kt`

**修改内容**:
- 添加 `pythonInterpreterPath` 的读写支持
- 更新 `initializeDefaultSettings()` 方法

### 步骤 8: 更新设置界面

**文件**: `ui/screens/settings/SettingsScreen.kt`

**修改内容**:
- 添加 Python 解释器路径配置卡片
- 支持手动输入路径或自动检测
- 显示当前检测到的路径

### 步骤 9: 更新 DeveloperViewModel

**文件**: `ui/screens/developer/DeveloperViewModel.kt`

**修改内容**:
- 注入 `PythonExecutor`
- `dumpClipboardInstruction()` 方法改为调用 Python 脚本

### 步骤 10: 更新 TriggerViewModel

**文件**: `ui/screens/trigger/TriggerViewModel.kt`

**修改内容**:
- 注入 `PythonExecutor`
- `copyFrameworkInstruction()` 方法改为:
  1. 调用 Python 脚本修改并保存指令
  2. 调用 Python 脚本加载指令到剪贴板

### 步骤 11: 简化 ClipboardManager

**文件**: `domain/clipboard/ClipboardManager.kt` 及其实现

**修改内容**:
- 移除复杂的 JNA 调用逻辑
- 保留基础接口定义
- 所有实际剪贴板操作转移到 Python 脚本

### 步骤 12: 更新依赖注入配置

**文件**: `di/AppModule.kt`

**修改内容**:
- 添加 `PythonExecutor` 的单例注册
- 更新相关 ViewModel 的依赖

---

## 4. 影刀指令数据格式说明

### 4.1 剪贴板数据结构
影刀复制的指令包含多种剪贴板格式，最关键的是:
- `ShadowBot.Flow.Blocks`: 包含指令的核心 JSON 数据
- `HTML Format`: 包含 base64 编码的相同 JSON 数据

### 4.2 JSON 指令结构
```json
[
  {
    "id": "uuid",
    "name": "programing.variable",
    "isEnabled": true,
    "inputs": {
      "value_type": {"value": "10:str"},
      "value": {"value": "10:"}  // 这里的 "10:" 后面是空的，需要填充
    },
    "outputs": {
      "variable": {
        "name": "triggerId",  // 变量名
        "type": "str",
        "isEnable": true
      }
    }
  }
]
```

### 4.3 变量替换逻辑
1. 找到 `"name":"variableName"` 的位置 (在 outputs 中)
2. 向前搜索最近的 `"10:"` (在 inputs 中，表示空值)
3. 将 `"10:"` 替换为 `"10:actualValue"`

需要替换的三个变量:
- `triggerId`: 触发器ID
- `triggerFilesPath`: 触发器文件路径 (如 `C:\Users\xxx\.filewebhook\triggerFiles\`)
- `serverIpAndPort`: 服务器地址 (如 `http://127.0.0.1:8089`)

---

## 5. 测试计划

### 5.1 单元测试
- Python 脚本独立测试
- Kotlin PythonExecutor 测试

### 5.2 集成测试
1. **转储测试**: 在影刀中复制指令 → 运行转储 → 验证文件生成
2. **复制测试**: 点击复制框架指令 → 在影刀中粘贴 → 验证指令正确

### 5.3 边界测试
- Python 解释器不存在的处理
- 剪贴板为空的处理
- 文件权限问题的处理

---

## 6. 风险与注意事项

### 6.1 技术风险
- **win32clipboard 依赖**: 需要确保影刀的 Python 环境中有此模块
- **编码问题**: 影刀指令中可能包含中文，需要正确处理 UTF-8 编码
- **路径问题**: Windows 路径需要使用正斜杠或转义

### 6.2 兼容性
- 影刀版本更新可能导致 Python 路径变化
- 剪贴板格式可能随影刀版本变化

### 6.3 安全性
- Python 脚本执行需要注意命令注入风险
- 文件路径需要验证防止路径遍历攻击

---

## 7. 执行顺序

1. ✅ 生成执行计划文档 (本文档)
2. ✅ 创建 `ShadowBotPythonFinder.kt` (Kotlin查找Python解释器)
3. ✅ 创建 `dump_clipboard_to_file.py`
4. ✅ 创建 `modify_and_save_instructions.py`
5. ✅ 创建 `load_instructions_to_clipboard.py`
6. ✅ 创建 `PythonExecutor.kt`
7. ✅ 更新 `AppSettings.kt`
8. ✅ 更新 `SettingsRepository.kt`
9. ✅ 更新 `SettingsScreen.kt` 和 `SettingsViewModel.kt`
10. ✅ 更新 `DeveloperViewModel.kt`
11. ✅ 更新 `TriggerViewModel.kt`
12. ✅ 简化 `ClipboardManager.kt`
13. ✅ 更新 `AppModule.kt`
14. ✅ 编译测试 - BUILD SUCCESSFUL

---

*文档创建时间: 2026-02-03*
*版本: V1.4.6*
