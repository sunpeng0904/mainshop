from dataclasses import dataclass
from typing import List


@dataclass
class ReviewResult:
    score: int
    status: str
    comments: List[str]


class ScoreManager:
    def calculate_status(self, score: int) -> str:
        if score >= 80:
            return "pass"
        elif score >= 60:
            return "conditional"
        else:
            return "fail"

    def create_review_result(self, score: int, comments: List[str]) -> ReviewResult:
        status = self.calculate_status(score)
        return ReviewResult(score=score, status=status, comments=comments)
