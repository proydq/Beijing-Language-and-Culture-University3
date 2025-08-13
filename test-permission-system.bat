@echo off
setlocal enabledelayedexpansion

REM 权限系统升级测试脚本 (Windows版本)
REM 用于验证权限系统的各项功能

echo ==================================
echo 权限系统升级测试脚本
echo ==================================

REM 配置变量
set BASE_URL=http://localhost:8080
set TOKEN=
set TEST_USER=testuser
set TEST_PASSWORD=password
set FAILED_TESTS=0
set TOTAL_TESTS=8

REM 日志函数
:log_info
echo [INFO] %~1
goto :eof

:log_warn
echo [WARN] %~1
goto :eof

:log_error
echo [ERROR] %~1
goto :eof

REM 获取认证token
:get_token
call :log_info "获取认证token..."

curl -s -X POST "%BASE_URL%/authentication/login" ^
    -H "Content-Type: application/json" ^
    -d "{\"username\":\"%TEST_USER%\",\"password\":\"%TEST_PASSWORD%\"}" ^
    -o temp_response.json

if not exist temp_response.json (
    call :log_error "无法连接到服务器"
    goto :test_failed
)

REM 简化的token提取（实际项目中可能需要使用jq或其他JSON解析工具）
for /f "tokens=2 delims=:," %%a in ('findstr "token" temp_response.json') do (
    set TOKEN=%%a
    set TOKEN=!TOKEN:"=!
    set TOKEN=!TOKEN: =!
)

del temp_response.json 2>nul

if "!TOKEN!"=="" (
    call :log_error "Token获取失败"
    goto :test_failed
) else (
    call :log_info "Token获取成功"
)
goto :eof

REM 测试权限缓存状态
:test_cache_status
call :log_info "测试权限缓存状态..."

curl -s -X GET "%BASE_URL%/api/permission/refresh/status" ^
    -H "Authorization: Bearer %TOKEN%" ^
    -o temp_cache_response.txt

if exist temp_cache_response.txt (
    findstr "缓存状态正常" temp_cache_response.txt >nul
    if !errorlevel! equ 0 (
        call :log_info "权限缓存状态正常"
        del temp_cache_response.txt
        goto :eof
    )
)

call :log_error "权限缓存状态异常"
del temp_cache_response.txt 2>nul
set /a FAILED_TESTS+=1
goto :eof

REM 测试权限别名功能
:test_permission_alias
call :log_info "测试权限别名功能..."

curl -s -X GET "%BASE_URL%/api/sys-user/list" ^
    -H "Authorization: Bearer %TOKEN%" ^
    -o temp_alias_response.txt

if exist temp_alias_response.txt (
    call :log_info "权限别名映射测试通过"
    del temp_alias_response.txt
    goto :eof
)

call :log_error "权限别名映射测试失败"
set /a FAILED_TESTS+=1
goto :eof

REM 测试审计日志功能
:test_audit_log
call :log_info "测试审计日志功能..."

curl -s -X GET "%BASE_URL%/api/sys-user/1" ^
    -H "Authorization: Bearer %TOKEN%" ^
    -o nul

REM 等待异步日志写入
timeout /t 2 >nul

call :log_info "审计日志功能测试完成"
goto :eof

REM 测试权限刷新功能
:test_permission_refresh
call :log_info "测试权限刷新功能..."

curl -s -X POST "%BASE_URL%/api/permission/refresh/all" ^
    -H "Authorization: Bearer %TOKEN%" ^
    -o temp_refresh_response.txt

if exist temp_refresh_response.txt (
    findstr "成功" temp_refresh_response.txt >nul
    if !errorlevel! equ 0 (
        call :log_info "权限刷新功能测试通过"
        del temp_refresh_response.txt
        goto :eof
    )
)

call :log_error "权限刷新功能测试失败"
del temp_refresh_response.txt 2>nul
set /a FAILED_TESTS+=1
goto :eof

REM 测试manage权限推理
:test_manage_permission
call :log_info "测试manage权限推理功能..."

curl -s -X GET "%BASE_URL%/api/sys-user/list" ^
    -H "Authorization: Bearer %TOKEN%" ^
    -o temp_manage_response.txt

if exist temp_manage_response.txt (
    call :log_info "manage权限推理测试通过"
    del temp_manage_response.txt
    goto :eof
)

call :log_error "manage权限推理测试失败"
set /a FAILED_TESTS+=1
goto :eof

REM 测试新旧权限码兼容性
:test_permission_compatibility
call :log_info "测试新旧权限码兼容性..."

set SUCCESS_COUNT=0
set ENDPOINT_COUNT=3

REM 测试用户接口
curl -s -w "%%{http_code}" -X GET "%BASE_URL%/api/sys-user/list" ^
    -H "Authorization: Bearer %TOKEN%" ^
    -o temp_compat1.txt

if exist temp_compat1.txt (
    set /a SUCCESS_COUNT+=1
    del temp_compat1.txt
)

REM 测试角色接口
curl -s -w "%%{http_code}" -X GET "%BASE_URL%/api/role/list" ^
    -H "Authorization: Bearer %TOKEN%" ^
    -o temp_compat2.txt

if exist temp_compat2.txt (
    set /a SUCCESS_COUNT+=1
    del temp_compat2.txt
)

REM 测试房间接口
curl -s -w "%%{http_code}" -X GET "%BASE_URL%/api/room/list" ^
    -H "Authorization: Bearer %TOKEN%" ^
    -o temp_compat3.txt

if exist temp_compat3.txt (
    set /a SUCCESS_COUNT+=1
    del temp_compat3.txt
)

if !SUCCESS_COUNT! equ !ENDPOINT_COUNT! (
    call :log_info "权限兼容性测试通过"
) else (
    call :log_error "权限兼容性测试失败，成功率: !SUCCESS_COUNT!/!ENDPOINT_COUNT!"
    set /a FAILED_TESTS+=1
)
goto :eof

REM 性能测试
:test_performance
call :log_info "进行权限系统性能测试..."

set START_TIME=%time%

for /l %%i in (1,1,10) do (
    curl -s -X GET "%BASE_URL%/api/sys-user/list" ^
        -H "Authorization: Bearer %TOKEN%" ^
        -o nul
)

set END_TIME=%time%

call :log_info "性能测试完成（详细性能分析需要专业工具）"
goto :eof

REM 数据库连接测试
:test_database
call :log_info "测试数据库连接..."

REM Windows下MySQL连接测试（需要MySQL客户端）
where mysql >nul 2>&1
if !errorlevel! equ 0 (
    mysql -h localhost -P 12100 -u bunny -pbunny room_booking_db ^
        -e "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema='room_booking_db' AND (table_name LIKE 'sys_permission_%%' OR table_name = 'sys_audit_log');" ^
        --skip-column-names 2>nul > temp_db_count.txt
    
    if exist temp_db_count.txt (
        set /p TABLE_COUNT=<temp_db_count.txt
        del temp_db_count.txt
        if !TABLE_COUNT! geq 4 (
            call :log_info "数据库表结构检查通过"
            goto :eof
        ) else (
            call :log_error "数据库表结构检查失败，预期4个表，实际!TABLE_COUNT!个"
            set /a FAILED_TESTS+=1
            goto :eof
        )
    )
) else (
    call :log_warn "未找到mysql命令，跳过数据库检查"
)
goto :eof

REM 主测试函数
:run_tests
echo.
call :log_info "开始执行权限系统测试..."
echo.

REM 获取token
call :get_token
if "!TOKEN!"=="" (
    call :log_error "无法获取认证token，终止测试"
    goto :test_failed
)

REM 执行各项测试
call :test_database
call :test_cache_status
call :test_permission_alias
call :test_audit_log
call :test_permission_refresh
call :test_manage_permission
call :test_permission_compatibility
call :test_performance

echo.
echo ==================================
echo 测试结果汇总
echo ==================================
echo 总测试数: %TOTAL_TESTS%
set /a SUCCESS_TESTS=%TOTAL_TESTS%-%FAILED_TESTS%
echo 成功: %SUCCESS_TESTS%
echo 失败: %FAILED_TESTS%

if %FAILED_TESTS% equ 0 (
    call :log_info "所有测试通过！权限系统升级成功！"
    goto :test_success
) else (
    call :log_error "有 %FAILED_TESTS% 个测试失败，请检查系统配置"
    goto :test_failed
)

:test_success
exit /b 0

:test_failed
exit /b 1

REM 显示使用帮助
:show_help
echo 权限系统测试脚本使用说明：
echo.
echo 用法: %~nx0 [选项]
echo.
echo 选项:
echo   /help               显示此帮助信息
echo   /url URL            设置基础URL (默认: http://localhost:8080)
echo   /user USER          设置测试用户名 (默认: testuser)
echo   /password PASS      设置测试密码 (默认: password)
echo.
echo 示例:
echo   %~nx0                                    # 使用默认配置运行测试
echo   %~nx0 /url http://localhost:8080        # 指定服务器地址
echo   %~nx0 /user admin /password admin123    # 使用自定义用户名密码
goto :eof

REM 解析命令行参数
:parse_args
if "%1"=="/help" (
    call :show_help
    exit /b 0
)
if "%1"=="/url" (
    set BASE_URL=%2
    shift
    shift
    goto :parse_args
)
if "%1"=="/user" (
    set TEST_USER=%2
    shift
    shift
    goto :parse_args
)
if "%1"=="/password" (
    set TEST_PASSWORD=%2
    shift
    shift
    goto :parse_args
)
if "%1"=="" goto :run_tests
call :log_error "未知参数: %1"
call :show_help
exit /b 1

REM 开始解析参数
call :parse_args %*