# ShadowBot-FileWebHook 产品需求文档(PRD)

> 版本：v1.0
> 日期：2026-01-30
> 状态：

---

## 一、项目概述

### 1.1 项目名称
**ShadowBot-FileWebHook**（以下简称 FileWebHook）

### 1.2 项目定位
FileWebHook 是影刀RPA生态中的一个中间件软件，用于解决影刀缺少Web触发器的问题。通过 FileWebHook，外部服务可以以 HTTP 的形式主动调用影刀应用，底层基于影刀的文件触发器实现。

### 1.3 核心价值
- 让外部服务能够通过标准 HTTP 接口调用影刀RPA应用
- 隐藏文件交互细节，对外表现为真正的 Webhook
- 提供完整的身份认证、权限控制和执行记录管理

---

## 二、产品背景

### 2.1 影刀RPA简介
影刀RPA是一个Windows GUI应用，用户可在此软件上编写RPA流程，并将流程发版为应用。影刀支持多种触发器：
- 文件触发器：当本地系统文件新增/删除/修改时触发应用执行
- 邮件触发器：当指定邮箱收到邮件时触发应用执行
- 定时触发器：定时循环执行应用
- 热键触发器：通过快捷键触发应用执行

### 2.2 痛点问题
影刀缺少 **Web触发器**，导致：
- 外部服务无法主动调用影刀应用
- 影刀只能发送HTTP请求，无法接收HTTP请求
- 无法与外部系统进行双向集成

### 2.3 解决方案
FileWebHook 作为中间件，利用影刀现有的文件触发器能力，将 HTTP 请求转换为文件操作，从而实现外部服务对影刀应用的调用。

---

## 三、系统架构

### 3.1 整体架构图

```

┌─────────────────────────────────────────────────────────────────────────────┐
│                              本地机器                                         │
│  ┌─────────────────────────────────────────────────────────────────────┐    │
│  │                        FileWebHook                                  │    │
│  │  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────────────┐     │    │
│  │  │ HTTP     │  │ 任务队列   │  │ 文件管理  │  │   数据库          │     │    │
│  │  │ Server   │  │ (全局)    │  │          │  │ - 用户表          │     │    │
│  │  │ :8089    │  │          │  │          │  │ - 触发器表         │     │    │
│  │  └──────────┘  └──────────┘  └──────────┘  │ - 执行记录表       │     │    │
│  │                                            └──────────────────┘     │    │
│  └─────────────────────────────────────────────────────────────────────┘    │
│                              ▲                                              │
│                              │ 文件读写                                       │
│                              ▼                                              │
│  ┌─────────────────────────────────────────────────────────────────────┐    │
│  │   windows文件路径:triggerFiles\{triggerId}\request.json               │    │
│  └─────────────────────────────────────────────────────────────────────┘    │
│                              ▲                                              │
│                              │ 文件触发器监听                                  │
│                              ▼                                              │
│  ┌─────────────────────────────────────────────────────────────────────┐    │
│  │                         影刀RPA                                      │    │
│  │  ┌──────────────────────────────────────────────────────────────┐   │    │
│  │  │  FileWebHook-APP-Framework (影刀侧框架，已开发完成)              │   │    │
│  │  │  - 读取 request.json 获取参数                                  │   │    │
│  │  │  - 调用FileWebHook通知开始执行                                  │   │    │ 
│  │  │  - 执行业务逻辑                                                │   │    │
│  │  │  - 调用FileWebHook通知执行完成并返回结果                          │   │    │
│  │  └──────────────────────────────────────────────────────────────┘   │    │
│  └─────────────────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────────────────┘
          ▲                                          │
          │ HTTP请求FileWebHook触发器执行接口,返回事件id  │ HTTP回调通知执行结果
          │                                          ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                           外部服务                                           │
│                    (Java/Python/Kotlin/其他外部服务)                          │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 3.2 工作流程

```
步骤1: 外部服务发起请求
       POST FileWebHook API
       Body: 包含1.用户id 2.用户密钥 3.触发器id 4,请求的业务参数(业务请求参数需嵌套到requestParam字段内)
              │
              ▼
步骤2: FileWebHook 验证 和 预处理
       - 验证密钥是否有效
       - 验证该用户是否有权限调用此触发器
       - 无论是否验证成功都是同步返回响应给外部服务,响应格式见API设计部分
       - 验证失败则无后续操作
       - 验证成功则进行步骤3
              │
              ▼
步骤3: 生成eventId(eventId是UUID,全局唯一,1个triggerId请求可以生成无限个eventId)
       - 将这个触发任务写入数据库执行记录表,初始状态为「已收到触发请求」
       - 将这个触发任务放入队列,队列最大长度可以通过FileWebHook的设置页面进行配置
       - 无论是否入队成功, 都调用外部服务POST,相当于告诉外部服务"你这个请求我确实收到了,但是是否能执行,要看我此次返回给你的数据,我会告诉你你这个任务在队列的位置,如果是-1代表我没入队成功,如果是大于等于0的数字,代表你这个任务在队列中的位置"
       - 无论是否入队成功, 都修改执行记录状态 [已入队成功待执行] 或 [入队失败]
              │
              ▼
步骤4: 队列消费，写入文件
       - 修改执行记录状态：「已写入文件待影刀执行」
       - 写入 triggerFiles\{triggerId}\request.json
              │
              ▼
步骤5: 影刀文件触发器监听到文件创建
      影刀回调 FileWebHook 告知我已经监听到了文件创建,接下来我会开始执行流程
      影刀读取 triggerFiles\{triggerId}\request.json 文件内容,里面有 triggerId 和 eventId 和 requestParam
              │
              ▼
步骤6: FileWebHook
       - 收到影刀预响应,记录状态更新：「影刀执行中」
              │
              ▼
步骤7: 影刀执行应用业务逻辑
       - .....
              │
              ▼
步骤8: 影刀执行完毕，第二次调用 FileWebHook,这次是真正的响应
       Body: {triggerId, eventId, responseCode, responseMessage, responseData}
       - 记录状态更新：「影刀执行完成」
              │
              ▼
步骤9: FileWebHook 最终处理
      - 删除 requestParam.json 文件
      - 回调外部服务 POST http://{用户的外部服务地址}...
         Body: {完整执行结果+时间信息}
      - 回调成功：状态更新：「回调用户完成」
      - 回调失败：状态更新：「回调外部服务失败」
```

---

## 四、功能需求

### 4.1 触发器管理

#### 4.1.1 功能描述
管理 FileWebHook 的触发器，每个触发器对应一个影刀应用。

#### 4.1.2 功能规则
| 规则项 | 说明 |
|--------|------|
| 创建 | 支持，点击创建按钮即可，无需填写表单，系统自动生成triggerId（从1递增） |
| 修改 | 不支持 |
| 删除 | 不支持 |
| 查看 | 支持，可查看触发器列表和详情 |

#### 4.1.3 触发器创建流程
1. 用户点击「新建触发器」按钮
2. 弹出表单，填写：名称、描述、备注（可选）
3. 系统自动生成：
   - triggerId（自增数字）
   - 文件夹路径：`{triggerFiles路径}\{triggerId}\`
   - 创建时间
4. 创建成功后，用户需要手动将文件夹路径配置到影刀的文件触发器中

#### 4.1.4 触发器列表展示
- triggerId
- 名称
- 描述
- 创建时间
- 文件夹路径（供用户复制配置到影刀）
- 操作：查看详情、查看执行记录

### 4.2 用户管理

#### 4.2.1 功能描述
管理可以调用 FileWebHook 的外部服务用户，包括身份认证和权限分配。

#### 4.2.2 用户信息
| 字段 | 说明 |
|------|------|
| userId | 用户唯一标识，系统自动生成 |
| 用户名称 | 用户的显示名称 |
| 用户备注 | 备注信息 |
| 密钥 | UUID格式，用于身份验证 |
| 回调地址 | 外部服务的回调地址（IP:端口 或 域名） |
| 触发器权限列表 | 该用户有权调用的触发器列表 |
| 创建时间 | 用户创建时间 |

#### 4.2.3 功能操作
| 操作 | 说明 |
|------|------|
| 新增用户 | 填写名称、备注、回调地址，系统自动生成userId和密钥 |
| 编辑用户 | 可修改名称、备注、回调地址 |
| 重置密钥 | 重新生成UUID密钥 |
| 分配权限 | 勾选该用户可调用的触发器 |
| 删除用户 | 删除用户及其权限配置 |

### 4.3 执行记录

#### 4.3.1 功能描述
记录所有通过 FileWebHook 的请求执行情况。

#### 4.3.2 记录字段
| 字段 | 说明 |
|------|------|
| eventId | 执行事件唯一标识 |
| triggerId | 触发器ID |
| userId | 调用用户ID |
| 入参时间 | 外部服务请求到达的时间 |
| 影刀开始执行时间 | 收到 /trigged 回调的时间 |
| 影刀执行完毕通知时间 | 收到 /notify 回调的时间 |
| 总耗时 | 从入参到回调完成的总时间(ms) |
| 影刀执行耗时 | 影刀实际执行时间(ms) |
| 入参参数 | 外部服务请求的参数(JSON) |
| 出参参数 | 影刀返回的结果参数(JSON) |
| 执行状态 | 当前执行状态 |

#### 4.3.3 执行状态流转
```

```

#### 4.3.4 查看方式
- 在触发器详情页查看该触发器的所有执行记录
- 支持按状态筛选
- 支持按时间范围筛选

### 4.4 系统设置

#### 4.4.1 可配置项
| 配置项                  | 说明                                         | 默认值                            |
|----------------------|--------------------------------------------|--------------------------------|
| triggerFilesPath     | 触发器文件存放路径                                  | {FileWebHook安装路径}\triggerFiles |
| globalQueueMaxLength | 全局队列最大长度                                   | 50                             |
| fileWebHookName      | fileWebHookName,本机名称,1个外部系统可能访问多个fileWebHook          | xiaokeer                       |
| fileWebHookSecretKey | fileWebHookSecretKey 本机密钥,回调外部系统时提供,用于验证身份 | {无默认值,必须由用户手动写入,一般是uuid}                             |

### 4.5 任务队列

#### 4.5.1 功能描述
所有外部服务的请求进入全局任务队列，串行执行。

#### 4.5.2 队列规则
- 全局唯一队列
- FIFO（先进先出）
- 同一时间只有一个任务在执行（因为底层影刀只有一个实例）
- 当前任务完成后（收到 /notify 回调）才消费下一个任务

---

## 五、API设计

### 5.1 外部服务调用接口

#### 5.1.1 触发执行
```
POST /trigger/execute
Request Headers:
  Content-Type: application/json
Request Body:
  {"userId":"","secretKey":"","triggerId":"","requestParam":{}}

响应示例:
当所有验证通过时的返回
{
  "code": "C_0",
  "message": "请求已接受",
  "eventId": "1-1706515200000",
  "triggerId": "1"
}
根据用户id查询不到用户 或 用户提供的secretKey和数据库不匹配时的返回
{
  "code": "C_1",
  "message": "身份验证失败",
  "eventId": "1-1706515200000",
  "triggerId": "1"
}
用户没有权限调用该触发器时的返回
{
  "code": "C_2",
  "message": "无权限",
  "eventId": "1-1706515200000",
  "triggerId": "1"
}
用户访问的触发器不存在时的返回
{
  "code": "C_3",
  "message": "触发器不存在",
  "eventId": "1-1706515200000",
  "triggerId": "1"
}
用户提供的requestParam不通过安全校验时的返回
{
  "code": "C_4",
  "message": "非法业务参数",
  "eventId": "1-1706515200000",
  "triggerId": "1"
}
```

### 5.2 影刀回调接口

#### 5.2.1 通知开始执行
```
POST /trigged

Request Body:
{
  "triggerId": "1",
  "eventId": "1-1706515200000"
}

Response (200):
{
  "code": 200,
  "message": "已知晓"
}
```

#### 5.2.2 通知执行完成
```
POST /notify

//如果影刀执行成功了
Request Body:
{
  "responseCode": "200",
  "responseMessage": "应用执行成功",
  "triggerId": "1",
  "eventId": "1-1706515200000",
  "responseData": {
    "任意业务返回字段": "值"
  }
}
//如果影刀执行失败了
Request Body:
{
  "responseCode": "500",
  "responseMessage": "{错误描述}",
  "triggerId": "1",
  "eventId": "1-1706515200000"
}

Response (200):
{
  "code": 200,
  "message": "已接收执行结果"
}
```

### 5.3 回调外部服务

#### 5.3.1 执行结果回调
```
POST http://{用户表的回调地址}/{fileWebHookName}/filewebhook/notify

Request Body:
{
  "fileWebHookName":"", //读取设置页面的fileWebHookName
  "fileWebHookSecretKey":"", //读取设置页面的fileWebHookSecretKey
  "triggerId": "1",
  "eventId": "1-1706515200000",
  "responseCode": "200",
  "responseMessage": "ok",
  "responseData": {
    "任意业务返回字段": "值"
  },
  "requestTime": "2026-01-29T10:00:00.000Z",
  "shadowBotStartTime": "2026-01-29T10:00:01.000Z",
  "shadowBotEndTime": "2026-01-29T10:00:05.000Z",
  "totalDuration": 5500,
  "shadowBotDuration": 4000
}
```

---

## 六、数据模型

### 6.1 用户表 (users)

| 字段名         | 类型 | 必填 | 说明 |
|-------------|------|------|------|
| id          | INTEGER | 是 | 主键，自增 |
| userId      | VARCHAR(36) | 是 | 用户唯一标识(UUID) |
| name        | VARCHAR(100) | 是 | 用户名称 |
| remark      | TEXT | 否 | 用户备注 |
| secretKey   | VARCHAR(36) | 是 | 密钥(UUID) |
| callbackUrl | VARCHAR(500) | 是 | 回调地址 |
| createdAt   | DATETIME | 是 | 创建时间 |
| updatedAt   | DATETIME | 是 | 更新时间 |

### 6.2 触发器表 (triggers)

| 字段名         | 类型 | 必填 | 说明 |
|-------------|------|------|------|
| id          | INTEGER | 是 | 主键，自增，即triggerId |
| name        | VARCHAR(100) | 是 | 触发器名称 |
| description | TEXT | 否 | 触发器描述 |
| remark      | TEXT | 否 | 备注 |
| folderPath  | VARCHAR(500) | 是 | 文件夹完整路径 |
| createdAt   | DATETIME | 是 | 创建时间 |

### 6.3 用户触发器权限表 (user_trigger_permissions)

| 字段名       | 类型 | 必填 | 说明 |
|-----------|------|------|------|
| id        | INTEGER | 是 | 主键，自增 |
| userId    | VARCHAR(36) | 是 | 用户ID(关联users表) |
| triggerId | INTEGER | 是 | 触发器ID(关联triggers表) |
| createdAt | DATETIME | 是 | 创建时间 |

### 6.4 执行记录表 (execution_logs)

| 字段名                | 类型 | 必填 | 说明 |
|--------------------|------|------|------|
| id                 | INTEGER | 是 | 主键，自增 |
| eventId            | VARCHAR(50) | 是 | 执行事件ID |
| triggerId          | INTEGER | 是 | 触发器ID |
| userId             | VARCHAR(36) | 是 | 调用用户ID |
| status             | VARCHAR(50) | 是 | 执行状态 |
| requestTime        | DATETIME | 是 | 入参时间 |
| shadowbotStartTime | DATETIME | 否 | 影刀开始执行时间 |
| shadowbotEndTime   | DATETIME | 否 | 影刀执行完毕通知时间 |
| totalDuration      | INTEGER | 否 | 总耗时(ms) |
| shadowbotDuration  | INTEGER | 否 | 影刀执行耗时(ms) |
| requestParams      | TEXT | 是 | 入参参数(JSON字符串) |
| responseParams     | TEXT | 否 | 出参参数(JSON字符串) |
| createdAt          | DATETIME | 是 | 记录创建时间 |
| updatedAt          | DATETIME | 是 | 记录更新时间 |

---

## 七、UI设计

### 7.1 设计风格

参照影刀RPA的UI风格，采用简约现代的设计：

| 设计要素 | 规范 |
|----------|------|
| 主色调 | 浅粉色 `rgb(252, 104, 104)` / `#FC6868` |
| 背景色 | 白色 `rgb(255, 255, 255)` / `#FFFFFF` |
| 边框/分隔色 | 浅灰色 `rgb(245, 247, 250)` / `#F5F7FA` |
| 布局 | 左侧边栏 + 顶部导航 + 右侧主内容区 |
| 按钮风格 | 圆角矩形，主按钮使用主色调填充 |
| 表格 | 简洁的表格式数据展示 |
| 字体 | 系统默认字体，清晰易读 |

### 7.2 页面结构

```
┌─────────────────────────────────────────────────────────────────┐
│  Logo: FileWebHook                                              │
├──────────────┬──────────────────────────────────────────────────┤
│              │                                                  │
│   侧边导航    │              主内容区                            │
│              │                                                  │
│  ┌────────┐  │                                                  │
│  │ 触发器  │  │                                                  │
│  │ 管理   │  │                                                  │
│  └────────┘  │                                                  │
│              │                                                  │
│  ┌────────┐  │                                                  │
│  │ 用户   │  │                                                  │
│  │ 管理   │  │                                                  │
│  └────────┘  │                                                  │
│              │                                                  │
│  ┌────────┐  │                                                  │
│  │ 系统   │  │                                                  │
│  │ 设置   │  │                                                  │
│  └────────┘  │                                                  │
│              │                                                  │
└──────────────┴──────────────────────────────────────────────────┘
```

### 7.3 页面清单

#### 7.3.1 触发器管理页面
- 触发器列表
  - 显示：ID、名称、描述、创建时间、文件夹路径
  - 操作：查看详情、查看执行记录、复制路径
- 新建触发器弹窗
  - 表单：名称(必填)、描述(选填)、备注(选填)
- 触发器详情页
  - 基本信息展示
  - 执行记录列表（支持筛选、分页）

#### 7.3.2 用户管理页面
- 用户列表
  - 显示：名称、备注、回调地址、权限数量、创建时间
  - 操作：编辑、分配权限、重置密钥、删除
- 新建/编辑用户弹窗
  - 表单：名称(必填)、备注(选填)、回调地址(必填)
- 权限分配弹窗
  - 触发器多选列表

#### 7.3.3 系统设置页面
- triggerFiles路径配置
- 当前配置信息展示
- HTTP服务状态展示

---

## 九、安全设计

### 9.1 请求体安全校验

由于外部服务的请求体会直接写入文件，需要防范文件攻击：

| 校验项 | 规则 |
|--------|------|
| 请求体大小限制 | 最大 1MB |
| JSON格式校验 | 必须是合法的JSON对象 |
| 字符串内容校验 | 过滤路径遍历字符（如 `../`、`..\\`） |
| 特殊字符过滤 | 过滤可能的注入字符 |
| 嵌套深度限制 | JSON嵌套最大深度 10 层 |
| 字段数量限制 | 单个对象最大字段数 100 个 |

### 9.2 文件操作安全

| 安全措施 | 说明 |
|----------|------|
| 路径校验 | 确保文件只写入指定的 triggerFiles 目录 |
| 文件名安全 | 只使用系统生成的安全文件名 |
| 权限控制 | 文件操作使用最小必要权限 |

### 9.3 身份认证

| 认证方式 | 说明 |
|----------|------|
| 权限校验 | 验证用户是否有权限调用指定触发器 |

---

## 十、技术架构

### 10.1 技术选型

| 层级 | 技术 | 说明 |
|------|------|------|
| 编程语言 | Kotlin 2.0+ | 主要开发语言，利用其空安全、协程等特性 |
| 跨平台框架 | Kotlin Multiplatform | 支持代码跨平台复用，当前目标平台为 Windows Desktop |
| UI 框架 | Compose Multiplatform | JetBrains 出品的声明式 UI 框架，基于 Jetpack Compose |
| HTTP 服务端 | Ktor Server | Kotlin 原生异步 HTTP 框架，用于接收外部服务请求和影刀回调 |
| HTTP 客户端 | Ktor Client | 用于回调外部服务通知执行结果 |
| 数据库 | SQLite + SQLDelight | 轻量级本地数据库，SQLDelight 提供类型安全的 SQL 操作 |
| 序列化 | Kotlinx Serialization | Kotlin 官方序列化库，用于 JSON 处理 |
| 异步处理 | Kotlinx Coroutines | Kotlin 协程，用于任务队列、异步 IO 等 |
| 依赖注入 | Koin | 轻量级 Kotlin 依赖注入框架 |
| 构建工具 | Gradle (Kotlin DSL) | 使用 Kotlin DSL 编写构建脚本 |
| 打包工具 | Compose Multiplatform Packaging | 生成 Windows .msi/.exe 安装包 |

### 10.2 项目结构

```
ShadowBot-FileWebHook/
├── build.gradle.kts                 # 根构建脚本
├── settings.gradle.kts              # 项目设置
├── gradle.properties                # Gradle 属性配置
│
├── composeApp/                      # 主应用模块
│   ├── build.gradle.kts
│   └── src/
│       ├── commonMain/              # 跨平台共享代码
│       │   └── kotlin/
│       │       ├── App.kt           # 应用入口
│       │       ├── di/              # 依赖注入模块
│       │       ├── data/            # 数据层
│       │       │   ├── database/    # SQLDelight 数据库
│       │       │   ├── repository/  # 数据仓库
│       │       │   └── model/       # 数据模型
│       │       ├── domain/          # 业务逻辑层
│       │       │   ├── usecase/     # 用例
│       │       │   └── queue/       # 任务队列
│       │       ├── server/          # Ktor HTTP 服务端
│       │       │   ├── routes/      # 路由定义
│       │       │   └── plugins/     # Ktor 插件配置
│       │       └── ui/              # Compose UI 层
│       │           ├── theme/       # 主题样式
│       │           ├── components/  # 可复用组件
│       │           ├── screens/     # 页面
│       │           │   ├── trigger/ # 触发器管理
│       │           │   ├── user/    # 用户管理
│       │           │   └── settings/# 系统设置
│       │           └── navigation/  # 导航
│       │
│       └── desktopMain/             # Desktop 平台特定代码
│           └── kotlin/
│               ├── main.kt          # Desktop 入口
│               └── platform/        # 平台相关实现（文件操作等）
│
└── gradle/
    └── libs.versions.toml           # 版本目录（依赖版本管理）
```

### 10.3 核心依赖版本

| 依赖 | 版本 | 用途 |
|------|------|------|
| Kotlin | 2.0.21+ | 编程语言 |
| Compose Multiplatform | 1.7.0+ | UI 框架 |
| Ktor | 3.0.0+ | HTTP 服务端/客户端 |
| SQLDelight | 2.0.2+ | 数据库 ORM |
| Kotlinx Serialization | 1.7.3+ | JSON 序列化 |
| Kotlinx Coroutines | 1.9.0+ | 协程 |
| Koin | 4.0.0+ | 依赖注入 |

### 10.4 架构模式

采用 **Clean Architecture** 分层架构：

```
┌─────────────────────────────────────────────────────────────┐
│                      UI Layer (Compose)                      │
│         Screens / Components / ViewModels / State           │
├─────────────────────────────────────────────────────────────┤
│                     Domain Layer                             │
│              UseCases / Business Logic / Queue               │
├─────────────────────────────────────────────────────────────┤
│                      Data Layer                              │
│         Repositories / Database / HTTP Client                │
├─────────────────────────────────────────────────────────────┤
│                   Infrastructure Layer                       │
│    Ktor Server / SQLDelight / File System / Platform APIs   │
└─────────────────────────────────────────────────────────────┘
```

**状态管理**：使用 Compose 的 `MutableState` 配合 `ViewModel` 模式管理 UI 状态。

### 10.5 关键技术实现说明

#### 10.5.1 HTTP 服务端 (Ktor Server)

```kotlin
// 嵌入式 HTTP 服务器配置示例
embeddedServer(CIO, port = 8089, host = "0.0.0.0") {
    install(ContentNegotiation) { json() }
    install(StatusPages) { /* 异常处理 */ }
    routing {
        post("/trigger/execute") { /* 触发执行 */ }
        post("/trigged") { /* 影刀开始执行回调 */ }
        post("/notify") { /* 影刀执行完成回调 */ }
    }
}.start(wait = false)
```

#### 10.5.2 任务队列 (Coroutines Channel)

```kotlin
// 使用 Channel 实现 FIFO 队列
private val taskChannel = Channel<TriggerTask>(capacity = maxQueueLength)

// 生产者：入队
suspend fun enqueue(task: TriggerTask): Boolean {
    return taskChannel.trySend(task).isSuccess
}

// 消费者：串行执行
launch {
    for (task in taskChannel) {
        processTask(task)  // 写入文件，等待影刀回调
    }
}
```

#### 10.5.3 数据库 (SQLDelight)

```sql
-- 定义类型安全的 SQL 查询
CREATE TABLE users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    userId TEXT NOT NULL UNIQUE,
    name TEXT NOT NULL,
    secretKey TEXT NOT NULL,
    callbackUrl TEXT NOT NULL,
    createdAt TEXT NOT NULL,
    updatedAt TEXT NOT NULL
);

selectByUserId:
SELECT * FROM users WHERE userId = ?;
```

#### 10.5.4 Compose UI 主题

```kotlin
// 影刀风格主题定义
val ShadowBotColors = lightColorScheme(
    primary = Color(0xFFFC6868),      // 主色调：浅粉色
    background = Color(0xFFFFFFFF),   // 背景色：白色
    surface = Color(0xFFF5F7FA),      // 表面色：浅灰色
)
```

## 十一、部署与发布

### 11.1 构建产物

| 产物 | 说明                           |
|------|------------------------------|
| Windows安装包 | .exe 安装程序,安装后在桌面有1个图标用户可点击打开 |

### 11.2 系统要求

| 要求项 | 规格 |
|--------|------|
| 操作系统 | Windows 10/11 |
| 内存 | 最低 4GB |
| 磁盘空间 | 最低 200MB |
| 依赖 | 无需额外安装运行时 |

### 11.3 开源发布

- 代码托管：GitHub
- 开源协议：MIT License
- 仓库名称：ShadowBot-FileWebHook

---

## 十二、网络配置

### 12.1 网络访问说明

FileWebHook 作为 HTTP Server 运行在本机，外部服务需要能够通过网络访问到 FileWebHook 才能触发调用。

### 12.2 监听地址配置

| 配置值 | 说明 | 适用场景 |
|--------|------|----------|
| `0.0.0.0` | 允许所有网络接口访问 | 外部服务在局域网或公网（默认值） |

### 12.3 网络访问方案

根据外部服务的部署位置，有以下几种网络访问方案：

#### 方案1：公网访问（内网穿透）
- **场景**：外部服务在公网，FileWebHook 在内网机器上
- **配置**：
  1. FileWebHook 监听地址设为 `0.0.0.0`
  2. 使用内网穿透工具将本地 8089 端口映射到公网
- **常用内网穿透工具**：
  - ngrok
  - frp (Fast Reverse Proxy)
  - 花生壳
  - cpolar
  - Cloudflare Tunnel
- **访问地址**：内网穿透工具提供的公网域名

### 12.4 注意事项

| 注意项 | 说明 |
|--------|------|
| 网络安全 | 公网暴露时请确保密钥足够复杂，建议定期更换 |
| 回调地址 | 用户的外部服务地址必须是 FileWebHook 能够访问到的地址 |
| 网络稳定性 | 内网穿透方案依赖第三方服务，需注意稳定性和带宽限制 |
| 用户责任 | 网络可达性由用户自行负责配置和维护 |

---

## 十三、文件说明

### 13.1 request.json 文件格式

```json
{
  "eventId": "1-1706515200000",
  "triggerId": "1",
  "userId": "uuid-xxx",
  "requestData": {
    "外部服务传入的业务参数": "值"
  }
}
```

### 13.2 文件生命周期

1. **创建**：队列消费任务时，写入 `triggerFiles\{triggerId}\requestParam.json`
2. **读取**：影刀文件触发器监听到文件创建，读取参数
3. **删除**：FileWebHook 收到 `/notify` 回调后，删除该文件

---

## 十四、附录

### 14.1 执行状态中英文对照

| 中文状态 | 英文标识 |
|----------|----------|
| 用户已请求 | REQUESTED |
| 已收到请求放入队列 | QUEUED |
| 已预响应外部服务 | PRE_RESPONDED |
| 已写入文件待影刀执行 | FILE_WRITTEN |
| 影刀执行中 | EXECUTING |
| 影刀执行完成 | COMPLETED |
| 回调用户完成 | CALLBACK_SUCCESS |
| 回调外部服务失败 | CALLBACK_FAILED |

### 14.2 eventId 生成规则

格式：`{triggerId}-{时间戳毫秒}`
示例：`1-1706515200000`

### 14.3 用户使用前提

1. 本机已安装并启动影刀RPA
2. 在影刀中导入 FileWebHook-APP-Framework
3. 启动 FileWebHook 软件, 创建触发器并配置权限
4. 在影刀中配置文件触发器，监听 FileWebHook 生成的文件夹路径

---

## 十五、版本历史

| 版本 | 日期 | 说明 |
|------|------|------|
| v1.0 | 2026-01-29 | 初始版本 |

---