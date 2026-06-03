# 需求规格说明书 (SRS) — 前端命名规范优化

## 1. 引言

### 1.1 目的
统一 online-mall 前端项目的命名规范，提升代码可读性和可维护性。

### 1.2 范围
关键模块（store、api、components、utils）的命名规范化改造。

### 1.3 术语定义
| 术语 | 说明 | 示例 |
|------|------|------|
| PascalCase | 大驼峰 | `ProductCard` |
| camelCase | 小驼峰 | `getProductList` |
| kebab-case | 短横线 | `product-card` |
| UPPER_SNAKE_CASE | 全大写下划线 | `API_BASE_URL` |

## 2. 总体描述

### 2.1 产品前景
online-mall 是 Vue 3 + Element Plus 电商前端，当前命名不统一，需要规范化。

### 2.2 用户特征
前端开发人员，熟悉 Vue 3 生态。

### 2.3 约束
- 本次仅改关键模块，不全量改造
- 改造后功能不能受影响

## 3. 功能需求

| 编号 | 需求 | 优先级 |
|------|------|--------|
| FR-001 | 组件文件名统一为 kebab-case（如 `product-card.vue`） | P0 |
| FR-002 | 组件注册名统一为 PascalCase（如 `ProductCard`） | P0 |
| FR-003 | 变量、函数名统一为 camelCase | P0 |
| FR-004 | 常量统一为 UPPER_SNAKE_CASE | P1 |
| FR-005 | store 模块命名统一规范 | P0 |
| FR-006 | api 模块命名统一规范 | P0 |
| FR-007 | 配置 ESLint 命名规则，新代码自动约束 | P1 |

## 4. 非功能需求

| 编号 | 需求 | 验收标准 |
|------|------|----------|
| NFR-001 | 构建正常 | `npm run build` 成功 |
| NFR-002 | 功能不受影响 | 页面正常运行 |
| NFR-003 | ESLint 无误报 | 命名规则无大量误报 |

## 5. 改造范围

| 模块 | 路径 | 改造内容 |
|------|------|----------|
| 组件 | `src/components/` | 文件名 → kebab-case，组件名 → PascalCase |
| 状态管理 | `src/store/` | 模块名、变量名 → camelCase |
| API | `src/api/` | 文件名 → kebab-case，函数名 → camelCase |
| 工具 | `src/utils/` | 文件名 → kebab-case，函数名 → camelCase |

## 6. 验收标准

- [ ] 改造模块命名符合上述规范
- [ ] `npm run build` 构建成功
- [ ] `npm run lint` 无命名相关报错
- [ ] 页面功能正常

## 7. 需求跟踪矩阵

| 需求编号 | 设计元素 | 测试用例 |
|----------|----------|----------|
| FR-001 | 文件命名规范 | TC-001 |
| FR-002 | 组件注册规范 | TC-002 |
| FR-003 | 变量命名规范 | TC-003 |
| FR-005 | store 规范 | TC-005 |
| FR-006 | api 规范 | TC-006 |
| FR-007 | ESLint 配置 | TC-007 |
