# 开发流程 SOP

## 1. 需求提出

```
/opsx:propose <功能名>
```

输入 kebab-case 功能名（如 `add-dark-mode`），系统自动生成：

| 产出物 | 说明 |
|--------|------|
| `proposal.md` | 为什么做、改什么、影响范围 |
| `design.md` | 技术方案、决策与取舍 |
| `specs/**/*.md` | 功能规格与验收场景 |
| `tasks.md` | 可执行的实施清单 |

产物路径：`openspec/changes/<功能名>/`

## 2. 方案评审

- 确认 `proposal.md` 的范围和优先级
- 审查 `design.md` 的技术决策是否合理
- 检查 `specs` 场景是否覆盖核心用例
- 如需调整，直接修改对应文件后重新确认

## 3. 任务实施

```
/opsx:apply
```

按 `tasks.md` 中的清单逐项执行，每完成一项勾选 `- [x]`。

### 实施规范

- 每个任务独立可验证，完成后自行测试
- 遵循项目现有代码风格和分层结构
- 不引入不必要的依赖或抽象
- 涉及 UI 变更需在浏览器中验证

## 4. 代码提交

```bash
git add <相关文件>
git commit -m "feat: <功能描述>"
```

### 提交规范

| 类型 | 用途 |
|------|------|
| `feat` | 新功能 |
| `fix` | 缺陷修复 |
| `docs` | 文档变更 |
| `refactor` | 重构（不改变行为） |
| `style` | 样式调整 |
| `test` | 测试相关 |

### 注意事项

- 按功能模块分批提交，避免一次性提交过多文件
- 不提交敏感信息（密钥、密码、`.env`）
- commit message 简洁明了，说明做了什么

## 5. 归档

```
/opsx:archive
```

实施完成后归档变更，清理 openspec 状态。

---

## 快速参考

```
/opsx:propose add-xxx    → 提出变更
/opsx:apply              → 开始实施
/opsx:archive            → 归档完成
/opsx:explore            → 探索模式（需求不清时使用）
```
