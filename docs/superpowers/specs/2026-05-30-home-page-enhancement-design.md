# 首页视觉增强设计文档

**日期**: 2026-05-30
**范围**: `frontend/src/views/home/index.vue` 及相关文件
**技术栈**: Vue 3 + Element Plus + SCSS + tsParticles

## 目标

将首页从基础静态页面升级为具有酷炫视觉效果的沉浸式体验，包含粒子背景、3D 卡片悬浮、视差滚动、霓虹灯效果、滚动入场动画和数字滚动计数器。

## 视觉效果清单

### 1. tsParticles 粒子背景

- 80-120 个漂浮光点，带微弱连线
- 颜色跟随页面主色调（蓝紫渐变系）
- 鼠标附近粒子被轻微吸引，产生交互感
- `fps_limit: 60`，移动端减半粒子数量
- 品牌故事区域叠加星空粒子层

### 2. 动态渐变背景

- 全局背景使用 `@keyframes hue-rotate` 色相旋转
- 每个 section 有独立的微妙渐变底色

### 3. 3D 卡片悬浮效果

- **统计卡片**: 鼠标跟随 3D 倾斜 + 光泽高光跟随
- **分类卡片**: 3D 倾斜 + 图标浮出 + 底部光晕扩散
- **商品卡片**: 3D 倾斜 + 图片放大 + 阴影加深
- **新品卡片**: 3D 倾斜 + 价格闪烁 + 按钮弹出
- 使用 CSS `perspective` + JS `mousemove` 计算倾斜角度
- `mouseleave` 时平滑归零（`transition: transform 0.6s`）
- 移动端禁用（触摸设备无 hover）

### 4. 滚动入场动画

- Intersection Observer 监听各 section
- 统计数字：数字从 0 滚动到目标值
- 分类卡片：依次从下方弹入（stagger delay）
- 商品卡片：依次从左/右滑入
- 标题：渐显效果
- 每个元素只触发一次动画

### 5. 视差滚动 (Parallax)

- 轮播图区域：背景与内容不同速度滚动
- 浮动装饰元素：多层视差
- 品牌故事区域：背景星空视差
- 移动端禁用

### 6. 霓虹灯效果

- Banner 标题：文字发光脉冲动画
- 品牌故事标题：霓虹灯管效果
- 热门商品标题 🔥 图标：火焰跳动动画

### 7. 页面加载动画

- 各 section 按顺序编排入场（cascade）

### 8. 磁性按钮

- CTA 按钮鼠标靠近时产生磁性吸附效果
- 按钮内部光点跟随鼠标

## 技术架构

### 文件结构

```
frontend/src/
├── views/home/
│   ├── index.vue              # 主页面（重写模板 + script）
│   └── composables/
│       ├── useParticles.ts    # tsParticles 配置与初始化
│       ├── use3DTilt.ts       # 3D 卡片倾斜逻辑（复用）
│       ├── useScrollAnim.ts   # 滚动入场动画（Intersection Observer）
│       └── useCounter.ts      # 数字滚动计数器
├── styles/
│   └── home-animations.scss   # 动画关键帧 & mixin
```

### 新增依赖

```json
{
  "@tsparticles/vue3": "^3.0.0",
  "@tsparticles/slim": "^3.0.0"
}
```

tsParticles slim 版本（~30KB gzip），包含基础粒子形状和连线功能。

### 3D 倾斜实现原理

```typescript
// use3DTilt.ts 核心逻辑
const handleMouseMove = (e: MouseEvent, el: HTMLElement) => {
  const rect = el.getBoundingClientRect()
  const x = (e.clientX - rect.left) / rect.width
  const y = (e.clientY - rect.top) / rect.height
  const rotateX = (y - 0.5) * -20
  const rotateY = (x - 0.5) * 20
  // 应用 transform + 光泽高光位置
}
```

### 滚动动画实现

```typescript
// useScrollAnim.ts
const observer = new IntersectionObserver((entries) => {
  entries.forEach(entry => {
    if (entry.isIntersecting) {
      entry.target.classList.add('animate-in')
      observer.unobserve(entry.target)
    }
  })
}, { threshold: 0.1 })
```

### 性能策略

- 粒子效果移动端减半数量，低端设备降级为纯 CSS
- 3D 倾斜使用 `requestAnimationFrame` 节流
- 滚动动画用 Intersection Observer 而非 scroll 事件
- 所有动画使用 `transform` 和 `opacity`（GPU 加速）

## 视觉效果与区域映射

| 区域 | 粒子 | 3D 倾斜 | 滚动动画 | 视差 | 霓虹 | 数字滚动 |
|------|------|---------|----------|------|------|----------|
| 全局背景 | ✅ | - | - | - | - | - |
| Banner | - | - | ✅ | ✅ | ✅ | - |
| 统计区 | - | ✅ | ✅ | - | - | ✅ |
| 分类区 | - | ✅ | ✅ | - | - | - |
| 热门商品 | - | ✅ | ✅ | - | - | - |
| 新品推荐 | - | ✅ | ✅ | - | - | - |
| 品牌故事 | ✅(星空) | - | ✅ | ✅ | ✅ | - |

## 移动端适配

- 粒子数量减半
- 禁用 3D 倾斜（无 hover）
- 禁用视差滚动（触摸设备体验差）
- 保留滚动入场动画（核心体验）
- 霓虹效果保留但减弱亮度

## 改动文件清单

| 文件 | 操作 | 说明 |
|------|------|------|
| `views/home/index.vue` | 重写 | 模板增加动画类名，script 引入 composables |
| `views/home/composables/useParticles.ts` | 新建 | tsParticles 配置 |
| `views/home/composables/use3DTilt.ts` | 新建 | 3D 倾斜复用逻辑 |
| `views/home/composables/useScrollAnim.ts` | 新建 | 滚动动画 observer |
| `views/home/composables/useCounter.ts` | 新建 | 数字滚动计数器 |
| `styles/home-animations.scss` | 新建 | 全局动画关键帧 |
| `package.json` | 修改 | 添加 tsParticles 依赖 |
