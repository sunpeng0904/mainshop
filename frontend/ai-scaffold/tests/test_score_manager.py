import pytest
from core.score_manager import ScoreManager
from agents.base_agent import ReviewResult
from core.orchestrator import Orchestrator


class TestScoreManager:
    def test_calculate_score_pass(self):
        sm = ScoreManager()
        result = sm.calculate_status(85)
        assert result == "pass"

    def test_calculate_score_conditional(self):
        sm = ScoreManager()
        result = sm.calculate_status(70)
        assert result == "conditional"

    def test_calculate_score_fail(self):
        sm = ScoreManager()
        result = sm.calculate_status(50)
        assert result == "fail"

    def test_calculate_score_boundary_pass(self):
        sm = ScoreManager()
        assert sm.calculate_status(80) == "pass"

    def test_calculate_score_boundary_conditional(self):
        sm = ScoreManager()
        assert sm.calculate_status(79) == "conditional"
        assert sm.calculate_status(60) == "conditional"

    def test_calculate_score_boundary_fail(self):
        sm = ScoreManager()
        assert sm.calculate_status(59) == "fail"

    def test_calculate_score_invalid_negative(self):
        sm = ScoreManager()
        with pytest.raises(ValueError):
            sm.calculate_status(-1)

    def test_calculate_score_invalid_above_100(self):
        sm = ScoreManager()
        with pytest.raises(ValueError):
            sm.calculate_status(101)

    def test_create_review_result(self):
        sm = ScoreManager()
        result = sm.create_review_result(85, ["Good code quality"])
        assert result.score == 85
        assert result.status == "pass"
        assert result.comments == ["Good code quality"]


class TestOrchestrator:
    def test_run_workflow_success(self):
        config = {
            "stages": ["project_manager", "requirement", "design"],
            "agents": {
                "project_manager": {"name": "ProjectManager"},
                "requirement": {"name": "Requirement"},
                "design": {"name": "Design"}
            }
        }
        orchestrator = Orchestrator(config)
        result = orchestrator.run_workflow()
        assert result is True
        # Verify all stages ran
        all_status = orchestrator.get_all_status()
        assert len(all_status) == 3
        assert "project_manager" in all_status
        assert "requirement" in all_status
        assert "design" in all_status

    def test_get_stage_status(self):
        config = {
            "stages": ["project_manager", "requirement"],
            "agents": {
                "project_manager": {"name": "ProjectManager"},
                "requirement": {"name": "Requirement"}
            }
        }
        orchestrator = Orchestrator(config)
        orchestrator.run_workflow()
        status = orchestrator.get_stage_status("project_manager")
        assert status["completed"] is True
        assert "score" in status
        assert "status" in status
        assert status["score"] == 80
        assert status["status"] == "pass"

    def test_get_stage_status_nonexistent(self):
        config = {
            "stages": ["project_manager"],
            "agents": {"project_manager": {"name": "ProjectManager"}}
        }
        orchestrator = Orchestrator(config)
        status = orchestrator.get_stage_status("nonexistent")
        assert status == {"completed": False}

    def test_get_all_status(self):
        config = {
            "stages": ["project_manager", "requirement"],
            "agents": {
                "project_manager": {"name": "ProjectManager"},
                "requirement": {"name": "Requirement"}
            }
        }
        orchestrator = Orchestrator(config)
        orchestrator.run_workflow()
        all_status = orchestrator.get_all_status()
        assert len(all_status) == 2
        assert "project_manager" in all_status
        assert "requirement" in all_status

    def test_run_workflow_empty_stages(self):
        config = {"stages": [], "agents": {}}
        orchestrator = Orchestrator(config)
        result = orchestrator.run_workflow()
        assert result is True
        assert orchestrator.get_all_status() == {}
