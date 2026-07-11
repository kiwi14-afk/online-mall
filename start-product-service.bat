@echo off
chcp 65001 >nul
set "BASE=%~dp0"

echo ========================================
echo    product-service 启动中 (port 8082)
echo ========================================
echo.
echo 工作目录: %BASE%product-service
echo.

cd /d "%BASE%product-service"
if %errorlevel% neq 0 (
    echo [错误] 无法进入目录！
    pause
    exit /b
)

echo 正在启动 Maven...
call mvnw.cmd spring-boot:run -DskipTests
if %errorlevel% neq 0 (
    echo.
    echo [错误] 启动失败，请查看上方错误信息
    pause
) else (
    echo.
    echo product-service 已停止
    pause
)
