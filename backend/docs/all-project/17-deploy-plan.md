# 部署方案

## 1. 部署信息
- 部署日期：2026-06-25
- 部署版本：v1.0.0
- 部署人员：孙朋
- 审批人员：孙朋

## 2. 部署内容

### 2.1 应用部署
| 应用 | 版本 | 部署方式 | 说明 |
|------|------|----------|------|
| online-mall-backend | v1.0.0 | Spring Boot JAR | 地址管理模块优化 |

### 2.2 数据库变更
| 变更类型 | 变更内容 | 执行顺序 | 说明 |
|----------|----------|----------|------|
| DDL | 添加地址表复合索引 | 1 | 优化查询性能 |
| DDL | 添加区域表索引 | 2 | 优化区域查询 |

### 2.3 配置变更
| 配置项 | 变更前 | 变更后 | 说明 |
|--------|--------|--------|------|
| Redis缓存配置 | 无 | 启用地址缓存 | 缓存过期时间30分钟 |

## 3. 部署步骤

### 3.1 部署前准备
- [x] 代码冻结
- [x] 测试通过
- [x] 审批通过
- [ ] 通知相关人员

### 3.2 部署执行

#### 步骤1：备份数据库
```bash
# 备份数据库
mysqldump -u root -p online_mall > backup_20260625.sql
```

#### 步骤2：执行数据库变更
```bash
# 执行索引优化脚本
mysql -u root -p online_mall < src/main/resources/db/address_optimization.sql
```

#### 步骤3：停止当前服务
```bash
# 停止Spring Boot服务
pkill -f online-mall-backend.jar
```

#### 步骤4：部署新版本
```bash
# 备份旧版本
mv online-mall-backend.jar online-mall-backend.jar.bak

# 部署新版本
cp target/online-mall-backend-1.0.0.jar online-mall-backend.jar

# 启动新版本
nohup java -jar online-mall-backend.jar --spring.profiles.active=prod > app.log 2>&1 &
```

#### 步骤5：验证服务启动
```bash
# 检查服务是否启动
curl -s http://localhost:8082/api/health

# 检查日志
tail -f app.log
```

### 3.3 部署后验证
- [ ] 服务启动正常
- [ ] 接口调用正常
- [ ] 数据验证正确
- [ ] 性能指标正常

#### 验证命令
```bash
# 验证地址列表接口
curl -s -H "Authorization: Bearer {token}" http://localhost:8082/api/address/list

# 验证默认地址接口
curl -s -H "Authorization: Bearer {token}" http://localhost:8082/api/address/default
```

## 4. 回滚方案

### 4.1 回滚条件
- 服务启动失败
- 接口调用异常
- 性能指标不达标
- 数据不一致

### 4.2 回滚步骤

#### 步骤1：停止新版本服务
```bash
pkill -f online-mall-backend.jar
```

#### 步骤2：恢复旧版本
```bash
# 恢复旧版本
mv online-mall-backend.jar.bak online-mall-backend.jar

# 启动旧版本
nohup java -jar online-mall-backend.jar --spring.profiles.active=prod > app.log 2>&1 &
```

#### 步骤3：恢复数据库（如果需要）
```bash
# 恢复数据库备份
mysql -u root -p online_mall < backup_20260625.sql
```

### 4.3 回滚验证
- [ ] 服务恢复
- [ ] 数据恢复
- [ ] 功能正常

## 5. 应急预案

### 5.1 风险识别
| 风险 | 可能性 | 影响 | 应对措施 |
|------|--------|------|----------|
| 服务启动失败 | 低 | 高 | 检查日志，修复问题后重新部署 |
| 数据库连接失败 | 低 | 高 | 检查数据库配置，恢复数据库服务 |
| Redis连接失败 | 低 | 中 | 检查Redis配置，恢复Redis服务 |
| 性能不达标 | 低 | 中 | 分析瓶颈，优化代码或配置 |

### 5.2 应急联系人
| 角色 | 姓名 | 联系方式 |
|------|------|----------|
| 技术负责人 | 孙朋 | - |
| 运维负责人 | 孙朋 | - |

## 6. 时间安排
| 时间 | 活动 | 负责人 |
|------|------|--------|
| 2026-06-25 09:00 | 部署准备 | 孙朋 |
| 2026-06-25 09:30 | 数据库变更 | 孙朋 |
| 2026-06-25 10:00 | 应用部署 | 孙朋 |
| 2026-06-25 10:30 | 部署验证 | 孙朋 |
| 2026-06-25 11:00 | 部署完成 | 孙朋 |

## 7. 部署检查清单

### 7.1 部署前检查
- [ ] 代码已冻结
- [ ] 测试已通过
- [ ] 审批已通过
- [ ] 备份已完成
- [ ] 通知已发送

### 7.2 部署中检查
- [ ] 数据库变更执行成功
- [ ] 服务停止成功
- [ ] 新版本部署成功
- [ ] 服务启动成功

### 7.3 部署后检查
- [ ] 接口调用正常
- [ ] 数据验证正确
- [ ] 性能指标正常
- [ ] 日志无异常

## 8. 交付物
- 部署方案（本文档）
- 部署检查清单
- 部署记录
- 回滚记录（如有）
