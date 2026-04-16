# 在线商城后端部署文档

## 环境要求

| 软件 | 版本 |
|------|------|
| JDK | 1.8+ |
| MySQL | 5.7+ / 8.0+ |
| Maven | 3.6+ |
| Redis | 6.0+ (可选，用于缓存) |

## 快速启动

### 1. 克隆项目

```bash
git clone <repository-url>
cd backend
```

### 2. 数据库配置

创建数据库：
```sql
CREATE DATABASE online_mall DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

修改 `src/main/resources/application.yml` 中的数据库连接：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/online_mall?useUnicode=true&characterEncoding=utf8mb4
    username: root
    password: your_password
```

### 3. 初始化数据

按顺序执行 SQL 脚本：

```bash
# 核心表结构（由 JPA 自动创建，或手动执行）
# 区域码值表
mysql -u root -p online_mall < src/main/resources/db/region.sql

# 抽奖活动表
mysql -u root -p online_mall < src/main/resources/db/lottery.sql
```

### 4. 编译运行

```bash
mvn clean compile
mvn spring-boot:run
```

应用启动后访问：
- API 地址: http://localhost:8082/api
- Swagger 文档: http://localhost:8082/api/swagger-ui.html

---

## 功能模块部署

### 抽奖活动（五一活动）

#### 数据库表

| 表名 | 说明 |
|------|------|
| lottery_prize | 奖品表 |
| lottery_record | 抽奖记录表 |

#### 并发安全

抽奖库存扣减使用**乐观锁**机制，防止高并发下库存超卖：

- `lottery_prize` 表包含 `version` 字段
- MyBatis-Plus `OptimisticLockerInnerInterceptor` 拦截器
- 扣减失败自动重试（最多3次）
- 重试失败降级为"谢谢参与"

#### 初始化奖品

首次启动时，系统自动初始化默认奖品（笔记本电脑、智能手机、保温杯、谢谢参与）。

#### API 接口

| 接口 | 说明 |
|------|------|
| GET /api/lottery/prizes | 获取奖品列表 |
| POST /api/lottery/draw | 执行抽奖 |
| GET /api/lottery/records | 获取用户抽奖记录 |
| POST /api/lottery/receive/{recordId} | 领取奖品 |

---

### 地址码值管理

#### 数据库表

| 表名 | 说明 |
|------|------|
| sys_region | 区域码值表（省市区） |

#### 字段变更

`user_address` 表字段已改造为码值存储：

| 旧字段 | 新字段 | 说明 |
|--------|--------|------|
| province | province_code | 省份编码 |
| city | city_code | 城市编码 |
| district | district_code | 区县编码 |

#### API 接口

| 接口 | 说明 |
|------|------|
| GET /api/region/provinces | 获取所有省份 |
| GET /api/region/cities/{provinceCode} | 获取城市列表 |
| GET /api/region/districts/{cityCode} | 获取区县列表 |

#### 前端对接

前端需改造为三级联动选择器：
1. 页面加载时调用 `/region/provinces` 获取省份列表
2. 选择省份后调用 `/region/cities/{provinceCode}` 获取城市
3. 选择城市后调用 `/region/districts/{cityCode}` 获取区县
4. 提交地址时传递 `provinceCode`、`cityCode`、`districtCode`

---

## 生产环境配置

### JVM 参数

```bash
java -Xms512m -Xmx1024m -jar target/online-mall-backend-1.0.0.jar
```

### 环境变量

| 变量 | 说明 | 默认值 |
|------|------|--------|
| SERVER_PORT | 服务端口 | 8082 |
| DB_URL | 数据库连接 | jdbc:mysql://localhost:3306/online_mall |
| DB_USERNAME | 数据库用户名 | root |
| DB_PASSWORD | 数据库密码 | - |
| JWT_SECRET | JWT密钥 | - |

### Redis 配置（可选）

```yaml
spring:
  redis:
    host: localhost
    port: 6379
    password: 
    database: 0
```

---

## 常见问题

### 1. 启动报 ClassNotFoundException

原因：编译不完整

解决：
```bash
mvn clean compile
```

### 2. 抽奖库存超卖

原因：未配置乐观锁

解决：确保 `lottery_prize` 表有 `version` 字段，且 MyBatisPlusConfig 配置了 `OptimisticLockerInnerInterceptor`

### 3. 地址码值查询为空

原因：未执行 region.sql 初始化数据

解决：
```bash
mysql -u root -p online_mall < src/main/resources/db/region.sql
```

---

## 版本历史

| 版本 | 日期 | 说明 |
|------|------|------|
| 1.0.0 | 2026-04-16 | 初始版本，包含抽奖乐观锁、地址码值改造 |
