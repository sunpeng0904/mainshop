# AI Scaffold Framework Design Specification

## 1. Overview

### 1.1 Purpose
Design a modular, reusable AI scaffold framework for managing software development lifecycle, with specific support for China Securities Regulatory Commission (CSRC) compliance requirements.

### 1.2 Scope
- Generic reusable framework
- Project-specific configuration for online-mall project
- Independent executable scripts (Python)

### 1.3 Key Requirements
- 6 main Agents + 6 Review Agents
- Pass/Fail review mechanism with human confirmation
- 100-point scoring system
- CSRC compliance skills
- Project-specific architecture and technology stack support

## 2. Architecture

### 2.1 Layer Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    AI Scaffold Framework                    │
├─────────────────────────────────────────────────────────────┤
│                      Orchestrator Layer                     │
│                    (Workflow Orchestration)                 │
├─────────────────────────────────────────────────────────────┤
│       │          │          │          │          │         │
│  ┌────▼────┐ ┌────▼────┐ ┌────▼────┐ ┌────▼────┐ ┌────▼────┐
│  │Project  │ │Require- │ │ Design  │ │Develop- │ │Testing  │
│  │Manager  │ │ment     │ │ Agent   │ │ment     │ │ Agent   │
│  │Agent    │ │Agent    │ │         │ │Agent    │ │         │
│  └────┬────┘ └────┬────┘ └────┬────┘ └────┬────┘ └────┬────┘
│       │          │          │          │          │         │
│  ┌────▼────┐ ┌────▼────┐ ┌────▼────┐ ┌────▼────┐ ┌────▼────┐
│  │Deployment│ │ Review  │ │ Review  │ │ Review  │ │ Review  │
│  │Agent    │ │Agent    │ │Agent    │ │Agent    │ │Agent    │
│  └─────────┘ └─────────┘ └─────────┘ └─────────┘ └─────────┘
├─────────────────────────────────────────────────────────────┤
│                       Skill Layer                          │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐       │
│  │Base      │ │Compliance│ │ Security │ │Performance│      │
│  │Skills    │ │Skills    │ │ Skills   │ │ Skills   │       │
│  └──────────┘ └──────────┘ └──────────┘ └──────────┘       │
├─────────────────────────────────────────────────────────────┤
│                      Config Layer                          │
│  ┌─────────────────────────────────────────────────────┐   │
│  │  Project Config │ Agent Config │ Skill Config      │   │
│  └─────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

### 2.2 Core Components

1. **Orchestrator Layer**: Manages workflow, coordinates Agent execution order
2. **Agent Layer**: 6 main Agents + 6 Review Agents
3. **Skill Layer**: Callable skill library for Agents
4. **Config Layer**: Project-specific configuration

## 3. Agent Layer Design

### 3.1 Main Agents

| Agent | Responsibility | Input | Output |
|-------|----------------|-------|--------|
| **ProjectManager** | Project management, resource coordination | Requirements, project plan | Project status report |
| **Requirement** | Requirements analysis, documentation | User requirements, business rules | Requirements specification |
| **Design** | System design, architecture design | Requirements specification | Design documents, architecture diagrams |
| **Development** | Code implementation, development standards | Design documents | Code, unit tests |
| **Testing** | Test planning, test execution | Code, requirements | Test reports |
| **Deployment** | Deployment, operations | Test reports, code | Deployment records |

### 3.2 Review Agents

Each main Agent has a corresponding **Review Agent** responsible for:
- Checking deliverables against standards
- Generating pass/fail status
- Recording review comments

### 3.3 Review Flow

```
Agent Output → Review Agent Check → Generate Status (Pass/Fail) → Human Confirmation → Next Stage
```

### 3.4 Scoring Mechanism

- 100-point scoring system
- Below 60: Fail
- 60-79: Conditional pass (requires modification)
- 80 and above: Pass

## 4. Skill Layer Design

### 4.1 Skill Categories

**1. Base Skills**
- Code generation
- Document generation
- Code review
- Test case generation

**2. Compliance Skills (CSRC)**
- Term definition management
- Standard compliance check
- Compliance report generation

**3. Security Skills**
- Vulnerability scanning
- Permission handling
- Data encryption

**4. Performance Skills**
- Performance testing
- Code optimization suggestions
- Resource monitoring

### 4.2 Project-Specific Configuration (online-mall)

- **Architecture layers**: Controller → Service → Mapper → Entity
- **Technology stack**: Spring Boot + MyBatis + Vue.js + Element Plus
- **Dictionary items**: Product categories, order status, payment methods, etc.
- **Permission control**: RBAC model (User-Role-Permission)

### 4.3 CSRC Compliance Requirements

- Code comment standards
- Annotation usage standards
- Log recording standards
- Exception handling standards
- Secure coding standards

## 5. Review Engine Design

### 5.1 Review Flow

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│  Agent      │     │  Review     │     │  Score      │     │  Human      │
│  Output     │────▶│  Agent Check│────▶│  System     │────▶│  Confirm    │
└─────────────┘     └─────────────┘     └─────────────┘     └─────────────┘
                           │                   │                   │
                           ▼                   ▼                   ▼
                    Check Standards      Generate Report     Pass/Reject
                    Generate Comments    (100-point)         Record Decision
```

### 5.2 Review Check Items

**General Check Items:**
- Code standards compliance
- Documentation completeness
- Test coverage
- Security checks

**CSRC Compliance Check Items:**
- Comment standards
- Annotation usage
- Log recording
- Exception handling
- Permission control

### 5.3 Scoring Rules

| Score Range | Status | Handling |
|-------------|--------|----------|
| 80-100 | Pass | Auto-proceed to next stage |
| 60-79 | Conditional Pass | Modify and re-review |
| 0-59 | Fail | Re-develop |

### 5.4 Human Confirmation Mechanism

- After Review Agent generates report, wait for human confirmation
- Human can confirm via CLI command: `python scaffold.py confirm --stage design --status pass`
- After confirmation, auto-proceed to next stage

## 6. File Structure

```
ai-scaffold/
├── README.md
├── setup.py
├── requirements.txt
├── scaffold.py
│
├── core/
│   ├── __init__.py
│   ├── orchestrator.py
│   ├── review_engine.py
│   ├── score_manager.py
│   └── config_manager.py
│
├── agents/
│   ├── __init__.py
│   ├── base_agent.py
│   ├── project_manager/
│   │   ├── __init__.py
│   │   ├── agent.py
│   │   └── reviewer.py
│   ├── requirement/
│   │   ├── __init__.py
│   │   ├── agent.py
│   │   └── reviewer.py
│   ├── design/
│   │   ├── __init__.py
│   │   ├── agent.py
│   │   └── reviewer.py
│   ├── development/
│   │   ├── __init__.py
│   │   ├── agent.py
│   │   └── reviewer.py
│   ├── testing/
│   │   ├── __init__.py
│   │   ├── agent.py
│   │   └── reviewer.py
│   └── deployment/
│       ├── __init__.py
│       ├── agent.py
│       └── reviewer.py
│
├── skills/
│   ├── __init__.py
│   ├── base_skills/
│   │   ├── code_generator.py
│   │   ├── doc_generator.py
│   │   ├── code_reviewer.py
│   │   └── test_generator.py
│   ├── compliance/
│   │   ├── term_definition.py
│   │   ├── standard_checker.py
│   │   └── compliance_report.py
│   ├── security/
│   │   ├── vulnerability_scanner.py
│   │   ├── permission_handler.py
│   │   └── encryption.py
│   └── performance/
│       ├── performance_tester.py
│       ├── code_optimizer.py
│       └── resource_monitor.py
│
├── configs/
│   ├── default.yaml
│   └── projects/
│       └── online-mall/
│           ├── project.yaml
│           ├── agents.yaml
│           ├── skills.yaml
│           └── compliance/
│               └── csrc_rules.yaml
│
├── templates/
│   ├── documents/
│   │   ├── requirement_spec.md
│   │   ├── design_doc.md
│   │   └── test_report.md
│   └── code/
│       ├── controller.java
│       ├── service.java
│       └── mapper.java
│
└── tests/
    ├── test_orchestrator.py
    ├── test_agents.py
    └── test_skills.py
```

## 7. Core Class Design

### 7.1 BaseAgent

```python
class BaseAgent:
    def __init__(self, config):
        self.config = config
        self.skills = []
    
    def execute(self, input_data):
        """Execute agent task"""
        pass
    
    def get_output(self):
        """Get deliverables"""
        pass
```

### 7.2 ReviewAgent

```python
class ReviewAgent(BaseAgent):
    def review(self, agent_output):
        """Review agent output"""
        score = self.calculate_score(agent_output)
        status = "pass" if score >= 80 else "conditional" if score >= 60 else "fail"
        return ReviewResult(score, status, self.get_comments())
```

### 7.3 Orchestrator

```python
class Orchestrator:
    def __init__(self, project_config):
        self.agents = self.load_agents(project_config)
        self.reviewers = self.load_reviewers(project_config)
    
    def run_workflow(self):
        """Execute complete workflow"""
        for stage in self.stages:
            agent = self.agents[stage]
            reviewer = self.reviewers[stage]
            
            # Agent execution
            output = agent.execute(self.get_input(stage))
            
            # Review Agent check
            review_result = reviewer.review(output)
            
            # Human confirmation
            if not self.wait_human_confirmation(stage, review_result):
                return False
            
        return True
```

## 8. Implementation Plan

### Phase 1: Core Framework
1. Implement base classes (BaseAgent, ReviewAgent, Orchestrator)
2. Implement review engine and scoring system
3. Implement configuration management

### Phase 2: Agent Implementation
1. Implement 6 main Agents
2. Implement 6 Review Agents
3. Add CSRC compliance checks

### Phase 3: Skill Implementation
1. Implement base skills
2. Implement compliance skills
3. Implement security and performance skills

### Phase 4: Project Configuration
1. Create online-mall project configuration
2. Define CSRC compliance rules
3. Create code templates

### Phase 5: Testing & Documentation
1. Write unit tests
2. Create user documentation
3. Create developer documentation

## 9. Success Criteria

1. All 6 Agents + 6 Review Agents functional
2. Review flow works with human confirmation
3. CSRC compliance skills operational
4. Project-specific configuration for online-mall
5. Independent executable scripts
6. Comprehensive documentation

---

**Document Version**: 1.0
**Created**: 2026-05-28
**Author**: AI Scaffold Design Team
