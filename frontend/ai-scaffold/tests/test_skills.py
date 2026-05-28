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


from skills.base_skills.code_generator import CodeGenerator


class TestCodeGenerator:
    def test_generate_controller(self):
        generator = CodeGenerator()
        code = generator.generate_controller("User")
        assert "class UserController" in code
        assert "@RestController" in code

    def test_generate_service(self):
        generator = CodeGenerator()
        code = generator.generate_service("User")
        assert "class UserService" in code
        assert "@Service" in code


from skills.security.vulnerability_scanner import VulnerabilityScanner
from skills.security.permission_handler import PermissionHandler
from skills.performance.performance_tester import PerformanceTester


class TestVulnerabilityScanner:
    def test_scan_code(self):
        scanner = VulnerabilityScanner()
        result = scanner.scan("SELECT * FROM users WHERE id = " + "user_input")
        assert "vulnerabilities" in result
        assert len(result["vulnerabilities"]) > 0

    def test_scan_safe_code(self):
        scanner = VulnerabilityScanner()
        result = scanner.scan("SELECT * FROM users WHERE id = ?")
        assert len(result["vulnerabilities"]) == 0


class TestPermissionHandler:
    def test_check_permission(self):
        handler = PermissionHandler()
        result = handler.check_permission("admin", "user:delete")
        assert result["allowed"] is True

    def test_check_permission_denied(self):
        handler = PermissionHandler()
        result = handler.check_permission("guest", "user:delete")
        assert result["allowed"] is False


class TestPerformanceTester:
    def test_measure_response_time(self):
        tester = PerformanceTester()
        result = tester.measure_response_time(lambda: None, iterations=10)
        assert "average_time" in result
        assert "min_time" in result
        assert "max_time" in result
