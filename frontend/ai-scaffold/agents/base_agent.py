from typing import Dict, Any, List
from dataclasses import dataclass


@dataclass
class ReviewResult:
    score: int
    status: str
    comments: List[str]


class BaseAgent:
    def __init__(self, config: Dict[str, Any]):
        self.config = config
        self.skills = []
        self.output = None

    def execute(self, input_data: Dict[str, Any]) -> Dict[str, Any]:
        self.output = {
            "agent_name": self.config.get("name", "unknown"),
            "deliverables": self._process(input_data)
        }
        return self.output

    def _process(self, input_data: Dict[str, Any]) -> Dict[str, Any]:
        return {"processed": True, "input": input_data}

    def get_output(self) -> Dict[str, Any]:
        if self.output is None:
            raise RuntimeError("Agent has not been executed yet")
        return self.output


class ReviewAgent(BaseAgent):
    def review(self, agent_output: Dict[str, Any]) -> ReviewResult:
        score = self._calculate_score(agent_output)
        comments = self._generate_comments(agent_output)
        status = self._determine_status(score)
        return ReviewResult(score=score, status=status, comments=comments)

    def _calculate_score(self, agent_output: Dict[str, Any]) -> int:
        deliverables = agent_output.get("deliverables", {})
        if not deliverables:
            return 0
        return 80

    def _generate_comments(self, agent_output: Dict[str, Any]) -> List[str]:
        return ["Review completed"]

    def _determine_status(self, score: int) -> str:
        if score >= 80:
            return "pass"
        elif score >= 60:
            return "conditional"
        else:
            return "fail"
