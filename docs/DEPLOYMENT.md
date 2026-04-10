# 在线商城项目部署指南

## 环境要求

### 1. 开发环境
- **Java**: JDK 17+
- **Node.js**: 18+
- **MySQL**: 8.0+
- **Maven**: 3.6+
- **IDE**: IntelliJ IDEA + VS Code

### 2. 数据库配置
```
主机: 127.0.0.1:3306
用户名: root
密码: root
数据库: online_mall
```

## 项目结构

```
online-mall/
├── backend/          # SpringBoot后端
│   ├── src/
│   ├── pom.xml
│   └── application.yml
├── frontend/         # Vue前端
│   ├── src/
│   ├── package.json
│   └── vue.config.js
├── database/         # 数据库脚本
│   └── mall.sql
└── docs/            # 文档
```

## 快速开始

### 步骤1：导入项目到 IDEA

1. 打开 IntelliJ IDEA
2. 选择 `File` → `Open`
3. 选择 `D:\项目\openclaw\test\online-mall\backend`
4. 等待 Maven 依赖下载完成

### 步骤2：初始化数据库

1. 启动 MySQL 服务
2. 运行数据库脚本：
```sql
-- 方法1：使用MySQL客户端
mysql -u root -p < database/mall.sql

-- 方法2：在IDEA中运行
-- 打开 database/mall.sql
-- 右键选择 "Run" 或按 Ctrl+Shift+F10
```

### 步骤3：配置后端

1. 检查 `backend/src/main/resources/application.yml`
2. 确认数据库配置正确：
```yaml
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/online_mall
    username: root
    password: root
```

### 步骤4：启动后端服务

**方法1：使用 IDEA 运行**
1. 打开 `OnlineMallApplication.java`
2. 右键选择 `Run 'OnlineMallApplication.main()'`
3. 等待启动完成（控制台显示 "Started OnlineMallApplication"）

**方法2：使用 Maven 命令**
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

**验证后端启动**
- 访问：http://localhost:8081/api/swagger-ui.html
- 应该能看到 Swagger API 文档

### 步骤5：配置前端

1. 打开 VS Code
2. 打开 `frontend` 目录
3. 安装依赖：
```bash
cd frontend
npm install
```

### 步骤6：启动前端服务

```bash
cd frontend
npm run serve
```

**验证前端启动**
- 访问：http://localhost:8080
- 应该能看到商城首页

## 开发指南

### 后端开发

#### 创建新的API接口
1. 在 `controller` 包下创建新的 Controller
2. 在 `service` 包下创建 Service 接口和实现
3. 在 `mapper` 包下创建 Mapper 接口
4. 在 `entity` 包下创建实体类

#### 示例：用户注册接口
```java
@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/register")
    public Result register(@RequestBody UserRegisterDTO dto) {
        return userService.register(dto);
    }
}
```

### 前端开发

#### 创建新的页面
1. 在 `views` 目录下创建新的 Vue 组件
2. 在 `router/index.js` 中添加路由配置
3. 在 `api` 目录下创建对应的 API 接口
4. 在 `store` 目录下创建状态管理（如果需要）

#### 示例：商品列表页面
```vue
<template>
  <div class="product-list">
    <el-row :gutter="20">
      <el-col v-for="product in products" :key="product.id" :span="6">
        <product-card :product="product" />
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { getProductList } from '@/api/product'

export default {
  data() {
    return {
      products: []
    }
  },
  async created() {
    await this.loadProducts()
  },
  methods: {
    async loadProducts() {
      const res = await getProductList()
      this.products = res.data
    }
  }
}
</script>
```

## 测试

### 后端测试
```bash
cd backend
mvn test
```

### 前端测试
```bash
cd frontend
npm run test:unit
```

### API 测试
使用 Postman 或 Swagger 测试接口：
- Swagger UI: http://localhost:8081/api/swagger-ui.html
- API 文档: http://localhost:8081/api/api-docs

## 打包部署

### 后端打包
```bash
cd backend
mvn clean package -DskipTests
# 生成文件: backend/target/online-mall-backend-1.0.0.jar
```

### 前端打包
```bash
cd frontend
npm run build
# 生成文件: frontend/dist/
```

### 生产环境部署
1. 部署后端 Jar 包到服务器
2. 部署前端静态文件到 Nginx
3. 配置数据库连接
4. 配置域名和 SSL 证书

## 常见问题

### 1. 数据库连接失败
- 检查 MySQL 服务是否启动
- 检查用户名密码是否正确
- 检查防火墙是否开放 3306 端口

### 2. 端口冲突
- 后端默认端口：8081
- 前端默认端口：8080
- 修改端口：在 `application.yml` 或 `vue.config.js` 中修改

### 3. 依赖下载失败
- 检查网络连接
- 配置 Maven 镜像源
- 配置 npm 镜像源

### 4. 跨域问题
- 开发环境已配置代理
- 生产环境需要配置 Nginx 反向代理

## 项目配置

### 后端配置 (application.yml)
```yaml
server:
  port: 8081
  servlet:
    context-path: /api

spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/online_mall
    username: root
    password: root
```

### 前端配置 (vue.config.js)
```javascript
devServer: {
  port: 8080,
  proxy: {
    '/api': {
      target: 'http://localhost:8081',
      changeOrigin: true
    }
  }
}
```

## 监控和日志

### 后端日志
- 日志文件：`logs/online-mall.log`
- 日志级别：在 `application.yml` 中配置

### 前端监控
- 使用浏览器开发者工具
- 使用 Vue Devtools 插件

## 性能优化

### 后端优化
1. 数据库连接池配置
2. Redis 缓存
3. 接口限流
4. SQL 优化

### 前端优化
1. 代码分割
2. 图片懒加载
3. 组件按需加载
4. CDN 加速

## 安全建议

1. 修改默认密码
2. 启用 HTTPS
3. 配置防火墙
4. 定期备份数据
5. 更新依赖版本

## 联系方式

- 项目维护：OpenClaw Assistant
- 创建时间：2025-04-07
- 最后更新：2025-04-07

---

**祝您开发愉快！**