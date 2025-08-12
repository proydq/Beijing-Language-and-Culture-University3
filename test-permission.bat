@echo off
echo === 测试权限控制 ===

REM 测试登录
echo.
echo 1. 测试登录...
curl -X POST http://localhost:10086/authentication/login -H "Content-Type: application/json" -d "{\"username\":\"test\",\"password\":\"123456\",\"customerId\":\"demo\"}"

echo.
echo.
echo 请手动复制上面返回的token，然后测试其他接口
echo.
pause