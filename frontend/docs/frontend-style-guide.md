# 前端样式规范指南

## 技术栈

- **框架**: Vue 3 (Composition API, `<script setup>`)
- **UI 组件库**: Element Plus 2.x
- **样式**: SCSS，组件内使用 scoped
- **图标**: @element-plus/icons-vue
- **状态管理**: Vuex 4 + Pinia 2

## 颜色规范

### 功能色

| 变量 | 色值 | 用途 |
|------|------|------|
| `$primary-color` | `#409EFF` | 按钮、链接、激活态 |
| `$success-color` | `#67C23A` | 成功标签、正向指示 |
| `$warning-color` | `#E6A23C` | 警告标签、注意状态 |
| `$danger-color` | `#F56C6C` | 价格、删除操作、错误状态 |
| `$info-color` | `#909399` | 禁用状态、次要信息 |

### 文字色

| 变量 | 色值 | 用途 |
|------|------|------|
| `$text-primary` | `#303133` | 标题、主要内容 |
| `$text-regular` | `#606266` | 正文、标签 |
| `$text-secondary` | `#909399` | 说明文字、时间戳、弱化文字 |
| `$text-placeholder` | `#C0C4CC` | 占位文字、图标 |

### 背景与边框色

| 变量 | 色值 | 用途 |
|------|------|------|
| `$bg-color` | `#F5F7FA` | 页面背景 |
| `$border-color-base` | `#DCDFE6` | 默认边框 |
| `$border-color-light` | `#E4E7ED` | 细微分隔线 |

## 间距系统

| 变量 | 值 | 用途 |
|------|------|------|
| `$spacing-xs` | `4px` | 紧凑间距 |
| `$spacing-sm` | `8px` | 小间距 |
| `$spacing-md` | `16px` | 卡片内边距、区块间距 |
| `$spacing-lg` | `24px` | 大区块间距 |
| `$spacing-xl` | `32px` | 页面级间距 |

工具类：`.mt-10` `.mt-20` `.mb-10` `.mb-20` `.ml-10` `.mr-10` `.p-10` `.p-20`

## 圆角

| 变量 | 值 | 用途 |
|------|------|------|
| `$border-radius-small` | `2px` | 标签、小元素 |
| `$border-radius-base` | `4px` | 输入框、按钮 |
| `$border-radius-large` | `8px` | 卡片、容器 |

## 阴影

| 变量 | 值 | 用途 |
|------|------|------|
| `$box-shadow-light` | `0 2px 8px rgba(0,0,0,0.06)` | 卡片、轻微抬升 |
| `$box-shadow-base` | `0 2px 12px rgba(0,0,0,0.1)` | 悬停态、弹窗 |

## 组件样式模式

### 卡片 (Card)

```scss
.card {
  background: #fff;
  border-radius: $border-radius-large; // 8px
  box-shadow: $box-shadow-light;
  padding: $spacing-md; // 16px
}
```

### 筛选栏 (Filter Bar) — 管理后台页面使用

```html
<div class="filter-card card">
  <el-form :inline="true" :model="filterForm" class="filter-form">
    <!-- 表单项 -->
  </el-form>
</div>
```

```scss
.filter-form {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;

  :deep(.el-form-item) {
    margin-bottom: 0;
  }
}
```

### 管理后台页面布局

```html
<div class="admin-[功能名]-list">
  <!-- 筛选区 -->
  <div class="filter-card card">...</div>

  <!-- 操作按钮栏 -->
  <div class="action-bar">
    <el-button type="primary">新增</el-button>
  </div>

  <!-- 表格区 -->
  <div class="table-card card">
    <el-table stripe>...</el-table>
    <div class="pagination-wrapper">
      <el-pagination ... />
    </div>
  </div>
</div>
```

### 商品网格 (Product Grid)

```scss
.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
}
```

### 价格展示

```scss
.price { color: #f56c6c; font-weight: bold; }
.original-price {
  margin-left: 8px;
  color: #909399;
  text-decoration: line-through;
  font-size: 12px;
}
```

### 分页容器

```scss
.pagination-wrapper {
  display: flex;
  justify-content: flex-end; // 后台页面右对齐
  // justify-content: center; // 前台页面居中
  margin-top: 16px;
}
```

## 全局布局

| 区域 | 规格 |
|------|------|
| 顶部导航 | 固定定位，高度 `60px`，白色背景，`box-shadow: 0 1px 4px rgba(0,0,0,0.08)` |
| 侧边栏 | 宽度 `200px`，白色背景，仅管理后台显示 |
| 内容区 | 内边距 `10px`，背景色 `#f5f7fa` |
| 前台最大宽度 | `1200px` 居中 |

## 工具类

### Flex 布局

`.flex` `.flex-center` `.flex-between` `.flex-around` `.flex-column` `.flex-wrap`

### 文字对齐

`.text-center` `.text-right` `.text-left`

### 文字颜色

`.text-primary` `.text-success` `.text-warning` `.text-danger` `.text-muted`

## Element Plus 覆写

- 使用 `:deep()` 穿透 scoped 样式
- 筛选栏表单项：`margin-bottom: 0`
- Tabs：隐藏下划线 `::after { display: none }`

## 过渡动画

页面切换使用淡入淡出：

```scss
.fade-enter-active, .fade-leave-active { transition: opacity 0.2s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
```

卡片悬停效果：`transform: translateY(-4px)` 配合增强阴影。

## 滚动条

自定义细滚动条：宽度 `6px`，轨道透明，滑块 `rgba(0,0,0,0.2)`。

## 响应式

- `.hidden-mobile`：768px 以下隐藏
- `.hidden-desktop`：769px 以上隐藏
