@echo off
REM ===================================================================
REM 脚本名称: ShadowbotAppFormwork_InstructDataCopyPaste.bat
REM 功能描述: 读取 InstructData 文件并将内容写入系统剪贴板
REM 用途: 用于恢复影刀流程指令，实现跨平台、跨主机的复制粘贴
REM ===================================================================

setlocal enabledelayedexpansion

REM 获取脚本所在目录
set SCRIPT_DIR=%~dp0

REM 设置源文件路径
set SOURCE_FILE=%SCRIPT_DIR%InstructData

echo ===================================================================
echo 影刀流程指令恢复工具
echo ===================================================================
echo.

REM 检查文件是否存在
if not exist "%SOURCE_FILE%" (
    echo [错误] 文件不存在: %SOURCE_FILE%
    echo.
    echo [提示] 请先使用 ShadowbotAppFormwork_InstructDataCopy.bat 保存剪贴板内容
    echo ===================================================================
    pause
    exit /b 1
)

echo [提示] 正在读取文件: %SOURCE_FILE%
echo.

REM 使用 PowerShell 读取文件内容并写入剪贴板（保留原始格式）
powershell -Command "$content = [System.IO.File]::ReadAllText('%SOURCE_FILE%', [System.Text.Encoding]::UTF8); if ($content) { Set-Clipboard -Value $content; Write-Host '[成功] 文件内容已复制到剪贴板' -ForegroundColor Green } else { Write-Host '[错误] 文件内容为空' -ForegroundColor Red; exit 1 }"

if %ERRORLEVEL% EQU 0 (
    echo.
    echo [提示] 您现在可以在影刀应用编辑页面按 Ctrl+V 粘贴流程指令
) else (
    echo.
    echo [失败] 无法读取文件内容，请检查文件是否有效
)

echo.
echo ===================================================================
pause
