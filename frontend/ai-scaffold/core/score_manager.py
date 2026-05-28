from dataclasses import dataclass
from typing import List

PASS_THRESHOLD = 80
CONDITIONAL_THRESHOLD = 60


@dataclass
class ReviewResult:
    score: int
    status: str
    comments: List[str]


class ScoreManager:
    def calculate_status(self, score: int) -> str:
        if not 0 <= score <= 100:
            raise ValueError(f"Score must be between 0 and 100, got {score}")
        if score >= PASS_THRESHOLD:
            return "pass"
        elif score >= CONDITIONAL_THRESHOLD:
            return "conditional"
        else:
            return "fail"

    def create_review_result(self, score: int, comments: List[str]) -> ReviewResult:
        status = self.calculate_status(score)
        return ReviewResult(score=score, status=status, comments=comments)
