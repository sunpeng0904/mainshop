# 在线商城后端系统

基于 Spring Boot 的在线商城后端服务，提供用户管理、商品管理、购物车、订单、支付、抽奖活动等功能。

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 2.7.18 | 基础框架 |
| Spring Security | - | 安全认证 |
| MyBatis-Plus | 3.5.x | ORM框架 |
| MySQL | 5.7+ / 8.0+ | 数据库 |
| JWT | - | Token认证 |
| Swagger | 3.0 | API文档 |

## 功能模块

| 模块 | 说明 |
|------|------|
| 用户管理 | 注册、登录、个人信息管理、角色权限 |
| 商品管理 | 商品CRUD、分类管理、国际化支持 |
| 购物车 | 添加、修改、删除、查询 |
| 订单管理 | 创建订单、订单列表、订单状态流转 |
| 支付管理 | 微信支付（沙箱模式） |
| 地址管理 | 收货地址管理、省市区三级联动 |
| 抽奖活动 | 五一活动、乐观锁库存控制 |
| 图片服务 | 图片缓存、文件上传 |
| 分享功能 | 文本/图片分享、圈子动态、点赞/收藏/转发/评论、好友关系、通知系统 |

## 快速开始

### 环境要求

- JDK 1.8+
- MySQL 5.7+
- Maven 3.6+

### 启动步骤

```bash
# 1. 克隆项目
git clone <repository-url>
cd backend

# 2. 创建数据库
mysql -u root -p -e "CREATE DATABASE online_mall DEFAULT CHARACTER SET utf8mb4;"

# 3. 修改数据库配置
# 编辑 src/main/resources/application.yml

# 4. 编译运行
mvn clean compile
mvn spring-boot:run
```

### 访问地址

- API: http://localhost:8082/api
- Swagger: http://localhost:8082/api/swagger-ui.html

## 项目结构

```
backend/
├── docs/                          # 文档目录
│   ├── DEPLOYMENT.md              # 部署文档
│   ├── API.md                     # API文档
│   ├── DATABASE.md                # 数据库设计
│   └── ARCHITECTURE.md            # 系统架构
├── src/main/java/com/online/mall/
│   ├── config/                    # 配置类
│   ├── controller/                # 控制器
│   │   └── admin/                 # 管理端接口
│   ├── dto/                       # 数据传输对象
│   ├── entity/                    # 实体类
│   ├── mapper/                    # MyBatis Mapper
│   ├── service/                   # 服务层
│   │   └── impl/                  # 服务实现
│   ├── vo/                        # 视图对象
│   ├── security/                  # 安全相关
│   ├── common/                    # 公共类
│   └── utils/                     # 工具类
├── src/main/resources/
│   ├── db/                        # SQL脚本
│   ├── i18n/                      # 国际化资源
│   └── static/                    # 静态资源
└── logs/                          # 日志目录
```

## 文档导航

- [部署文档](docs/DEPLOYMENT.md) - 环境配置、部署指南
- [API文档](docs/API.md) - 接口说明
- [数据库设计](docs/DATABASE.md) - 表结构说明
- [系统架构](docs/ARCHITECTURE.md) - 架构设计
- [分享功能需求](docs/SHARE.md) - 分享、圈子、互动功能设计
- [分享功能详细设计](docs/SHARE_DESIGN.md) - 类设计、时序图、缓存、异步处理

## 默认账号

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | admin123 | 管理员 |
| user | user123 | 普通用户 |

## License

MIT
