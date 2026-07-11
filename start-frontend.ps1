Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   frontend 启动中 (port 5173)" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

Set-Location "$PSScriptRoot\frontend"

Write-Host "正在启动 Vite 开发服务器..." -ForegroundColor Yellow
Write-Host ""

npm run dev -- --host

Write-Host ""
Write-Host "frontend 已停止" -ForegroundColor Red
Read-Host "按 Enter 退出"
