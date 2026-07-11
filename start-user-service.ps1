Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   user-service 启动中 (port 8081)" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

Set-Location "$PSScriptRoot\user-service"

Write-Host "正在启动 Spring Boot..." -ForegroundColor Yellow
Write-Host "首次编译需要下载依赖，请耐心等待..." -ForegroundColor Yellow
Write-Host "看到 'Started UserServiceApplication' 表示启动成功" -ForegroundColor Green
Write-Host ""

& .\mvnw.cmd spring-boot:run -DskipTests

Write-Host ""
Write-Host "user-service 已停止" -ForegroundColor Red
Read-Host "按 Enter 退出"
