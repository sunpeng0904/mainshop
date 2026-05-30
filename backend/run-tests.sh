#!/bin/bash

# 运行所有测试

echo "=========================================="
echo "  在线商城 - 自动化测试"
echo "=========================================="
echo ""

# 检查应用是否运行
echo "1. 检查应用服务..."
if curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
    echo "   ✅ 应用服务运行中"
else
    echo "   ❌ 应用服务未启动"
    echo ""
    echo "请先启动应用服务："
    echo "  ./start.sh"
    echo ""
    exit 1
fi

echo ""
echo "2. 运行单元测试..."
mvn test -Dtest=UserAddrServiceImplTest -q

echo ""
echo "3. 运行API测试..."
npm run test:address

echo ""
echo "4. 运行安全测试..."
npm run test:security

echo ""
echo "=========================================="
echo "  测试完成"
echo "=========================================="
echo ""
echo "查看详细报告: npm run test:report"
