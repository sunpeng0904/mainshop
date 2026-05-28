from typing import Dict, Any, List


class StandardChecker:
    def __init__(self):
        self.csrc_rules = [
            "Must have code comments",
            "Must use proper annotations",
            "Must handle exceptions properly",
            "Must log important operations",
            "Must validate input parameters"
        ]

    def check_design_compliance(self) -> Dict[str, Any]:
        return {
            "compliant": True,
            "rules": self.csrc_rules,
            "checked_at": "design_phase"
        }

    def check_code_compliance(self, code: str) -> Dict[str, Any]:
        issues = []

        if "TODO" in code:
            issues.append("Contains TODO comments")
        if "FIXME" in code:
            issues.append("Contains FIXME comments")

        return {
            "compliant": len(issues) == 0,
            "issues": issues,
            "checked_at": "development_phase"
        }
