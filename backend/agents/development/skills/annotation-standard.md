---
name: annotation-standard
description: 当需要检查或生成符合中证体系的Java注释（JavaDoc）和注解（Spring/MyBatis/Lombok/校验）时使用
---

## Overview

确保代码注释和注解符合中证体系要求，所有类、public方法、常量必须有完整JavaDoc，注解使用规范统一。

## When to Use

- 代码缺少类注释、方法注释或字段注释
- 需要统一注解风格（Spring/MyBatis/Lombok）
- 需要添加参数校验注解（@NotNull/@NotBlank/@Size等）
- 代码评审中发现注释不规范或注解缺失

## Core Pattern

| 注解类型 | 常用注解 |
|----------|----------|
| Spring | @RestController, @Service, @Repository, @Autowired |
| MyBatis | @Mapper, @Select, @Insert, @Update, @Delete |
| Lombok | @Data, @Builder, @Slf4j, @AllArgsConstructor |
| 校验 | @NotNull, @NotBlank, @Size, @Email |

## Implementation

1. **注解检查** - 检查类注解、方法注解、字段注解是否符合规范
2. **注释检查** - 检查类/方法/字段/逻辑注释是否完整
3. **注解生成** - 补充缺失注解，优化现有注解，统一风格
4. **注释生成** - 补充缺失注释，优化现有注释，统一风格

## Quick Reference

| 元素 | 必须包含 |
|------|----------|
| 类注释 | 功能描述、@author、@since、@date、@see |
| 方法注释 | 功能描述、@param、@return、@throws、@since |
| 字段注释 | 字段描述（单行注释） |
| 常量 | 字段说明 |

## Quality Checklist

- [ ] 所有类有JavaDoc（含@author/@since/@date）
- [ ] 所有public方法有JavaDoc（含@param/@return/@throws）
- [ ] 常量有字段注释
- [ ] 关键业务逻辑有行注释
- [ ] 注释使用中文，简洁明了

## Common Mistakes

- 方法注释缺少@return或@throws说明
- 使用@Data同时手动写getter/setter
- 注释与代码不同步（修改代码未更新注释）
- @NotNull和@NotBlank混用（字符串应使用@NotBlank）
