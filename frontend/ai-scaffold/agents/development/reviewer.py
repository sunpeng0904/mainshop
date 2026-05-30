from typing import Dict, Any, List
from agents.base_agent import ReviewAgent, ReviewResult


class DevelopmentReviewer(ReviewAgent):
    def _calculate_score(self, agent_output: Dict[str, Any]) -> int:
        deliverables = agent_output.get("deliverables", {})
        score = 0

        if "code" in deliverables:
            score += 40
            if deliverables["code"].get("compliance", {}).get("compliant"):
                score += 10

        if "tests" in deliverables:
            score += 40
            coverage = deliverables["tests"].get("coverage", "0%")
            if int(coverage.replace("%", "")) >= 80:
                score += 10

        return min(score, 100)

    def _generate_comments(self, agent_output: Dict[str, Any]) -> List[str]:
        comments = []
        deliverables = agent_output.get("deliverables", {})

        if "code" in deliverables:
            files = deliverables["code"].get("files", [])
            comments.append(f"Generated {len(files)} code files")

            compliance = deliverables["code"].get("compliance", {})
            if compliance.get("compliant"):
                comments.append("Code meets CSRC compliance standards")

        if "tests" in deliverables:
            coverage = deliverables["tests"].get("coverage", "0%")
            comments.append(f"Test coverage: {coverage}")

        return comments
