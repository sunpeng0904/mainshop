@echo off
chcp 65001 >nul

echo ==========================================
echo   在线商城 - 自动化测试
echo ==========================================
echo.

REM 检查应用是否运行
echo 1. 检查应用服务...
curl -s http://localhost:8080/actuator/health >nul 2>&1
if %errorlevel% neq 0 (
    echo    ❌ 应用服务未启动
    echo.
    echo 请先启动应用服务：
    echo   start.bat
    echo.
    pause
    exit /b 1
)
echo    ✅ 应用服务运行中

echo.
echo 2. 运行单元测试...
call mvn test -Dtest=UserAddrServiceImplTest -q

echo.
echo 3. 运行API测试...
call npm run test:address

echo.
echo 4. 运行安全测试...
call npm run test:security

echo.
echo ==========================================
echo   测试完成
echo ==========================================
echo.
echo 查看详细报告: npm run test:report
echo.
pause
