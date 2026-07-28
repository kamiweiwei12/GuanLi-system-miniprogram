@echo off
chcp 65001 >nul
title 巡检管理系统 - Docker 启动

echo ============================================
echo   巡检管理系统 - Docker 启动
echo ============================================
echo.

:: 检查 Docker 是否运行
docker info >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] Docker 未运行，请先启动 Docker Desktop
    echo.
    pause
    exit /b 1
)

echo [1/2] Docker 已就绪
cd /d "%~dp0"

echo [2/2] 正在启动服务...
docker compose up -d

if %errorlevel% equ 0 (
    echo.
    echo ============================================
    echo   启动完成！
    echo   管理后台：http://localhost
    echo   默认管理员 Token：admin-setup-token
    echo ============================================
) else (
    echo.
    echo [错误] 启动失败，请检查 Docker 状态和日志
)

echo.
pause