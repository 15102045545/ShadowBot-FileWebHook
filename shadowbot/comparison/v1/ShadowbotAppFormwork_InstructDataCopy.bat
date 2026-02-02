@echo off
REM ===================================================================
REM 脚本名称: ShadowbotAppFormwork_InstructDataCopy.bat
REM 功能描述: 将当前系统剪贴板内容保存到 InstructData 文件
REM 用途: 用于保存影刀流程指令，实现跨平台、跨主机的复制粘贴
REM ===================================================================

setlocal enabledelayedexpansion

REM 获取脚本所在目录
set SCRIPT_DIR=%~dp0

REM 设置目标文件路径
set TARGET_FILE=%SCRIPT_DIR%InstructData

echo ===================================================================
echo 影刀流程指令保存工具
echo ===================================================================
echo.
echo [提示] 正在保存剪贴板内容到: %TARGET_FILE%
echo.

REM 使用 PowerShell 获取剪贴板内容并保存到文件（保留原始格式）
powershell -Command "Get-Clipboard -Format FileDropList,Text,Image,Audio | Out-Null; $clip = Get-Clipboard -Raw; if ($clip) { [System.IO.File]::WriteAllText('%TARGET_FILE%', $clip, [System.Text.Encoding]::UTF8) } else { $bytes = Get-Clipboard -Format Image; if ($bytes) { [System.IO.File]::WriteAllBytes('%TARGET_FILE%', $bytes) } else { Write-Host '[错误] 剪贴板为空或无法读取' -ForegroundColor Red; exit 1 } }"

if %ERRORLEVEL% EQU 0 (
    echo [成功] 剪贴板内容已保存到: %TARGET_FILE%
    echo.
    echo [提示] 您现在可以将此文件分享给其他人
    echo [提示] 其他人使用 ShadowbotAppFormwork_InstructDataCopyPaste.bat 即可恢复内容
) else (
    echo [失败] 无法保存剪贴板内容，请检查剪贴板是否有内容
)

echo.
echo ===================================================================
pause
