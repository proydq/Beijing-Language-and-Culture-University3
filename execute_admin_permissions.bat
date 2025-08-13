@echo off
echo =====================================================
echo 管理员设置模块权限资源添加脚本
echo =====================================================
echo.

set MYSQL_HOST=localhost
set MYSQL_PORT=3306
set MYSQL_USER=root
set MYSQL_PASSWORD=root
set MYSQL_DATABASE=your_database_name

echo 请确认数据库连接信息：
echo 主机: %MYSQL_HOST%
echo 端口: %MYSQL_PORT%
echo 用户: %MYSQL_USER%
echo 数据库: %MYSQL_DATABASE%
echo.
echo 如果信息不正确，请按 Ctrl+C 退出并修改此脚本
pause

echo.
echo 1. 执行管理员管理权限添加脚本...
mysql -h %MYSQL_HOST% -P %MYSQL_PORT% -u %MYSQL_USER% -p%MYSQL_PASSWORD% %MYSQL_DATABASE% < "backend\src\main\resources\sql\add_admin_management_permissions.sql"
if %errorlevel% neq 0 (
    echo [错误] 权限添加脚本执行失败！
    pause
    exit /b 1
)
echo [成功] 权限添加完成！

echo.
echo 2. 执行管理员管理菜单配置脚本...
mysql -h %MYSQL_HOST% -P %MYSQL_PORT% -u %MYSQL_USER% -p%MYSQL_PASSWORD% %MYSQL_DATABASE% < "backend\src\main\resources\sql\add_admin_management_menu.sql"
if %errorlevel% neq 0 (
    echo [错误] 菜单配置脚本执行失败！
    pause
    exit /b 1
)
echo [成功] 菜单配置完成！

echo.
echo 3. 执行权限验证脚本...
mysql -h %MYSQL_HOST% -P %MYSQL_PORT% -u %MYSQL_USER% -p%MYSQL_PASSWORD% %MYSQL_DATABASE% < "backend\src\main\resources\sql\verify_admin_permissions.sql"
if %errorlevel% neq 0 (
    echo [错误] 权限验证脚本执行失败！
    pause
    exit /b 1
)

echo.
echo =====================================================
echo 所有脚本执行完成！
echo =====================================================
echo.
echo 后续操作建议：
echo 1. 重启应用程序
echo 2. 清除权限缓存
echo 3. 重新登录系统
echo 4. 访问管理员管理页面测试功能
echo.
pause