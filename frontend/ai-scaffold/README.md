# AI Scaffold Framework

A modular, reusable AI scaffold framework for managing software development lifecycle with CSRC compliance support.

## Features

- **6 Main Agents**: ProjectManager, Requirement, Design, Development, Testing, Deployment
- **6 Review Agents**: Quality control for each stage
- **CSRC Compliance**: China Securities Regulatory Commission compliance checking
- **Modular Skills**: Base, Compliance, Security, Performance skills
- **100-point Scoring**: Pass/Fail/Conditional review system
- **Human Confirmation**: Required human approval at each stage

## Installation

```bash
pip install -e .
```

## Usage

```bash
# Run workflow for a project
python scaffold.py --project online-mall --action run

# Check status
python scaffold.py --project online-mall --action status

# Confirm a stage
python scaffold.py --project online-mall --action confirm --stage design --status pass
```

## Project Structure

```
ai-scaffold/
├── core/              # Core framework components
├── agents/            # Agent implementations
├── skills/            # Skill implementations
├── configs/           # Project configurations
├── templates/         # Code and document templates
└── tests/             # Unit tests
```

## Configuration

Project-specific configurations are stored in `configs/projects/<project-name>/`.

## License

MIT
