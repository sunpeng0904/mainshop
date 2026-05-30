---
name: unit-test
description: 当需要为代码生成单元测试、设计测试用例、分析测试覆盖率或编写JUnit5+Mockito测试时使用
---

## Overview

生成和执行单元测试，确保代码质量，覆盖正常场景、异常场景和边界场景，达到行覆盖率>=80%、分支覆盖率>=70%、方法覆盖率>=90%。

## When to Use

- 新增业务代码后需要补充单元测试
- 需要提升代码测试覆盖率
- 重构代码后需要验证功能不变
- 需要设计测试用例覆盖正常/异常/边界场景

## Core Pattern

测试结构遵循Given-When-Then模式：

```java
@Test
void test_[methodName]_[scenario]_[expected]() {
    // Given - 准备测试数据
    // When - 执行被测方法
    // Then - 验证结果
}
```

## Implementation

1. **测试设计** - 分析被测代码，设计测试用例（正常/异常/边界），确定测试数据
2. **测试生成** - 生成测试类（[类名]Test），生成测试方法（test_[方法名]_[场景]_[预期]），Mock外部依赖
3. **测试执行** - 执行单元测试，收集测试结果
4. **覆盖率分析** - 分析代码覆盖率，识别未覆盖代码，补充测试用例

## Templates

### 测试类结构
```java
@Slf4j
public class [ClassName]Test {
    @Mock
    private [Dependency] [dependency];
    @InjectMocks
    private [ClassName] [className];

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
}
```

## Quality Checklist

- [ ] 测试命名规范：test_[方法名]_[场景]_[预期结果]
- [ ] 测试数据准备完整（Given步骤）
- [ ] 外部依赖全部Mock（数据库、第三方服务）
- [ ] 行覆盖率>=80%，分支覆盖率>=70%，方法覆盖率>=90%
- [ ] 覆盖正常场景、异常场景（空值/异常）、边界场景（最小/最大/临界值）

## Common Mistakes

- 未Mock数据库或第三方服务导致测试不稳定
- 只测正常路径不测异常和边界
- 测试方法命名不规范导致难以定位失败用例
- 断言过于宽泛（如只assertNotNull不检查具体字段值）
