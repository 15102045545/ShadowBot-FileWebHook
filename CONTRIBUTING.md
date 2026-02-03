# 贡献指南

感谢您对 FileWebHook 项目的关注！我们欢迎各种形式的贡献。

## 如何贡献

### 报告 Bug

如果您发现了 Bug，请通过 [GitHub Issues](https://github.com/15102045545/ShadowBot-FileWebHook/issues) 提交，并包含以下信息：

- **问题描述**：清晰描述遇到的问题
- **复现步骤**：详细的步骤说明
- **期望行为**：您期望看到的结果
- **实际行为**：实际发生的情况
- **环境信息**：
  - 操作系统及版本
  - Java 版本
  - 应用版本
- **日志/截图**：如有相关错误日志或截图

### 功能建议

欢迎提出新功能建议！请在 Issue 中说明：

- 功能描述
- 使用场景
- 期望的实现方式（可选）

### 提交代码

#### 1. Fork 仓库

点击 GitHub 页面右上角的 Fork 按钮。

#### 2. 克隆到本地

```bash
git clone https://github.com/15102045545/ShadowBot-FileWebHook.git
cd ShadowBot-FileWebHook
```

#### 3. 创建分支

```bash
git checkout -b feature/your-feature-name
# 或
git checkout -b fix/your-bug-fix
```

分支命名规范：
- `feature/xxx` - 新功能
- `fix/xxx` - Bug 修复
- `docs/xxx` - 文档更新
- `refactor/xxx` - 代码重构

#### 4. 开发

确保您的代码：
- 遵循现有的代码风格
- 包含必要的注释（中文）
- 通过编译测试

```bash
# 编译项目
./gradlew :composeApp:build

# 运行应用测试
./gradlew :composeApp:run
```

#### 5. 提交

```bash
git add .
git commit -m "feat: 添加新功能描述"
```

Commit 消息规范：
- `feat:` 新功能
- `fix:` Bug 修复
- `docs:` 文档更新
- `style:` 代码格式（不影响功能）
- `refactor:` 代码重构
- `test:` 测试相关
- `chore:` 构建/工具相关

#### 6. 推送

```bash
git push origin feature/your-feature-name
```

#### 7. 创建 Pull Request

在 GitHub 上创建 Pull Request，并：
- 填写清晰的标题和描述
- 关联相关的 Issue（如有）
- 等待代码审查

## 开发环境设置

### 必需工具

- JDK 17+
- IntelliJ IDEA（推荐）或其他 IDE
- Gradle 8.0+（项目自带 Wrapper）

### 推荐的 IDEA 插件

- Kotlin
- Compose Multiplatform IDE Support
- SQLDelight

### 项目结构说明

```

```

## 发布流程

1. 更新 `CHANGELOG.md`
2. 更新版本号（`build.gradle.kts`）
3. 创建 Release Tag
4. 构建各平台安装包
5. 发布 GitHub Release

## 行为准则

请阅读并遵守我们的 [行为准则](CODE_OF_CONDUCT.md)。

## 获取帮助

如有任何问题，可以：
- 查看 [README](README.md) 和文档
- 搜索现有 [Issues](https://github.com/15102045545/ShadowBot-FileWebHook/issues)
- 创建新的 Issue 提问

## 致谢

感谢所有贡献者的付出！

---

再次感谢您的贡献！🎉
