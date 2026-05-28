from typing import Dict, Any
from agents.base_agent import BaseAgent


class ProjectManagerAgent(BaseAgent):
    def _process(self, input_data: Dict[str, Any]) -> Dict[str, Any]:
        requirements = input_data.get("requirements", "")
        return {
            "project_plan": {
                "phases": ["requirements", "design", "development", "testing", "deployment"],
                "timeline": "3 months",
                "resources": ["developer", "tester", "designer"]
            },
            "milestones": [
                "Requirements approved",
                "Design completed",
                "Development completed",
                "Testing passed",
                "Deployment successful"
            ]
        }
