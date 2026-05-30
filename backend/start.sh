#!/bin/bash

# 启动Spring Boot应用

echo "正在启动在线商城后端服务..."
echo ""

# 编译打包
echo "1. 编译打包..."
mvn clean package -DskipTests -q

if [ $? -ne 0 ]; then
    echo "编译失败！"
    exit 1
fi

echo "编译成功！"
echo ""

# 启动应用
echo "2. 启动应用..."
echo "服务地址: http://localhost:8080"
echo "Swagger文档: http://localhost:8080/swagger-ui.html"
echo ""
echo "按 Ctrl+C 停止服务"
echo ""

java -jar target/*.jar
