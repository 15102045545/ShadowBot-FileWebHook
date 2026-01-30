# 更新日志

本文件记录项目的所有重要变更。

格式基于 [Keep a Changelog](https://keepachangelog.com/zh-CN/1.0.0/)，
版本号遵循 [语义化版本](https://semver.org/lang/zh-CN/)。

## [Unreleased]

### 计划中
- 执行记录页面
- 执行记录导出功能
- 系统日志查看
- 暗色主题支持

---

## [1.0.0] - 2024-XX-XX

### 新增
- 🎉 首次发布

#### 核心功能
- HTTP 触发执行接口 (`POST /trigger/execute`)
- 影刀开始执行回调接口 (`POST /trigged`)
- 影刀执行完成回调接口 (`POST /notify`)
- 外部服务执行结果回调

#### 触发器管理
- 创建触发器
- 查看触发器列表
- 自动创建触发器文件夹
- 复制文件夹路径到剪贴板

#### 用户管理
- 创建用户（自动生成 userId 和 secretKey）
- 编辑用户信息
- 删除用户
- 重置用户密钥
- 分配触发器权限

#### 系统设置
- 配置触发器文件路径
- 配置队列最大长度
- 配置 HTTP 端口
- 配置 FileWebHook 名称和密钥
- 启动/停止 HTTP 服务

#### 安全特性
- 用户身份验证（userId + secretKey）
- 触发器权限控制
- 请求参数安全校验（大小、深度、危险字符）

#### 技术特性
- Kotlin Multiplatform 跨平台支持
- Compose Multiplatform 原生 UI
- SQLite 本地数据库
- FIFO 任务队列
- 协程异步处理

---

## 版本说明

### 版本号规则

- **主版本号 (MAJOR)**: 不兼容的 API 变更
- **次版本号 (MINOR)**: 向后兼容的功能新增
- **修订号 (PATCH)**: 向后兼容的 Bug 修复

### 变更类型

- `新增` - 新功能
- `变更` - 现有功能的变更
- `弃用` - 即将移除的功能
- `移除` - 已移除的功能
- `修复` - Bug 修复
- `安全` - 安全相关修复

---

[Unreleased]: https://github.com/your-username/ShadowBot-FileWebHook/compare/v1.0.0...HEAD
[1.0.0]: https://github.com/your-username/ShadowBot-FileWebHook/releases/tag/v1.0.0
