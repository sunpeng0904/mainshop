# 部署方案

## 基本信息

- **项目名称**: 在线商城 - 地址管理模块优化
- **部署版本**: v1.0.0
- **部署时间**: 2026-05-29
- **负责人**: 部署Agent

## 部署内容

### 代码变更

| 模块 | 文件 | 变更类型 |
|------|------|---------|
| Entity | UserAddr.java | 新增 |
| Entity | Region.java | 更新 |
| DTO | AddressDTO.java | 更新 |
| VO | AddressVO.java | 更新 |
| Mapper | UserAddrMapper.java | 新增 |
| Service | UserAddrService.java | 新增 |
| Service | UserAddrServiceImpl.java | 新增 |
| Controller | AddrController.java | 新增 |
| SQL | address_table_attrc2e.sql | 新增 |

### 数据库变更

| 变更项 | 说明 |
|--------|------|
| 新增表 | user_addr_tb, region_tb |
| 新增索引 | idx_user_addr_user_id, idx_user_addr_user_id_dft |
| 数据迁移 | 无（新模块） |

## 部署步骤

### 阶段一：数据库准备

```sql
-- 1. 执行DDL脚本
source src/main/resources/db/address_table_attrc2e.sql;

-- 2. 验证表结构
DESCRIBE user_addr_tb;
DESCRIBE region_tb;

-- 3. 验证索引
SHOW INDEX FROM user_addr_tb;
```

### 阶段二：应用部署

```bash
# 1. 拉取最新代码
git pull origin master

# 2. 编译打包
mvn clean package -DskipTests

# 3. 停止服务
./stop.sh

# 4. 备份旧版本
cp app.jar app.jar.bak

# 5. 部署新版本
cp target/app.jar ./

# 6. 启动服务
./start.sh
```

### 阶段三：验证

```bash
# 1. 健康检查
curl http://localhost:8080/actuator/health

# 2. 接口验证
curl -X GET http://localhost:8080/api/address/list \
  -H "Authorization: Bearer {token}"

# 3. 日志检查
tail -f logs/app.log | grep "ERROR"
```

## 回滚方案

### 触发条件
- 接口响应超时 > 5s
- 错误率 > 5%
- 数据库连接异常

### 回滚步骤

```bash
# 1. 停止服务
./stop.sh

# 2. 恢复旧版本
cp app.jar.bak app.jar

# 3. 回滚数据库（如需要）
mysql -u root -p < rollback.sql

# 4. 启动服务
./start.sh
```

## 环境配置

### 数据库配置

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/online_mall?useUnicode=true&characterEncoding=utf8
    username: root
    password: ${DB_PASSWORD}
```

## 监控指标

| 指标 | 阈值 | 监控方式 |
|------|------|---------|
| 接口响应时间 | < 100ms | Prometheus |
| 错误率 | < 1% | 日志分析 |
| CPU使用率 | < 80% | 系统监控 |
| 内存使用率 | < 80% | 系统监控 |
| 数据库连接数 | < 100 | 数据库监控 |

## 风险评估

| 风险 | 等级 | 应对措施 |
|------|------|---------|
| 数据库索引失效 | 低 | 重建索引 |
| 接口兼容性 | 低 | 版本控制，灰度发布 |
| 数据库连接池耗尽 | 低 | 调整连接池配置 |
