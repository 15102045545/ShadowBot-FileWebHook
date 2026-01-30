# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Tech Stack

| Category | Technology | Version |
|----------|------------|---------|
| Language | Kotlin | 2.0.21 |
| UI Framework | Compose Multiplatform | 1.7.1 |
| HTTP Framework | Ktor | 3.0.2 |
| Database | SQLDelight + SQLite | 2.0.2 |
| DI | Koin | 4.0.0 |
| Serialization | Kotlinx Serialization | 1.7.3 |
| Coroutines | Kotlinx Coroutines | 1.9.0 |
| Build | Gradle with Kotlin DSL | 8.10 |

## Common Commands

```bash
# Run the desktop application
./gradlew :composeApp:run
# On Windows: gradlew.bat :composeApp:run

# Build the project
./gradlew :composeApp:build

# Build distribution packages
./gradlew :composeApp:packageMsi      # Windows
./gradlew :composeApp:packageDmg      # macOS
./gradlew :composeApp:packageDeb      # Linux

# Clean build
./gradlew clean build

# Generate SQLDelight code (auto-generated during build)
# Output: composeApp/build/generated/sqldelight/code/
```

## Project Overview

FileWebHook 是一个桌面中间件应用，专为影刀 RPA 设计。它将 HTTP 请求转换为文件触发器，解决影刀缺少原生 HTTP 触发器的问题。

## Architecture

```
External Service → HTTP POST → FileWebHook → Write request.json → ShadowBot File Trigger
                                    ↑                                      ↓
                        Callback notification ←─────────────────── Execute RPA App
```

**Core Flow:**
1. External service POSTs to `/trigger/execute` with userId, secretKey, triggerId, requestParam
2. FileWebHook validates credentials and permissions
3. Request enters global FIFO queue (serial execution - one task at a time)
4. Queue consumer writes `triggerFiles/{triggerId}/request.json`
5. ShadowBot file trigger detects file, calls `/triggered` callback
6. ShadowBot executes business logic, calls `/notify` with results
7. FileWebHook deletes request file, POSTs callback to external service

## Project Structure

```
composeApp/src/
├── commonMain/kotlin/
│   ├── data/              # Data Layer
│   │   ├── model/         # Data models (User, Trigger, ExecutionLog, Settings)
│   │   └── repository/    # Database repositories
│   ├── domain/            # Domain Layer
│   │   ├── queue/         # FIFO task queue
│   │   └── service/       # Business services (FileService, RequestValidator)
│   ├── server/            # HTTP Server Layer
│   │   ├── routes/        # API routes (TriggerRoutes, CallbackRoutes)
│   │   └── plugins/       # Ktor plugins
│   ├── client/            # HTTP Client (CallbackClient)
│   ├── di/                # Koin DI modules
│   └── ui/                # UI Layer
│       ├── theme/         # Theme configuration
│       ├── navigation/    # Navigation definitions
│       ├── components/    # Reusable components
│       └── screens/       # Screen composables and ViewModels
├── commonMain/sqldelight/ # SQLDelight schema
└── desktopMain/kotlin/    # Desktop platform implementation
```

## API Endpoints

**For External Services:**
- `POST /trigger/execute` - Trigger an RPA application

**For ShadowBot Callbacks:**
- `POST /triggered` - Notification that execution started
- `POST /notify` - Notification with execution results

## Data Model

Five SQLite tables:
- `users` - External service credentials (userId, secretKey, callbackUrl)
- `triggers` - RPA trigger definitions (auto-increment id, folderPath)
- `user_trigger_permissions` - Many-to-many user/trigger access control
- `execution_logs` - Event tracking with status flow
- `settings` - Key-value system settings

**Execution Status Flow:**
```
REQUESTED → QUEUED → PRE_RESPONDED → FILE_WRITTEN → EXECUTING → COMPLETED → CALLBACK_SUCCESS/CALLBACK_FAILED
```

## Key Configuration

| Setting | Description | Default |
|---------|-------------|---------|
| triggerFilesPath | Directory for trigger files | `{user.home}/.filewebhook/triggerFiles` |
| globalQueueMaxLength | Max queue size | 50 |
| httpPort | HTTP server port | 8089 |
| fileWebHookName | Instance identifier for callbacks | - |
| fileWebHookSecretKey | Authentication key for external service callbacks | - |

Settings are stored in SQLite database (`settings` table) and managed through UI.

## Security Requirements

- Request body max 1MB
- JSON nesting max 10 levels deep
- Max 100 fields per object
- Filter path traversal characters (`../`, `..\\`)
- Filter XSS patterns (`<script`, `javascript:`, etc.)
- Files only written to triggerFiles directory
- eventId format: `{triggerId}-{timestamp_ms}`

## UI Design

- Primary color: `#FC6868` (ShadowBot pink)
- Three main pages: Trigger Management, User Management, System Settings
- Left sidebar navigation
- Material 3 Design

## Code Style

- 所有注释使用中文
- 文件顶部包含模块说明
- 类和函数使用 KDoc 文档注释
- 遵循 Kotlin 官方代码风格

## Build Target

**Currently configured for Windows only:**
- Windows: `.msi` / `.exe`
- macOS and Linux: build targets exist in gradle but are not configured

## Runtime Behavior

- HTTP server and task queue **must be manually started** via Settings screen (not auto-started)
- SQLDelight generates code in `composeApp/build/generated/sqldelight/code/`
- ShadowBot callback timeout: 30 minutes
- Task queue is FIFO (serial execution - only one task runs at a time)
- Queue uses Kotlin Channel with bounded capacity
- Each task lifecycle: REQUESTED → QUEUED → PRE_RESPONDED → FILE_WRITTEN → EXECUTING → COMPLETED → CALLBACK_SUCCESS/CALLBACK_FAILED

## Integration Details

**Event ID Format:** `{triggerId}-{timestamp_ms}` - critical for callback matching throughout the system

**External Service Callback URL Pattern:**
```
{callbackUrl}/{fileWebHookName}/filewebhook/notify
```

**Dependency Injection:** Uses Koin with three modules:
- `dataModule` - Database driver, repositories
- `domainModule` - CallbackClient, FileService, TaskQueue
- `serverModule` - FileWebHookServer

**Core Components:**
- `TaskQueue` (domain/queue/TaskQueue.kt) - Serial FIFO queue using Kotlin Channel, manages task lifecycle
- `FileWebHookServer` (server/FileWebHookServer.kt) - Ktor CIO embedded server with routes for external services and ShadowBot callbacks
- `FileService` (domain/service/FileService.kt) - Manages writing/deleting request.json files in trigger folders
- `CallbackClient` (client/CallbackClient.kt) - Ktor HTTP client for posting results to external services
