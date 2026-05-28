import pytest
from core.score_manager import ScoreManager, ReviewResult


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

    def test_create_review_result(self):
        sm = ScoreManager()
        result = sm.create_review_result(85, ["Good code quality"])
        assert result.score == 85
        assert result.status == "pass"
        assert result.comments == ["Good code quality"]
