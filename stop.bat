@echo off
chcp 65001 >nul
title 巡检管理系统 - Docker 停止

echo ============================================
echo   巡检管理系统 - Docker 停止
echo ============================================
echo.

cd /d "%~dp0"
docker compose down

echo.
echo 已停止所有容器（数据卷已保留）
echo.
echo 重新启动：start.bat
echo 清除数据：docker compose down -v
echo.

pause