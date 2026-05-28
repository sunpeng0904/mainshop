from typing import Dict, Any
from agents.base_agent import BaseAgent


class RequirementAgent(BaseAgent):
    def _process(self, input_data: Dict[str, Any]) -> Dict[str, Any]:
        user_needs = input_data.get("user_needs", "")
        return {
            "requirements_spec": {
                "version": "1.0",
                "sections": ["overview", "functional", "non-functional", "constraints"],
                "author": "AI Requirement Agent"
            },
            "functional_requirements": [
                {"id": "FR-001", "description": "User authentication"},
                {"id": "FR-002", "description": "Product management"},
                {"id": "FR-003", "description": "Order processing"}
            ],
            "non_functional_requirements": [
                {"id": "NFR-001", "description": "Performance: Response time < 2s"},
                {"id": "NFR-002", "description": "Security: HTTPS encryption"}
            ]
        }
