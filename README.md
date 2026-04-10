# 在线商城项目

## 项目概述
一个简单的 SpringBoot + Vue 在线商城系统。

## 技术栈
- **后端**: SpringBoot 3.2.5 + MyBatis Plus 3.5.5 + MySQL 8.0
- **前端**: Vue 3.4 + Element Plus 2.4 + Axios 1.6
- **数据库**: MySQL 8.0 (127.0.0.1:3306, root/root)
- **开发工具**: IntelliJ IDEA + VS Code

## 功能模块
1. **用户模块**: 注册、登录、个人信息管理
2. **商品模块**: 商品展示、分类、搜索、详情
3. **购物车模块**: 添加商品、修改数量、删除
4. **订单模块**: 下单、支付、订单管理
5. **后台管理**: 商品管理、订单管理、用户管理

## 快速开始

### 1. 环境准备
```bash
# 安装 Java 17+
# 安装 Node.js 18+
# 安装 MySQL 8.0
```

### 2. 数据库初始化
```sql
-- 创建数据库
CREATE DATABASE IF NOT EXISTS online_mall DEFAULT CHARSET utf8mb4;

-- 使用数据库
USE online_mall;

-- 运行 database/mall.sql 初始化表结构
```

### 3. 启动后端
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

### 4. 启动前端
```bash
cd frontend
npm install
npm run serve
```

### 5. 访问地址
- 前端: http://localhost:8080
- 后端API: http://localhost:8081
- Swagger文档: http://localhost:8081/swagger-ui.html

## 项目结构说明
- `backend/` - SpringBoot后端项目
- `frontend/` - Vue前端项目
- `database/` - 数据库脚本
- `docs/` - 项目文档

## 开发指南
1. 使用 IntelliJ IDEA 打开 `backend/` 目录
2. 使用 VS Code 打开 `frontend/` 目录
3. 配置数据库连接: 127.0.0.1:3306, root/root
4. 按照快速开始步骤启动项目

## 接口规范
- RESTful API 设计
- JSON 数据格式
- JWT 认证
- 统一响应格式

## 部署说明
### 本地部署
```bash
# 后端打包
mvn clean package -DskipTests

# 前端打包
npm run build

# 运行jar包
java -jar backend/target/online-mall.jar
```

### 生产部署
- Nginx 反向代理
- Docker 容器化
- 数据库主从复制

## 注意事项
1. 确保 MySQL 服务已启动
2. 确保端口 8080 和 8081 未被占用
3. 首次运行需要初始化数据库
4. 开发环境使用 H2 内存数据库便于测试

## 联系方式
- 项目维护: OpenClaw Assistant
- 创建时间: 2025-04-07
- 最后更新: 2025-04-07