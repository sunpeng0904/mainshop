# 后台管理前端 API 集成完成

## 完成内容

已将后台管理徒步路线相关页面与后端 API 进行集成。

### 1. 路线管理页面 (routes.vue)

**集成 API:**
- `getAdminHikingRoutes` - 获取路线列表
- `deleteHikingRoute` - 删除路线
- `toggleRouteStatus` - 切换路线上/下线状态

**功能:**
- 分页加载路线数据
- 支持按名称、难度、地区、状态筛选
- 支持上线/下线操作
- 支持删除路线
- 点击编辑跳转到编辑页面

### 2. 路线编辑页面 (route-edit.vue)

**集成 API:**
- `getRouteDetail` - 获取路线详情
- `createHikingRoute` - 创建新路线
- `updateHikingRoute` - 更新路线

**功能:**
- 新增/编辑路线
- 自动解析 JSON 字段 (images, track, itinerary, equipment, warnings)
- 保存时自动将数组转换为 JSON 字符串
- 支持保存草稿和立即发布

### 3. 评价管理页面 (reviews.vue)

**集成 API:**
- `getAdminHikingReviews` - 获取评价列表
- `approveHikingReview` - 审核通过
- `rejectHikingReview` - 拒绝评价
- `replyHikingReview` - 回复评价
- `deleteHikingReview` - 删除评价

**功能:**
- 分页加载评价数据
- 支持按状态筛选
- 批量审核通过/拒绝
- 批量删除
- 回复评价

### 4. API 文件 (src/api/hiking.js)

新增 11 个后台管理 API 函数:
```javascript
// 路线管理
export const getAdminHikingRoutes = (params) => {...}
export const createHikingRoute = (data) => {...}
export const updateHikingRoute = (id, data) => {...}
export const deleteHikingRoute = (id) => {...}
export const toggleRouteStatus = (id, status) => {...}

// 评价管理
export const getAdminHikingReviews = (params) => {...}
export const approveHikingReview = (id) => {...}
export const rejectHikingReview = (id) => {...}
export const replyHikingReview = (id, reply) => {...}
export const deleteHikingReview = (id) => {...}
export const getHikingRatingStats = (routeId) => {...}
```

## 访问地址

- 前端: http://localhost:8082/
- 后台管理: http://localhost:8082/#/admin/dashboard
- 路线管理: http://localhost:8082/#/admin/hiking/routes
- 评价管理: http://localhost:8082/#/admin/hiking/reviews

## 后端启动步骤

1. 编译项目:
```bash
cd backend/backend
mvn clean package
```

2. 启动服务:
```bash
java -jar target/online-mall-*.jar
```

3. 后端地址: http://localhost:8081

## 注意事项

1. 后台管理需要管理员权限登录
2. 后端数据库需已执行 `hiking-tables.sql` 脚本
3. 后端已预置 8 条测试路线数据
4. 评价需要审核后才能显示 (status='approved')

## 测试账户

使用具有 ADMIN 角色的账户登录后台管理系统。
