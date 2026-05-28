from typing import Dict, Any, List
from agents.base_agent import ReviewAgent, ReviewResult


class ProjectManagerReviewer(ReviewAgent):
    def _calculate_score(self, agent_output: Dict[str, Any]) -> int:
        deliverables = agent_output.get("deliverables", {})
        score = 0

        if "project_plan" in deliverables:
            score += 40
        if "milestones" in deliverables:
            score += 40
        if deliverables.get("project_plan", {}).get("phases"):
            score += 20

        return min(score, 100)

    def _generate_comments(self, agent_output: Dict[str, Any]) -> List[str]:
        comments = []
        deliverables = agent_output.get("deliverables", {})

        if "project_plan" in deliverables:
            comments.append("Project plan created successfully")
        else:
            comments.append("Missing project plan")

        if "milestones" in deliverables:
            comments.append(f"Defined {len(deliverables['milestones'])} milestones")

        return comments
