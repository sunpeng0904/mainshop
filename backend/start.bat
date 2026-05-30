@echo off
chcp 65001 >nul

echo 正在启动在线商城后端服务...
echo.

REM 编译打包
echo 1. 编译打包...
call mvn clean package -DskipTests -q

if %errorlevel% neq 0 (
    echo 编译失败！
    pause
    exit /b 1
)

echo 编译成功！
echo.

REM 启动应用
echo 2. 启动应用...
echo 服务地址: http://localhost:8080
echo Swagger文档: http://localhost:8080/swagger-ui.html
echo.
echo 按 Ctrl+C 停止服务
echo.

java -jar target\*.jar
