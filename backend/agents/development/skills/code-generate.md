---
name: code-generate
description: 当需要根据API设计文档、数据模型和架构设计生成符合规范的代码框架、实体类、Mapper、Service、Controller时使用
---

## Overview

根据设计文档生成符合中证开发规范的代码，覆盖从项目结构到各层代码的完整生成流程。

## When to Use

- 收到API设计文档后需要生成代码
- 需要创建实体类、Mapper、Service、Controller等分层代码
- 需要生成项目结构和配置文件
- 新增业务模块需要搭建代码框架

## Core Pattern

| 层次 | 生成内容 | 关键注解 |
|------|----------|----------|
| Entity | 实体类 | @Data, @TableName, @TableId |
| Mapper | 数据访问接口 | @Mapper, extends BaseMapper |
| Service | 业务逻辑层 | @Service, @Slf4j |
| Controller | 控制器层 | @RestController, @RequestMapping |

## Implementation

1. **框架生成** - 生成项目目录结构、配置文件（数据库/缓存/消息队列）、构建脚本
2. **实体类生成** - 根据数据模型生成实体，包含主键、字段映射、创建/更新时间
3. **Mapper生成** - 继承BaseMapper，添加自定义查询方法
4. **Service生成** - 实现业务接口，注入Mapper，编写业务逻辑
5. **Controller生成** - RESTful接口，参数校验，调用Service并返回Result
6. **质量检查** - 检查代码规范、命名规范、注释完整性

## Templates

### 实体类
```java
@Data
@TableName("t_[表名]")
public class [EntityName] {
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("[字段名]")
    private [类型] [字段名];
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
```

### Mapper
```java
@Mapper
public interface [EntityName]Mapper extends BaseMapper<[EntityName]> {
    List<[EntityName]> selectByCondition([EntityName] condition);
}
```

### Service
```java
@Service
@Slf4j
public class [EntityName]ServiceImpl implements [EntityName]Service {
    @Autowired
    private [EntityName]Mapper [entityName]Mapper;
}
```

### Controller
```java
@RestController
@RequestMapping("/api/v1/[资源]")
@Slf4j
public class [EntityName]Controller {
    @Autowired
    private [EntityName]Service [entityName]Service;

    @GetMapping("/{id}")
    public Result<[EntityName]> getById(@PathVariable Long id) {
        return Result.success([entityName]Service.getById(id));
    }
}
```

## Quality Checklist

- [ ] 代码符合Controller/Service/Manager/DAO分层
- [ ] 命名符合中证规范（大驼峰类名、小驼峰方法名）
- [ ] 注释完整（类/方法/字段均有JavaDoc）
- [ ] 异常处理完善，不吞掉异常

## Common Mistakes

- 跳过Manager层直接在Service中调用外部服务
- 实体类缺少createTime/updateTime自动填充
- Controller层包含业务逻辑
- Mapper未继承BaseMapper导致基础方法缺失
