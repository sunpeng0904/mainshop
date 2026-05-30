# 完整开发流程 SOP

基于本项目的实际协作流程整理，覆盖从需求提出到代码上线的全链路。

---

## 阶段一：项目认知

在开始任何开发前，先了解项目全貌。

### 1.1 项目结构梳理

- 确认技术栈（后端 Spring Boot / 前端 Vue 3 + Element Plus）
- 了解目录分层（controller → service → mapper / views → components → store → api）
- 确认 Git 仓库根目录和分支状态

```bash
git rev-parse --show-toplevel   # 查看仓库根目录
git branch -a                    # 查看所有分支
git status -s                    # 查看当前变更
git log --oneline -10            # 查看最近提交
```

### 1.2 识别当前开发状态

- 有哪些未提交的功能代码
- 有哪些未追踪的新文件
- 当前在哪个分支（注意 detached HEAD 情况）

---

## 阶段二：需求提出

### 2.1 使用 OpenSpec 提出变更

```
/opsx:propose <功能名-kebab-case>
```

系统自动完成：
1. 探索项目结构和技术栈
2. 创建变更目录 `openspec/changes/<功能名>/`
3. 按依赖顺序生成 4 个产物

### 2.2 产物清单

| 产物 | 作用 | 依赖 |
|------|------|------|
| `proposal.md` | 为什么做、改什么、影响范围 | 无 |
| `design.md` | 技术方案、关键决策与取舍 | proposal |
| `specs/**/*.md` | 功能规格、验收场景（WHEN/THEN） | proposal |
| `tasks.md` | 可执行的实施清单（checkbox） | design + specs |

产物路径：`openspec/changes/<功能名>/`

### 2.3 探索模式（可选）

需求不清晰时，先进入探索模式讨论：

```
/opsx:explore
```

---

## 阶段三：方案评审

生成产物后，人工审查以下内容：

- [ ] `proposal.md` — 范围是否合理，有没有遗漏的影响面
- [ ] `design.md` — 技术决策是否可行，备选方案是否考虑充分
- [ ] `specs` — 验收场景是否覆盖核心用例和边界情况
- [ ] `tasks.md` — 任务粒度是否合适，依赖顺序是否正确

如需调整，直接编辑对应文件后继续。

---

## 阶段四：任务实施

### 4.1 启动实施

```
/opsx:apply
```

按 `tasks.md` 中的清单逐项执行，每完成一项勾选 `- [x]`。

### 4.2 实施规范

- 每个任务独立可验证，完成后自行测试
- 遵循项目现有代码风格和分层结构
- 不引入不必要的依赖或抽象
- 涉及 UI 变更需在浏览器中验证
- 前端改动后启动 dev server 确认效果

### 4.3 项目简介（可选）

向团队或 AI 助手介绍项目时，可要求生成简介：

```
简单描述下当前项目，100字左右
```

---

## 阶段五：测试验证

每个功能实施完成后、提交代码前，必须经过测试验证。

### 5.1 后端接口测试

#### 启动后端服务

```bash
cd backend
mvn spring-boot:run
```

#### 接口手动验证（curl / Postman）

```bash
# 示例：GET 请求
curl -X GET http://localhost:8080/api/products -H "Authorization: Bearer <token>"

# 示例：POST 请求
curl -X POST http://localhost:8080/api/share \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"content":"测试内容","images":[]}'
```

#### 后端测试检查清单

- [ ] 接口返回 200，响应结构符合 VO 定义
- [ ] 参数校验生效（缺必填字段返回 400）
- [ ] 未登录访问需认证接口返回 401
- [ ] 无权限操作返回 403
- [ ] 数据库写入正确（查表确认）
- [ ] 异常场景有合理错误提示

### 5.2 前端功能测试

#### 启动前端服务

```bash
cd frontend
npm run serve
```

#### 前端测试检查清单

**通用检查（每个页面必做）：**
- [ ] 页面正常加载，无控制台报错
- [ ] 路由跳转正常，浏览器前进/后退不出错
- [ ] 接口请求正常（Network 面板无红色错误）
- [ ] 加载状态和空状态有合理展示

**表单交互：**
- [ ] 必填项为空时显示校验提示
- [ ] 提交成功后有反馈（提示/跳转）
- [ ] 提交失败时有错误提示

**列表/分页：**
- [ ] 数据正常渲染
- [ ] 翻页后数据刷新
- [ ] 空列表展示空状态组件

**弹窗/抽屉：**
- [ ] 打开/关闭动画正常
- [ ] 遮罩层点击可关闭（如设计允许）
- [ ] 弹窗内表单重置

### 5.3 跨功能回归测试

改动涉及共享模块时，需验证关联功能不受影响：

| 改动区域 | 需回归的功能 |
|----------|-------------|
| 用户认证模块 | 登录、注册、Token 刷新、权限控制 |
| 商品模块 | 商品列表、详情、分类、搜索 |
| 购物车模块 | 加购、数量修改、删除、结算 |
| 订单模块 | 下单、支付、订单列表、订单详情 |
| 分享模块 | 发布、点赞、评论、收藏、转发 |
| 后台管理 | 各管理页面 CRUD、分页、筛选 |

### 5.4 浏览器兼容性

- 主测 Chrome 最新版
- 如有需要，抽检 Firefox / Safari / Edge
- 移动端需在真机或 DevTools 移动模式下验证响应式布局

### 5.5 自动化测试建设（推荐逐步引入）

当前项目暂无自动化测试基础设施，建议按优先级逐步建设：

**第一步：后端单元测试**
```xml
<!-- pom.xml 已包含 spring-boot-starter-test -->
<!-- 创建 src/test/java/ 目录，为 Service 层编写单元测试 -->
```

```java
@SpringBootTest
class ShareServiceTest {
    @Autowired
    private ShareService shareService;

    @Test
    void createShare_withValidData_returnsSuccess() {
        // given / when / then
    }
}
```

**第二步：前端组件测试**
```bash
cd frontend
npm install -D @vue/test-utils jest @vue/vue3-jest
```

```javascript
// src/components/__tests__/ShareCard.test.js
import { mount } from '@vue/test-utils'
import ShareCard from '../ShareCard.vue'

describe('ShareCard', () => {
  it('renders content correctly', () => {
    const wrapper = mount(ShareCard, { props: { share: mockData } })
    expect(wrapper.text()).toContain('测试内容')
  })
})
```

**第三步：API 集成测试**

使用 Spring Boot Test + MockMvc 测试 Controller 层：

```java
@SpringBootTest
@AutoConfigureMockMvc
class ShareControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void createShare_returns200() throws Exception {
        mockMvc.perform(post("/api/share")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"content\":\"test\"}"))
                .andExpect(status().isOk());
    }
}
```

---

## 阶段六：代码提交

### 6.1 提交前检查

```bash
git status -s          # 查看所有变更
git diff --stat        # 查看改动统计
```

### 6.2 分模块提交

按功能模块分批提交，避免一次性提交过多文件：

```bash
# 示例：社交分享功能
git add backend/src/main/java/com/online/mall/controller/share/ \
        backend/src/main/java/com/online/mall/service/share/ \
        backend/src/main/java/com/online/mall/mapper/Share*.java \
        frontend/src/views/share/ \
        frontend/src/components/Share*.vue
git commit -m "feat: 社交分享功能完整开发"

# 文档
git add backend/docs/
git commit -m "docs: 添加设计文档与开发SOP"

# 配置与资源
git add backend/openspec/ backend/.claude/
git commit -m "chore: 添加openspec配置与工具链配置"
```

### 6.3 Commit 规范

| 类型 | 用途 | 示例 |
|------|------|------|
| `feat` | 新功能 | `feat: 社交分享功能完整开发` |
| `fix` | 缺陷修复 | `fix: 修复图片保存路径问题` |
| `docs` | 文档变更 | `docs: 添加设计文档与SOP` |
| `refactor` | 重构 | `refactor: 提取公共校验逻辑` |
| `style` | 样式调整 | `style: 暗色模式适配` |
| `chore` | 构建/配置 | `chore: 添加openspec配置` |
| `test` | 测试 | `test: 添加分享接口单元测试` |

### 6.4 注意事项

- 不提交敏感信息（密钥、密码、`.env`）
- `.claude/settings.local.json` 等本地配置按需提交
- commit message 用中文，简洁说明做了什么

---

## 阶段七：推送与归档

### 7.1 推送到远程

```bash
git push
```

如果推送失败（网络问题），检查代理配置：

```bash
git config --global http.proxy http://127.0.0.1:<端口>
git config --global https.proxy http://127.0.0.1:<端口>
```

### 7.2 归档变更

```bash
/opsx:archive
```

归档后 `openspec/changes/<功能名>/` 标记为已完成。

---

## 快速参考

```
/opsx:propose add-xxx    → 提出变更（自动生成 proposal + design + specs + tasks）
/opsx:apply              → 开始实施（按 tasks 逐项执行）
/opsx:archive            → 归档完成
/opsx:explore            → 探索模式（需求不清时使用）
```

## 完整流程图

```
需求提出                    方案生成                    评审
  │                         │                         │
  ▼                         ▼                         ▼
/opsx:propose ──→ proposal ──→ design + specs ──→ tasks.md
                                                    │
                                                    ▼
                                              方案评审通过？
                                              ┌─ 否 → 修改产物，重新评审
                                              └─ 是 ↓
                                                    │
实施                      测试                        提交
  │                       │                           │
  ▼                       ▼                           ▼
/opsx:apply ──→ 逐项完成 tasks ──→ 后端接口 + 前端功能 + 回归测试
                                       │
                                       ▼
                                  测试通过？
                                  ┌─ 否 → 修复问题，重新测试
                                  └─ 是 ↓
                                       │
                                       ▼
                              git add + commit（分模块）
                                       │
                                       ▼
                                 git push ──→ /opsx:archive
```
