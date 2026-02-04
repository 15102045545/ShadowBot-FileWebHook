# FileWebHook

<p align="center">
  <img src="docs/images/logo.png" alt="FileWebHook Logo" width="120" height="120">
</p>

<p align="center">
  <strong>将 HTTP 请求转换为文件触发器的中间件</strong>
</p>

<p align="center">
  <a href="功能特性">功能特性</a> •
  <a href="快速开始">快速开始</a> •
  <a href="使用指南">使用指南</a> •
  <a href="api-文档">API 文档</a> •
  <a href="技术架构">技术架构</a> •
  <a href="贡献指南">贡献指南</a>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Windows-blue" alt="Platform">
  <img src="https://img.shields.io/badge/Kotlin-2.0.21-purple" alt="Kotlin">
  <img src="https://img.shields.io/badge/Compose-1.7.1-green" alt="Compose">
  <img src="https://img.shields.io/badge/License-MIT-yellow" alt="License">
</p>

---

## 简介

FileWebHook 是一个桌面中间件应用，专为 **影刀 RPA** 设计，解决社区版影刀的应用不支持被 HTTP 请求触发的问题。


有了 FileWebHook , 您可以让您的影刀应用被任何地区,任何语言,任何平台的服务通过 HTTP 请求轻松触发。
## 功能特性

- **将影刀原生触发器高度封装为http触发器** - 开箱即用,开发者仅需要在自己的rpa应用,从上下文件获取http入参即可,无需编写读取文件获取参数的指令
- **自动回调机制** - 影刀执行完成后自动回调结果给外部服务,开发者无需编写回调指令
- **多客户端** - 支持多客户端的http请求
- **安全验证** - 支持基于API Key的请求验证,确保只有授权客户端可以触发影刀任务
- **参数传递** - 支持任意类型的参数传递给影刀任务,包括字符串,数字,布尔值和JSON对象
- **执行记录追踪** - 完整记录每次执行的请求、响应和耗时统计
- **本地数据库存储** - 使用 SQLite 存储用户,触发器,执行记录,设置等信息
- **FIFO 任务队列** - 串行执行，确保任务有序处理
- **现代化 UI** - 基于 Compose Multiplatform 的原生桌面界面

## 截图

<p >
  <img src="docs/images/img.png" alt="" >
  <img src="docs/images/img_2.png" alt="" >
  <img src="docs/images/img_1.png" alt="" >
</p>


## 使用指南
图文:

[从0到1快速使用FileWebHook.md](./docs/use/从0到1快速使用FileWebHook.md)

[FileWebHook常见使用问题.md](./docs/use/FileWebHook常见使用问题.md)

视频:

[从0到1快速使用FileWebHook.bilibili](https://www.bilibili.com/video/BV18z6UBHE5P)

[FileWebHook流程和原理讲解.bilibili](https://www.bilibili.com/video/BV18z6UBHE5P)

## 快速开始

### 可以 直接下载安装包使用

从 [Releases](https://github.com/15102045545/ShadowBot-FileWebHook/releases) 页面下载对应平台的安装包：

| 平台 | 文件                      |
|------|-------------------------|
| Windows | `FileWebHook-1.0.0.msi` |

### 或 从源码构建开始使用

```bash
# 克隆仓库
git clone https://github.com/15102045545/ShadowBot-FileWebHook.git
cd ShadowBot-FileWebHook

# 运行应用（开发模式）
./gradlew :composeApp:run
# Windows: gradlew.bat :composeApp:run

# 打包 MSI 安装程序
./gradlew :composeApp:packageMsi
# Windows: gradlew.bat :composeApp:packageMsi
# 输出位置: composeApp/build/compose/binaries/main/msi/FileWebHook-x.x.x.msi
```

#### 打包说明

| 命令 | 说明 | 输出位置 |
|------|------|----------|
| `gradlew :composeApp:run` | 运行应用（开发模式） | - |
| `gradlew :composeApp:packageMsi` | 打包 Windows MSI 安装包 | `composeApp/build/compose/binaries/main/msi/` |

**打包要求：**
- JDK 17 或更高版本
- WiX Toolset 3.11（首次打包时 Gradle 会自动下载，如网络问题可手动下载放置到 `C:\Program Files\wix311-binaries`）

**安装包特性：**
- 自动创建桌面快捷方式
- 支持通过控制面板卸载

### 系统要求

- **操作系统**: Windows 10+
- **影刀 RPA**: 需安装影刀客户端

## 技术架构

### 技术栈

| 类别 | 技术 | 版本 |
|------|------|------|
| 语言 | Kotlin | 2.0.21 |
| UI 框架 | Compose Multiplatform | 1.7.1 |
| HTTP 框架 | Ktor | 3.0.2 |
| 数据库 | SQLDelight + SQLite | 2.0.2 |
| 依赖注入 | Koin | 4.0.0 |
| 序列化 | Kotlinx Serialization | 1.7.3 |

### 项目结构

```
composeApp/src/
├── commonMain/kotlin/
│   ├── client/              # HTTP 客户端层
│   │   └── CallbackClient   # 回调外部服务的 HTTP 客户端
│   ├── data/                # 数据层
│   │   ├── model/           # 数据模型 (User, Trigger, ExecutionLog, Settings)
│   │   └── repository/      # 数据库仓库 (CRUD 操作)
│   ├── di/                  # 依赖注入
│   │   └── AppModule        # Koin 模块定义
│   ├── domain/              # 领域层（业务逻辑）
│   │   ├── clipboard/       # 剪贴板操作（影刀指令复制）
│   │   ├── python/          # Python 脚本执行
│   │   ├── queue/           # FIFO 任务队列
│   │   └── service/         # 业务服务 (FileService, RequestValidator)
│   ├── server/              # HTTP 服务器层
│   │   ├── plugins/         # Ktor 插件配置
│   │   ├── routes/          # API 路由 (TriggerRoutes, CallbackRoutes)
│   │   └── FileWebHookServer# 内嵌 HTTP 服务器
│   ├── shadowbot/           # 影刀集成资源
│   │   └── *.py, *.pkl      # Python 脚本和指令模板
│   └── ui/                  # UI 层
│       ├── components/      # 可复用组件 (DataTable, Sidebar, CommonDialogs)
│       ├── navigation/      # 导航定义
│       ├── screens/         # 页面实现
│       │   ├── trigger/     # 触发器管理
│       │   ├── user/        # 用户管理
│       │   ├── userpermission/  # 权限管理
│       │   ├── executionlog/    # 执行日志
│       │   ├── settings/    # 系统设置
│       │   └── developer/   # 开发者工具
│       └── theme/           # 主题配置
├── commonMain/sqldelight/   # SQLDelight 数据库定义
│   └── FileWebHook.sq       # SQL 表结构和查询
└── desktopMain/kotlin/      # 桌面平台实现
    ├── main.kt              # 应用入口
    └── domain/              # 平台特定实现
        ├── clipboard/       # Windows 剪贴板 (JNA)
        └── python/          # 脚本路径提供器
```

### 架构模式

本项目采用 **分层架构 + MVVM** 模式：

```
┌─────────────────────────────────────────────────────────────────┐
│                         UI Layer                                 │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐              │
│  │   Screen    │  │   Screen    │  │   Screen    │  ...         │
│  │  Composable │  │  Composable │  │  Composable │              │
│  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘              │
│         │                │                │                      │
│  ┌──────▼──────┐  ┌──────▼──────┐  ┌──────▼──────┐              │
│  │  ViewModel  │  │  ViewModel  │  │  ViewModel  │              │
│  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘              │
└─────────┼────────────────┼────────────────┼─────────────────────┘
          │                │                │
┌─────────▼────────────────▼────────────────▼─────────────────────┐
│                       Domain Layer                               │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐  │
│  │   TaskQueue     │  │   FileService   │  │ RequestValidator│  │
│  │   (FIFO队列)    │  │   (文件操作)    │  │   (安全校验)    │  │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘  │
│  ┌─────────────────┐  ┌─────────────────┐                       │
│  │ ClipboardManager│  │ PythonExecutor  │                       │
│  │   (剪贴板)      │  │   (脚本执行)    │                       │
│  └─────────────────┘  └─────────────────┘                       │
└─────────────────────────────┬───────────────────────────────────┘
                              │
┌─────────────────────────────▼───────────────────────────────────┐
│                        Data Layer                                │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │                     Repositories                         │    │
│  │  UserRepo │ TriggerRepo │ PermissionRepo │ ExecutionRepo │    │
│  └─────────────────────────┬───────────────────────────────┘    │
│                            │                                     │
│  ┌─────────────────────────▼───────────────────────────────┐    │
│  │              SQLDelight Database (SQLite)                │    │
│  │         users │ triggers │ permissions │ logs │ settings │    │
│  └─────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                       Server Layer                               │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │              FileWebHookServer (Ktor CIO)                │    │
│  │  ┌─────────────────┐        ┌─────────────────┐         │    │
│  │  │  TriggerRoutes  │        │ CallbackRoutes  │         │    │
│  │  │ POST /execute   │        │ POST /triggered │         │    │
│  │  │                 │        │ POST /notify    │         │    │
│  │  └─────────────────┘        └─────────────────┘         │    │
│  └─────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                       Client Layer                               │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │              CallbackClient (Ktor HttpClient)            │    │
│  │         发送执行结果回调到外部服务的回调地址              │    │
│  └─────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────┘
```

### 核心数据流

```
外部服务                    FileWebHook                        影刀 RPA
   │                            │                                 │
   │  POST /trigger/execute     │                                 │
   │ ──────────────────────────>│                                 │
   │                            │                                 │
   │                            │  1. 验证凭证和权限               │
   │                            │  2. 请求入队 (FIFO)             │
   │                            │  3. 写入 request.json           │
   │                            │ ───────────────────────────────>│
   │                            │                                 │
   │                            │      POST /triggered            │
   │                            │<─────────────────────────────── │
   │                            │                                 │
   │                            │      POST /notify               │
   │                            │<─────────────────────────────── │
   │                            │                                 │
   │    回调通知 (执行结果)      │                                 │
   │<────────────────────────── │                                 │
   │                            │                                 │
```



## 贡献指南

欢迎提交 Issue 和 Pull Request！

详见 [CONTRIBUTING.md](CONTRIBUTING.md)

## 许可证

本项目采用 [MIT License](LICENSE) 开源协议。

## 致谢

- [JetBrains](https://www.jetbrains.com/) - Kotlin 和 Compose Multiplatform
- [Ktor](https://ktor.io/) - 异步 HTTP 框架
- [SQLDelight](https://cashapp.github.io/sqldelight/) - 类型安全的 SQL 框架
- [Koin](https://insert-koin.io/) - 轻量级依赖注入框架

---

<p align="center">
  Made with love for RPA automation
</p>
