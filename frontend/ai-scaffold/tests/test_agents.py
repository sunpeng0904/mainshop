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
