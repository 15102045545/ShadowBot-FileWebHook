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

# Clean build
./gradlew clean build

# Generate SQLDelight code (auto-generated during build)
# Output: composeApp/build/generated/sqldelight/code/
```

## Project Overview

FileWebHook is a desktop middleware application designed for ShadowBot RPA. It converts HTTP requests into file triggers, solving the problem that ShadowBot lacks native HTTP trigger support.

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

---

## Project Structure

```
composeApp/src/
├── commonMain/kotlin/
│   ├── client/              # HTTP Client Layer
│   │   └── CallbackClient.kt
│   ├── data/                # Data Layer
│   │   ├── model/           # Data models
│   │   │   ├── ApiModels.kt
│   │   │   ├── ExecutionLog.kt
│   │   │   ├── Settings.kt
│   │   │   ├── Trigger.kt
│   │   │   ├── User.kt
│   │   │   └── UserPermission.kt
│   │   ├── repository/      # Database repositories
│   │   │   ├── ExecutionLogRepository.kt
│   │   │   ├── PermissionRepository.kt
│   │   │   ├── SettingsRepository.kt
│   │   │   ├── TriggerRepository.kt
│   │   │   └── UserRepository.kt
│   │   └── DatabaseDriverFactory.kt
│   ├── di/                  # Dependency Injection
│   │   └── AppModule.kt
│   ├── domain/              # Domain Layer (Business Logic)
│   │   ├── clipboard/       # Clipboard operations
│   │   │   └── ClipboardManager.kt
│   │   ├── python/          # Python script execution
│   │   │   ├── PythonExecutor.kt
│   │   │   ├── ScriptPathProviderFactory.kt
│   │   │   └── ShadowBotPythonFinder.kt
│   │   ├── queue/           # Task queue
│   │   │   └── TaskQueue.kt
│   │   └── service/         # Business services
│   │       ├── FileService.kt
│   │       └── RequestValidator.kt
│   ├── server/              # HTTP Server Layer
│   │   ├── plugins/         # Ktor plugins
│   │   │   └── Plugins.kt
│   │   ├── routes/          # API routes
│   │   │   ├── CallbackRoutes.kt
│   │   │   └── TriggerRoutes.kt
│   │   └── FileWebHookServer.kt
│   ├── shadowbot/           # ShadowBot integration resources
│   │   ├── BaseFileWebHookAppFramework.pkl
│   │   ├── dump_clipboard_to_file.py
│   │   ├── load_instructions_to_clipboard.py
│   │   └── modify_shadowbot_instructions.py
│   └── ui/                  # UI Layer
│       ├── components/      # Reusable UI components
│       │   ├── CommonDialogs.kt
│       │   ├── DataTable.kt
│       │   └── Sidebar.kt
│       ├── navigation/      # Navigation definitions
│       │   └── Navigation.kt
│       ├── screens/         # Screen implementations
│       │   ├── developer/
│       │   ├── executionlog/
│       │   ├── settings/
│       │   ├── trigger/
│       │   ├── user/
│       │   └── userpermission/
│       ├── theme/           # Theme configuration
│       │   └── Theme.kt
│       └── App.kt
├── commonMain/sqldelight/   # SQLDelight schema
│   └── com/filewebhook/db/
│       └── FileWebHook.sq
└── desktopMain/kotlin/      # Desktop platform implementation
    ├── data/
    │   └── DatabaseDriverFactory.kt
    ├── domain/
    │   ├── clipboard/
    │   │   └── ClipboardManager.kt
    │   └── python/
    │       ├── DesktopScriptPathProvider.kt
    │       ├── ScriptPathProviderFactory.kt
    │       └── ScriptResourceExtractor.kt
    └── main.kt
```

---

## Detailed Class Documentation

### Data Layer (`data/`)

#### `data/DatabaseDriverFactory.kt`
- **Type:** Expect class (multiplatform)
- **Purpose:** Abstract factory for creating SQLite database drivers
- **Key Method:**
  - `createDriver(): SqlDriver` - Creates platform-specific database driver

#### `data/model/ApiModels.kt`
API request/response models for HTTP communication:

| Class | Purpose |
|-------|---------|
| `ExecuteRequest` | External service request body (userId, secretKey, triggerId, requestParam) |
| `TriggerResponse` | Response returned to external service |
| `TriggerCallback` | Internal callback data structure |
| `NotifyRequest` | ShadowBot notification request body |

#### `data/model/User.kt`
- **Class:** `User`
- **Purpose:** External service user information
- **Fields:** userId (unique), name, secretKey, callbackUrl, callbackHeaders, remark

#### `data/model/Trigger.kt`
- **Class:** `Trigger`
- **Purpose:** RPA trigger definition
- **Fields:** id (auto-increment), name, description, folderPath, remark

#### `data/model/ExecutionLog.kt`
- **Classes:** `ExecutionLog`, `ExecutionStatus` (enum)
- **Purpose:** Execution event tracking with status flow
- **ExecutionStatus Values:**
  ```
  REQUESTED → QUEUED → FILE_WRITTEN → EXECUTING → COMPLETED → CALLBACK_SUCCESS/CALLBACK_FAILED
  ```

#### `data/model/Settings.kt`
- **Classes:** `AppSettings`, `SettingsKey` (enum)
- **Purpose:** System configuration
- **Key Settings:** httpPort, globalQueueMaxLength, triggerFilesPath, pythonInterpreterPath

#### `data/model/UserPermission.kt`
- **Class:** `UserPermission`
- **Purpose:** User-trigger permission association for access control

---

### Repository Layer (`data/repository/`)

#### `UserRepository.kt`
| Method | Purpose |
|--------|---------|
| `getAllUsers()` | Get all users |
| `getUserById(userId)` | Get user by ID |
| `validateUserCredentials(userId, secretKey)` | Validate user credentials |
| `insertUser(user)` | Create new user |
| `updateUser(user)` | Update user |
| `deleteUser(userId)` | Delete user |

#### `TriggerRepository.kt`
| Method | Purpose |
|--------|---------|
| `getAllTriggers()` | Get all triggers |
| `getTriggerById(id)` | Get trigger by ID |
| `insertTrigger(trigger)` | Create new trigger |
| `updateTrigger(trigger)` | Update trigger |
| `deleteTrigger(id)` | Delete trigger |

#### `PermissionRepository.kt`
| Method | Purpose |
|--------|---------|
| `getPermissionsByUserId(userId)` | Get user's permissions |
| `hasPermission(userId, triggerId)` | Check if user has permission for trigger |
| `grantPermissions(userId, triggerIds)` | Grant permissions to user |
| `revokePermission(userId, triggerId)` | Revoke permission |
| `getAllPermissions()` | Get all permission records |

#### `ExecutionLogRepository.kt`
| Method | Purpose |
|--------|---------|
| `createExecutionLog(log)` | Create new execution log entry |
| `updateStatus(eventId, status)` | Update log status |
| `updateShadowbotStartTime(eventId)` | Record ShadowBot start time |
| `updateExecutionComplete(eventId, responseCode, responseMessage, responseParams)` | Record execution completion |
| `getLogsByFilters(filters, page, pageSize)` | Get logs with filtering and pagination |
| `countLogsByFilters(filters)` | Count logs matching filters |

#### `SettingsRepository.kt`
| Method | Purpose |
|--------|---------|
| `getSettings()` | Load all settings as AppSettings object |
| `saveSettings(settings)` | Save AppSettings to database |
| `initializeDefaultSettings()` | Initialize settings with default values |

---

### Domain Layer (`domain/`)

#### `domain/queue/TaskQueue.kt`
- **Purpose:** Serial FIFO task queue using Kotlin Channel with bounded capacity
- **Key Properties:**
  - `queueSize: StateFlow<Int>` - Current queue size observable
  - `pendingTasksFlow: StateFlow<List<TriggerTask>>` - Pending tasks observable
  - `currentTask: StateFlow<TriggerTask?>` - Currently executing task
- **Key Methods:**

| Method | Purpose |
|--------|---------|
| `start()` | Start queue processing coroutine |
| `stop()` | Stop queue processing gracefully |
| `clearAndStop()` | Clear all pending tasks and stop |
| `enqueue(task): EnqueueResult` | Add task to queue (returns success/queue_full) |
| `setExecuting(eventId)` | Mark task as currently executing |
| `setCompleted(eventId)` | Mark task as completed and remove from queue |

#### `domain/service/FileService.kt`
- **Purpose:** Manages writing/deleting request.json files in trigger folders
- **Key Methods:**

| Method | Purpose |
|--------|---------|
| `writeRequestFile(task): FileWriteResult` | Write task data as JSON to trigger folder |
| `deleteRequestFile(triggerId, eventId)` | Delete request file after completion |
| `ensureTriggerFolder(triggerId): String` | Create trigger folder if not exists |
| `getTriggerFilesPath(): String` | Get configured trigger files base path |

#### `domain/service/RequestValidator.kt`
- **Purpose:** Security validation for incoming HTTP requests
- **Key Methods:**

| Method | Purpose |
|--------|---------|
| `validateRequest(request): ValidationResult` | Validate request body (size, nesting, fields, security) |
| `validateJsonElement(element)` | Recursive JSON validation |
| `containsMaliciousPatterns(value): Boolean` | Check for XSS/injection patterns |

**Validation Rules:**
- Max request body: 1MB
- Max JSON nesting: 10 levels
- Max fields per object: 100
- Blocked patterns: `../`, `..\\`, `<script`, `javascript:`, `onclick`, `onerror`

#### `domain/clipboard/ClipboardManager.kt`
- **Type:** Expect class (multiplatform)
- **Purpose:** Abstract clipboard operations for ShadowBot instructions
- **Key Methods:**

| Method | Purpose |
|--------|---------|
| `dumpClipboardToFile(savePath): ClipboardResult` | Save clipboard content to file |
| `restoreClipboardFromFile(sourcePath, replacements): ClipboardResult` | Restore clipboard with variable replacement |
| `copyWithReplacementsViaFile(sourcePath, targetPath, replacements): ClipboardResult` | Copy instructions with dynamic replacements |

#### `domain/python/PythonExecutor.kt`
- **Purpose:** Execute Python scripts for clipboard operations
- **Key Methods:**

| Method | Purpose |
|--------|---------|
| `dumpClipboardToFile(savePath): PythonResult` | Execute Python script to dump clipboard |
| `loadInstructionsToClipboard(sourcePath, replacements): PythonResult` | Load instructions to clipboard via Python |
| `isDevEnvironment(): Boolean` | Check if running in development environment |
| `getDevResourcesSavePath(): String?` | Get development resources save path |

#### `domain/python/ShadowBotPythonFinder.kt`
- **Purpose:** Auto-detect ShadowBot's built-in Python interpreter
- **Key Method:**
  - `findShadowBotPython(): PythonFinderResult` - Scan system for ShadowBot Python installation

#### `domain/python/ScriptPathProviderFactory.kt`
- **Purpose:** Factory for creating platform-specific script path providers
- **Interface:** `ScriptPathProvider`
- **Key Methods:**
  - `getScriptPath(scriptName): String` - Get path to Python script
  - `getBaseFrameworkPath(): String` - Get path to base framework instructions file

---

### Server Layer (`server/`)

#### `server/FileWebHookServer.kt`
- **Purpose:** Ktor CIO embedded HTTP server
- **Key Properties:**
  - `isRunning: Boolean` - Server running state
- **Key Methods:**

| Method | Purpose |
|--------|---------|
| `start(port: Int)` | Start HTTP server on specified port |
| `stop()` | Stop HTTP server gracefully |

#### `server/routes/TriggerRoutes.kt`
- **Function:** `Application.configureTriggerRoutes()`
- **Endpoint:** `POST /trigger/execute`
- **Purpose:** Handle external service requests to trigger RPA applications
- **Flow:** Validate credentials → Check permissions → Validate request → Create log → Enqueue task

#### `server/routes/CallbackRoutes.kt`
- **Function:** `Application.configureCallbackRoutes()`
- **Endpoints:**
  - `POST /triggered` - ShadowBot notifies execution started
  - `POST /notify` - ShadowBot sends execution results
- **Purpose:** Handle callbacks from ShadowBot during RPA execution

#### `server/plugins/Plugins.kt`
- **Function:** `Application.configurePlugins()`
- **Purpose:** Configure Ktor server plugins
- **Configures:** ContentNegotiation (JSON), StatusPages (error handling), CallLogging

---

### Client Layer (`client/`)

#### `client/CallbackClient.kt`
- **Purpose:** HTTP client for posting results to external services
- **Key Methods:**

| Method | Purpose |
|--------|---------|
| `sendCallback(user, eventId, triggerId, responseCode, responseMessage, responseParams): CallbackResult` | Send callback to external service |
| `buildCallbackUrl(user): String` | Build callback URL pattern |

**Callback URL Pattern:** `{user.callbackUrl}/{fileWebHookName}/filewebhook/notify`

---

### Dependency Injection (`di/`)

#### `di/AppModule.kt`
Koin DI module definitions:

| Module | Components |
|--------|------------|
| `dataModule` | DatabaseDriverFactory, Database, UserRepository, TriggerRepository, PermissionRepository, ExecutionLogRepository |
| `domainModule` | CallbackClient, FileService, TaskQueue, ClipboardManager, PythonExecutor, SettingsRepository, ScriptPathProvider |
| `serverModule` | FileWebHookServer |

**Usage:** `appModules` combines all modules for application startup

---

### UI Layer (`ui/`)

#### `ui/App.kt`
- **Function:** `App()`
- **Purpose:** Root composable with theme and navigation setup

#### `ui/theme/Theme.kt`
- **Function:** `FileWebHookTheme()`
- **Key Colors:**
  - `ShadowBotPink` (#FC6868) - Primary brand color
  - `ShadowBotSuccess` - Green for success states
  - `ShadowBotError` - Red for error states

#### `ui/navigation/Navigation.kt`
- **Sealed Class:** `Screen`
- **Routes:**
  - `Screen.Triggers` - Trigger management
  - `Screen.Users` - User management
  - `Screen.UserPermissions` - Permission management
  - `Screen.ExecutionLogs` - Execution log viewer
  - `Screen.Settings` - System settings
  - `Screen.Developer` - Developer tools

#### UI Components (`ui/components/`)

| File | Components | Purpose |
|------|------------|---------|
| `CommonDialogs.kt` | `ConfirmDialog`, `FormTextField`, `FormFieldLabel` | Common dialog and form components |
| `DataTable.kt` | `DataTable`, `TableColumn`, `TableCellText` | Generic data table with customizable columns |
| `Sidebar.kt` | `SidebarLayout`, `SidebarNavItem` | Left sidebar navigation with icons |

#### Screen ViewModels

##### `TriggerViewModel.kt`
| Method | Purpose |
|--------|---------|
| `loadTriggers()` | Load all triggers |
| `addTrigger(name, description, remark)` | Create new trigger |
| `updateTrigger(trigger)` | Update trigger |
| `deleteTrigger(triggerId)` | Delete trigger |
| `copyFrameworkInstruction(trigger)` | Copy ShadowBot framework instructions to clipboard |

##### `UserViewModel.kt`
| Method | Purpose |
|--------|---------|
| `loadUsers()`, `loadTriggers()` | Load data |
| `addUser(name, remark, callbackUrl, callbackHeaders)` | Create user (auto-generates userId, secretKey) |
| `updateUser(user)` | Update user |
| `deleteUser(userId)` | Delete user |
| `regenerateSecretKey(userId)` | Generate new secret key |
| `updateUserPermissions(userId, triggerIds)` | Update user's trigger permissions |

##### `UserPermissionViewModel.kt`
| Method | Purpose |
|--------|---------|
| `loadPermissions()` | Load permissions (with optional userId filter) |
| `setSearchUserId(userId)` | Set search filter |
| `generateJavaClientExample(permission)` | Generate Java code snippet for API integration |

##### `ExecutionLogViewModel.kt`
| Method | Purpose |
|--------|---------|
| `initialize()` | Load triggers and users for filter dropdowns |
| `loadExecutionLogs()` | Load logs with current filters |
| `applyFilters(triggerId, userId, status, eventId, startTime, endTime)` | Apply filter conditions |
| `clearFilters()` | Reset all filters |
| `goToPage(page)`, `previousPage()`, `nextPage()` | Pagination control |

##### `SettingsViewModel.kt`
| Method | Purpose |
|--------|---------|
| `loadSettings()` | Load settings from database |
| `saveSettings(settings)` | Save settings |
| `startServer()` | Start HTTP server and task queue |
| `stopServer()` | Stop HTTP server and task queue |
| `restartServer()` | Restart with new configuration |
| `autoStartServerOnLaunch()` | Auto-start on app launch |
| `detectPythonInterpreter()` | Auto-detect ShadowBot Python |
| `startQueueDataCollection()` / `stopQueueDataCollection()` | Monitor queue state |
| `getRuntimeDataPath()` | Get runtime data directory path |
| `openRuntimeDataDirectory()` | Open data folder in file manager |
| `clearRuntimeDataAndExit()` | Clear all data and exit application |

##### `DeveloperViewModel.kt`
| Method | Purpose |
|--------|---------|
| `isDevEnvironment()` | Check if running in development mode |
| `dumpClipboardInstruction()` | Dump ShadowBot instructions from clipboard to project resources |

---

### Desktop Platform Implementation (`desktopMain/`)

#### `main.kt`
- **Function:** `main()`
- **Purpose:** Desktop application entry point
- **Features:** Initialize Koin, configure window, start application

#### `data/DatabaseDriverFactory.kt`
- **Purpose:** Desktop-specific SQLite driver using JDBC
- **Database Path:** `{user.home}/.filewebhook/filewebhook.db`

#### `domain/clipboard/ClipboardManager.kt`
- **Purpose:** Windows-specific clipboard implementation using JNA
- **Features:**
  - Direct Windows API calls (OpenClipboard, GetClipboardData, SetClipboardData)
  - Support for all clipboard formats including ShadowBot custom formats
  - Variable replacement in ShadowBot.Flow.Blocks and HTML Format
- **Helper Interfaces:** `CustomUser32`, `CustomKernel32` - JNA bindings

#### `domain/python/DesktopScriptPathProvider.kt`
- **Purpose:** Desktop-specific script path resolution
- **Logic:**
  - Development: Use source directory (`composeApp/src/commonMain/kotlin/shadowbot/`)
  - Production: Extract from JAR to `{user.home}/.filewebhook/scripts/`

#### `domain/python/ScriptResourceExtractor.kt`
- **Purpose:** Extract Python scripts from JAR to local filesystem
- **Extracted Scripts:**
  - `dump_clipboard_to_file.py`
  - `load_instructions_to_clipboard.py`
  - `modify_shadowbot_instructions.py`
  - `BaseFileWebHookAppFramework.pkl`

---

## SQLDelight Schema

**File:** `commonMain/sqldelight/com/filewebhook/db/FileWebHook.sq`

### Tables

| Table | Purpose | Key Columns |
|-------|---------|-------------|
| `users` | External service users | userId (unique), name, secretKey, callbackUrl, callbackHeaders |
| `triggers` | RPA trigger definitions | id (auto-increment), name, description, folderPath |
| `user_trigger_permissions` | User-trigger access control (M:N) | userId, triggerId, UNIQUE constraint, CASCADE delete |
| `execution_logs` | Execution event tracking | eventId (unique), triggerId, userId, status, timestamps, responseParams |
| `settings` | Key-value configuration | settingKey (PK), settingValue |

---

## API Endpoints

**For External Services:**
- `POST /trigger/execute` - Trigger an RPA application

**For ShadowBot Callbacks:**
- `POST /triggered` - Notification that execution started
- `POST /notify` - Notification with execution results

---

## Key Configuration

| Setting | Description | Default |
|---------|-------------|---------|
| `triggerFilesPath` | Directory for trigger files | `{user.home}/.filewebhook/triggerFiles` |
| `globalQueueMaxLength` | Max queue size | 50 |
| `httpPort` | HTTP server port | 8089 |
| `fileWebHookName` | Instance identifier for callbacks | - |
| `fileWebHookSecretKey` | Authentication key for external service callbacks | - |
| `pythonInterpreterPath` | Custom Python path (auto-detected if empty) | - |

---

## Security Requirements

- Request body max 1MB
- JSON nesting max 10 levels deep
- Max 100 fields per object
- Filter path traversal characters (`../`, `..\\`)
- Filter XSS patterns (`<script`, `javascript:`, etc.)
- Files only written to triggerFiles directory
- eventId format: `{triggerId}-{timestamp_ms}`

---

## UI Features

- Primary color: `#FC6868` (ShadowBot pink)
- Six main pages: Trigger Management, User Management, Permissions, Execution Logs, Settings, Developer Tools
- Left sidebar navigation with icons
- Material 3 Design
- Data tables with pagination and filtering

---

## Code Style

- All comments in Chinese
- File headers with module description
- KDoc documentation for classes and functions
- Follow Kotlin official code style

---

## Build Target

**Currently configured for Windows only:**
- Windows: `.msi` / `.exe`
- macOS and Linux: build targets exist in gradle but are not configured

---

## Runtime Behavior

- HTTP server and task queue **must be manually started** via Settings screen (not auto-started)
- SQLDelight generates code in `composeApp/build/generated/sqldelight/code/`
- ShadowBot callback timeout: 30 minutes
- Task queue is FIFO (serial execution - only one task runs at a time)
- Queue uses Kotlin Channel with bounded capacity
- Each task lifecycle: REQUESTED → QUEUED → FILE_WRITTEN → EXECUTING → COMPLETED → CALLBACK_SUCCESS/CALLBACK_FAILED

---

## Integration Details

**Event ID Format:** `{triggerId}-{timestamp_ms}` - critical for callback matching throughout the system

**External Service Callback URL Pattern:**
```
{callbackUrl}/{fileWebHookName}/filewebhook/notify
```

**Dependency Injection:** Uses Koin with three modules:
- `dataModule` - Database driver, repositories
- `domainModule` - CallbackClient, FileService, TaskQueue, ClipboardManager, PythonExecutor
- `serverModule` - FileWebHookServer

---

## Claude Code Notes for Windows

**Executing Gradle commands on Windows:**

Claude Code's Bash tool on Windows cannot directly recognize `.bat` files. Must use `cmd //c` with **full absolute path**.

**Incorrect examples (will fail):**
```bash
gradlew.bat :composeApp:build                    # bash cannot find command
.\gradlew.bat :composeApp:build                  # same issue
cmd //c "gradlew.bat :composeApp:build"          # relative path invalid
```

**Correct examples:**
```bash
cmd //c "C:/project/ShadowBot-FileWebHook/gradlew.bat :composeApp:build"
cmd //c "C:/project/ShadowBot-FileWebHook/gradlew.bat :composeApp:run"
cmd //c "C:/project/ShadowBot-FileWebHook/gradlew.bat :composeApp:packageMsi"
```

**Key points:**
1. Use `cmd //c` prefix to call Windows commands
2. Use full absolute path to `gradlew.bat`
3. Use forward slashes `/` instead of backslashes `\`
