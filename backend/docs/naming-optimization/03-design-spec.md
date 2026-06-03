# 前端命名规范设计文档

## 1. 设计概述

### 1.1 设计目标
制定 online-mall 前端项目的统一命名规范，并对关键模块进行改造。

### 1.2 设计原则
- 遵循 Vue 官方推荐风格
- 保持一致性优先
- 渐进式改造，不影响现有功能

## 2. 命名规范标准

### 2.1 文件命名

| 文件类型 | 规范 | 示例 |
|----------|------|------|
| Vue 组件文件 | kebab-case | `product-card.vue` |
| JS/TS 文件 | kebab-case | `api-service.js` |
| 样式文件 | kebab-case | `global-styles.scss` |
| 目录名 | kebab-case | `user-center/` |

### 2.2 代码命名

| 元素 | 规范 | 示例 |
|------|------|------|
| 组件注册名 | PascalCase | `ProductCard` |
| 组件实例 | PascalCase | `<ProductCard />` |
| 变量 | camelCase | `productList` |
| 函数 | camelCase | `getProductList` |
| 常量 | UPPER_SNAKE_CASE | `API_BASE_URL` |
| CSS 类名 | kebab-case | `product-card__title` |
| Props | camelCase | `productName` |
| Events | kebab-case | `@product-click` |

### 2.3 特殊场景

| 场景 | 规范 | 示例 |
|------|------|------|
| Store 模块 | camelCase 变量名 | `useUserStore` |
| API 函数 | camelCase，动词开头 | `getProductList`、`createOrder` |
| Router 路径 | kebab-case | `/product-detail/:id` |
| 工具函数 | camelCase，动词开头 | `formatDate`、`debounce` |

## 3. 改造方案

### 3.1 改造模块清单

| 模块 | 路径 | 改造内容 | 影响范围 |
|------|------|----------|----------|
| 组件 | `src/components/` | 文件名 kebab-case | 引用路径更新 |
| Store | `src/store/` | 变量名规范化 | 模块内部 |
| API | `src/api/` | 文件名 kebab-case，函数名 camelCase | 调用处更新 |
| Utils | `src/utils/` | 文件名 kebab-case，函数名 camelCase | 调用处更新 |

### 3.2 改造策略

**第一步：ESLint 规则配置**
- 添加 `vue/multi-word-component-names` 规则
- 添加 `id-match` 规则约束命名风格
- 配置 `.eslintrc.js` 文件

**第二步：组件模块改造**
- 重命名组件文件为 kebab-case
- 更新所有引用路径
- 验证组件注册名

**第三步：API 模块改造**
- 重命名 API 文件为 kebab-case
- 统一函数命名为 camelCase
- 更新所有调用处

**第四步：Store/Utils 模块改造**
- 规范化变量和函数命名
- 更新所有引用

**第五步：构建验证**
- 运行 `npm run build` 验证构建
- 运行 `npm run lint` 验证规范
- 手动验证关键页面

## 4. ESLint 配置设计

```javascript
// .eslintrc.js 命名相关规则
module.exports = {
  rules: {
    // 组件名必须是多个单词
    'vue/multi-word-component-names': 'off', // 现有项目暂关闭
    
    // 变量命名规范
    'id-match': ['error', '^[a-z][a-zA-Z0-9]*$|^[A-Z][a-zA-Z0-9]*$|^[A-Z][A-Z0-9_]*$', {
      properties: false,
      onlyDeclarations: true
    }],
    
    // Vue 组件名 PascalCase
    'vue/component-name-in-template': ['error', {
      registeredComponentsOnly: false
    }]
  }
}
```

## 5. 风险与应对

| 风险 | 可能性 | 影响 | 应对措施 |
|------|--------|------|----------|
| 引用路径更新遗漏 | 中 | 高 | 构建验证 + 全量搜索 |
| ESLint 规则误报 | 低 | 中 | 先在关键模块验证 |
| 文件重命名冲突 | 低 | 低 | 使用 git mv 操作 |

## 6. 验收标准

- [ ] 组件文件名符合 kebab-case
- [ ] 组件注册名符合 PascalCase
- [ ] 变量/函数名符合 camelCase
- [ ] 常量符合 UPPER_SNAKE_CASE
- [ ] `npm run build` 构建成功
- [ ] `npm run lint` 无命名相关报错
