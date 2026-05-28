import pytest
from agents.base_agent import BaseAgent, ReviewAgent, ReviewResult


class TestBaseAgent:
    def test_execute_returns_output(self):
        agent = BaseAgent({"name": "test-agent"})
        result = agent.execute({"input": "test"})
        assert result is not None

    def test_get_output_returns_deliverables(self):
        agent = BaseAgent({"name": "test-agent"})
        agent.execute({"input": "test"})
        output = agent.get_output()
        assert "deliverables" in output

    def test_execute_sets_agent_name(self):
        agent = BaseAgent({"name": "my-agent"})
        result = agent.execute({"input": "test"})
        assert result["agent_name"] == "my-agent"

    def test_get_output_before_execute_raises_error(self):
        agent = BaseAgent({"name": "test-agent"})
        with pytest.raises(RuntimeError):
            agent.get_output()


class TestReviewAgent:
    def test_review_returns_review_result(self):
        reviewer = ReviewAgent({"name": "test-reviewer"})
        agent_output = {"deliverables": {"code": "test code"}}
        result = reviewer.review(agent_output)
        assert isinstance(result, ReviewResult)
        assert result.score >= 0
        assert result.score <= 100
        assert result.status in ["pass", "conditional", "fail"]

    def test_review_with_empty_deliverables(self):
        reviewer = ReviewAgent({"name": "test-reviewer"})
        agent_output = {"deliverables": {}}
        result = reviewer.review(agent_output)
        assert result.score == 0
        assert result.status == "fail"


from agents.project_manager.agent import ProjectManagerAgent
from agents.project_manager.reviewer import ProjectManagerReviewer


class TestProjectManagerAgent:
    def test_create_project_plan(self):
        agent = ProjectManagerAgent({"name": "ProjectManager"})
        result = agent.execute({"requirements": "Build e-commerce platform"})
        assert "project_plan" in result["deliverables"]
        assert "milestones" in result["deliverables"]

    def test_review_project_plan(self):
        reviewer = ProjectManagerReviewer({"name": "PMReviewer"})
        agent_output = {
            "deliverables": {
                "project_plan": {"phases": ["design", "develop", "test"]},
                "milestones": ["Phase 1 complete"]
            }
        }
        result = reviewer.review(agent_output)
        assert result.score >= 80
        assert result.status == "pass"


from agents.requirement.agent import RequirementAgent
from agents.requirement.reviewer import RequirementReviewer


class TestRequirementAgent:
    def test_analyze_requirements(self):
        agent = RequirementAgent({"name": "Requirement"})
        result = agent.execute({"user_needs": "User management system"})
        assert "requirements_spec" in result["deliverables"]
        assert "functional_requirements" in result["deliverables"]

    def test_review_requirements(self):
        reviewer = RequirementReviewer({"name": "ReqReviewer"})
        agent_output = {
            "deliverables": {
                "requirements_spec": {"version": "1.0", "sections": ["overview", "functional"]},
                "functional_requirements": [{"id": "FR-001", "description": "User login"}]
            }
        }
        result = reviewer.review(agent_output)
        assert result.score >= 80
