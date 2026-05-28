from typing import Dict, Any, List
from agents.base_agent import ReviewAgent, ReviewResult


class RequirementReviewer(ReviewAgent):
    def _calculate_score(self, agent_output: Dict[str, Any]) -> int:
        deliverables = agent_output.get("deliverables", {})
        score = 0

        if "requirements_spec" in deliverables:
            score += 35
        if "functional_requirements" in deliverables:
            score += 50
        if "non_functional_requirements" in deliverables:
            score += 30

        return min(score, 100)

    def _generate_comments(self, agent_output: Dict[str, Any]) -> List[str]:
        comments = []
        deliverables = agent_output.get("deliverables", {})

        if "functional_requirements" in deliverables:
            req_count = len(deliverables["functional_requirements"])
            comments.append(f"Defined {req_count} functional requirements")

        if "non_functional_requirements" in deliverables:
            comments.append("Non-functional requirements included")

        return comments
