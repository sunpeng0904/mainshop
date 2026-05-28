import pytest
from skills.compliance.standard_checker import StandardChecker


class TestStandardChecker:
    def test_check_design_compliance(self):
        checker = StandardChecker()
        result = checker.check_design_compliance()
        assert "compliant" in result
        assert "rules" in result

    def test_check_code_compliance(self):
        checker = StandardChecker()
        result = checker.check_code_compliance("public class Test {}")
        assert "compliant" in result
        assert "issues" in result

    def test_check_code_compliance_with_todo(self):
        checker = StandardChecker()
        result = checker.check_code_compliance("public class Test { // TODO fix this }")
        assert result["compliant"] is False
        assert len(result["issues"]) > 0
