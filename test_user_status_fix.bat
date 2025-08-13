@echo off
echo ==========================================
echo 用户状态修复测试脚本
echo ==========================================

echo.
echo 1. 启动后端应用（包含状态修复）...
cd backend
start "Backend Server" cmd /c "mvn spring-boot:run -Dspring-boot.run.profiles=dev -q"

echo.
echo 等待后端启动（30秒）...
timeout /t 30 /nobreak

echo.
echo 2. 测试用户状态分布接口...
curl -X GET "http://localhost:10087/debug/user-status/distribution" ^
     -H "Content-Type: application/json" ^
     -H "Authorization: Bearer your-token-here" 2>nul

echo.
echo 3. 测试用户搜索接口...
curl -X POST "http://localhost:10087/system/user/search" ^
     -H "Content-Type: application/json" ^
     -H "Authorization: Bearer your-token-here" ^
     -d "{\"pageNumber\": 1, \"pageSize\": 10}" 2>nul

echo.
echo ==========================================
echo 测试完成！请检查后端控制台日志
echo ==========================================
pause