from typing import Dict, Any, List
from agents.base_agent import ReviewAgent, ReviewResult


class DeploymentReviewer(ReviewAgent):
    def _calculate_score(self, agent_output: Dict[str, Any]) -> int:
        deliverables = agent_output.get("deliverables", {})
        score = 0

        if "deployment_record" in deliverables:
            record = deliverables["deployment_record"]
            if record.get("status") == "success":
                score += 50

        if "rollback_plan" in deliverables:
            plan = deliverables["rollback_plan"]
            if plan.get("steps") and len(plan["steps"]) >= 3:
                score += 50

        return min(score, 100)

    def _generate_comments(self, agent_output: Dict[str, Any]) -> List[str]:
        comments = []
        deliverables = agent_output.get("deliverables", {})

        if "deployment_record" in deliverables:
            record = deliverables["deployment_record"]
            comments.append(f"Deployment status: {record.get('status', 'unknown')}")
            comments.append(f"Environment: {record.get('environment', 'unknown')}")

        if "rollback_plan" in deliverables:
            plan = deliverables["rollback_plan"]
            steps_count = len(plan.get("steps", []))
            comments.append(f"Rollback plan has {steps_count} steps")

        return comments
