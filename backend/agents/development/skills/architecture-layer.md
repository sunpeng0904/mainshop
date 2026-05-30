---
name: architecture-layer
description: 当需要检查代码分层是否正确、依赖方向是否合规、是否存在循环依赖或职责混乱时使用
---

## Overview

确保代码分层清晰、职责明确、依赖方向正确，遵循Controller → Service → Manager → DAO的分层架构。

## When to Use

- 新增模块需要确定代码归属层次
- 代码评审中发现跨层调用或循环依赖
- 需要检查类是否放在正确的包路径下
- 重构时需要调整代码分层

## Core Pattern

```
Controller（控制层）→ Service（业务层）→ Manager（管理层）→ DAO（数据访问层）→ Model（模型层）
```

依赖规则：上层可依赖下层，下层不可依赖上层，禁止循环依赖。

## Implementation

1. **分层检查** - 检查包结构是否符合controller/service/manager/dao/model分层，检查类归属是否正确
2. **依赖检查** - 检查层间依赖方向，识别循环依赖，检查跨层调用
3. **职责检查** - 检查类职责是否单一，方法职责是否明确
4. **合规报告** - 生成分层合规报告，列出问题清单，提供重构建议

## Quick Reference

| 层次 | 包路径 | 职责 |
|------|--------|------|
| Controller | controller/ | 接收请求、参数校验、返回响应 |
| Service | service/ + service/impl/ | 业务逻辑处理、事务管理 |
| Manager | manager/ | 可复用逻辑、第三方封装、缓存 |
| DAO | dao/ | 数据库操作、SQL编写 |
| Model | model/entity, dto, vo, bo | 数据模型定义 |

## Common Mistakes

- Controller层包含业务逻辑
- Service层直接调用外部HTTP接口（应通过Manager封装）
- 下层依赖上层（如DAO层注入Service）
- 循环依赖（A依赖B、B依赖A）
- entity/dto/vo/bo混用，未按场景区分模型
