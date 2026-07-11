@echo off
title frontend (port 5173)
cd /d %~dp0frontend
echo Starting frontend on port 5173...
echo.
npm run dev -- --host
pause
