from typing import Dict, Any, List
from agents.base_agent import ReviewAgent, ReviewResult


class DesignReviewer(ReviewAgent):
    def _calculate_score(self, agent_output: Dict[str, Any]) -> int:
        deliverables = agent_output.get("deliverables", {})
        score = 0

        if "design_document" in deliverables:
            score += 40
            if deliverables["design_document"].get("compliance", {}).get("compliant"):
                score += 10

        if "architecture" in deliverables:
            score += 40
            if deliverables["architecture"].get("technology_stack"):
                score += 10

        return min(score, 100)

    def _generate_comments(self, agent_output: Dict[str, Any]) -> List[str]:
        comments = []
        deliverables = agent_output.get("deliverables", {})

        if "design_document" in deliverables:
            compliance = deliverables["design_document"].get("compliance", {})
            if compliance.get("compliant"):
                comments.append("Design meets CSRC compliance standards")
            else:
                comments.append("Design has compliance issues")

        if "architecture" in deliverables:
            tech_stack = deliverables["architecture"].get("technology_stack", {})
            if tech_stack:
                comments.append(f"Technology stack: {tech_stack.get('backend', 'N/A')}")

        return comments
