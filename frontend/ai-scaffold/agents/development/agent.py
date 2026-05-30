from typing import Dict, Any
from agents.base_agent import BaseAgent
from skills.base_skills.code_generator import CodeGenerator
from skills.compliance.standard_checker import StandardChecker


class DevelopmentAgent(BaseAgent):
    def __init__(self, config: Dict[str, Any]):
        super().__init__(config)
        self.code_generator = CodeGenerator()
        self.standard_checker = StandardChecker()

    def _process(self, input_data: Dict[str, Any]) -> Dict[str, Any]:
        design = input_data.get("design", "")
        code = self.code_generator.generate_controller("User")
        compliance = self.standard_checker.check_code_compliance(code)

        return {
            "code": {
                "files": ["UserController.java", "UserService.java", "UserMapper.java"],
                "content": code,
                "compliance": compliance
            },
            "tests": {
                "files": ["UserControllerTest.java", "UserServiceTest.java"],
                "coverage": "85%"
            }
        }
