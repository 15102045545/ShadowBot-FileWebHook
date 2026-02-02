# FileWebHook-App-Framework 指令功能实现计划

## 实现状态: 已完成 ✅

## 需求概述

实现影刀指令的跨平台/跨机器/跨用户的传递，以及影刀指令动态修改/生成功能。

### 核心目标
1. **指令转储** - 开发者将元FileWebHook-App-Framework框架指令从剪贴板保存到文件
2. **动态修改指令** - 根据triggerId、端口、IP、文件路径动态替换指令中的占位符
3. **将指令写入系统粘贴板** - 用户一键复制专属于某触发器的框架指令

---

## 实现步骤

### 第一阶段：新增"开发者功能"页面和指令转储功能

#### 任务1.1：添加导航定义
- 文件：`ui/navigation/Navigation.kt`
- 操作：在Screen枚举中添加 `DEVELOPER("开发者功能")`

#### 任务1.2：更新侧边栏
- 文件：`ui/components/Sidebar.kt`
- 操作：添加开发者功能的导航菜单项（使用Build图标）

#### 任务1.3：创建开发者功能页面
- 新增文件：`ui/screens/developer/DeveloperScreen.kt`
- 功能：
  - 显示"转储指令"按钮
  - 点击后读取系统剪贴板所有格式数据
  - 将数据序列化保存到 `BaseFileWebHookAppFramework` 文件
  - 成功后弹窗通知

#### 任务1.4：创建开发者功能ViewModel
- 新增文件：`ui/screens/developer/DeveloperViewModel.kt`
- 功能：
  - `dumpClipboardInstruction()` 方法
  - 状态管理（loading、success、error）

#### 任务1.5：实现剪贴板读取工具类
- 新增文件：`domain/clipboard/ClipboardManager.kt`
- 功能：
  - `readAllClipboardFormats()`: 读取剪贴板所有格式数据
  - `writeClipboardData()`: 将指令数据写入剪贴板
  - 使用JNA调用Windows API (user32.dll, kernel32.dll)
  - 支持自定义格式ID的注册和映射
- 参考Python脚本的逻辑

#### 任务1.6：更新App.kt
- 文件：`ui/App.kt`
- 操作：添加DEVELOPER页面的路由处理

---

### 第二阶段：触发器页面添加"复制框架指令"按钮

#### 任务2.1：修改触发器列表页面
- 文件：`ui/screens/trigger/TriggerListScreen.kt`
- 操作：在每行触发器添加"复制FileWebHook-App-Framework"按钮

#### 任务2.2：更新TriggerViewModel
- 文件：`ui/screens/trigger/TriggerViewModel.kt`
- 操作：
  - 添加 `copyFrameworkInstruction(triggerId: Long)` 方法
  - 读取BaseFileWebHookAppFramework文件
  - 替换占位符 `${triggerId}`, `${triggerFilesPath}`, `${serverIpAndPort}`
  - 调用ClipboardManager写入剪贴板
  - 处理成功/失败状态

#### 任务2.3：添加复制成功提示
- 在TriggerListScreen中添加Snackbar或Dialog显示复制结果

---

### 第三阶段：DI注入和整合

#### 任务3.1：注册ClipboardManager到Koin
- 文件：`di/Modules.kt`
- 操作：在domainModule中注册ClipboardManager单例

---

## 技术要点

### 剪贴板格式处理

#### 从Python脚本学到的关键点：
1. **读取剪贴板** (`ShadowbotAppFormwork_InstructDataCopy.py`)
   - 使用 `EnumClipboardFormats` 枚举所有格式
   - 对每个格式调用 `GetClipboardData` 获取数据
   - 使用 `GetClipboardFormatName` 获取格式名称
   - 将数据和元信息（format_id, format_name, data）序列化保存

2. **写入剪贴板** (`ShadowbotAppFormwork_InstructDataPaste.py`)
   - 读取序列化的数据
   - 对于自定义格式（format_id >= 0xC000 即 49152）：
     - 需要用格式名称调用 `RegisterClipboardFormat` 获取当前机器的正确ID
     - 不同机器分配的格式ID可能不同，必须通过名称重新注册
   - 调用 `SetClipboardData` 设置每种格式的数据

### Kotlin/JNA实现

```kotlin
// Windows API调用
interface User32 : StdCallLibrary {
    fun OpenClipboard(hWndNewOwner: Pointer?): Boolean
    fun CloseClipboard(): Boolean
    fun EmptyClipboard(): Boolean
    fun EnumClipboardFormats(format: Int): Int
    fun GetClipboardData(uFormat: Int): Pointer?
    fun SetClipboardData(uFormat: Int, hMem: Pointer?): Pointer?
    fun GetClipboardFormatNameW(format: Int, lpszFormatName: CharArray, cchMaxCount: Int): Int
    fun RegisterClipboardFormatW(lpszFormat: WString): Int
}

interface Kernel32 : StdCallLibrary {
    fun GlobalAlloc(uFlags: Int, dwBytes: Int): Pointer?
    fun GlobalLock(hMem: Pointer?): Pointer?
    fun GlobalUnlock(hMem: Pointer?): Boolean
    fun GlobalSize(hMem: Pointer?): Int
}
```

### 数据序列化格式

使用Kotlin Serialization将剪贴板数据保存为二进制格式：
```kotlin
@Serializable
data class ClipboardFormatData(
    val formatId: Int,
    val formatName: String,
    val data: ByteArray
)

@Serializable
data class ClipboardData(
    val formats: List<ClipboardFormatData>
)
```

### 占位符替换
```kotlin
fun processTemplate(template: String, triggerId: Long, port: Int, triggerFilesPath: String): String {
    return template
        .replace("\${triggerId}", triggerId.toString())
        .replace("\${triggerFilesPath}", triggerFilesPath)
        .replace("\${serverIpAndPort}", "http://127.0.0.1:$port")
}
```

---

## 文件清单

### 新增文件
1. `ui/screens/developer/DeveloperScreen.kt` - 开发者功能页面
2. `ui/screens/developer/DeveloperViewModel.kt` - 开发者功能ViewModel
3. `domain/clipboard/ClipboardManager.kt` - 剪贴板管理工具类

### 修改文件
1. `ui/navigation/Navigation.kt` - 添加DEVELOPER页面定义
2. `ui/components/Sidebar.kt` - 添加开发者功能导航项
3. `ui/App.kt` - 添加DEVELOPER页面路由
4. `ui/screens/trigger/TriggerListScreen.kt` - 添加复制按钮
5. `ui/screens/trigger/TriggerViewModel.kt` - 添加复制逻辑
6. `di/Modules.kt` - 注册ClipboardManager

---

## 依赖要求

需要添加JNA依赖用于调用Windows API：
```kotlin
implementation("net.java.dev.jna:jna:5.14.0")
implementation("net.java.dev.jna:jna-platform:5.14.0")
```

---

## 执行顺序

1. 先添加JNA依赖
2. 实现ClipboardManager（最核心的部分）
3. 创建开发者功能页面和ViewModel
4. 更新导航和路由
5. 修改触发器页面添加复制按钮
6. 测试完整流程
