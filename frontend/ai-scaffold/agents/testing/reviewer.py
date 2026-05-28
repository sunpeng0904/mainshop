from typing import Dict, Any, List
from agents.base_agent import ReviewAgent, ReviewResult


class TestingReviewer(ReviewAgent):
    def _calculate_score(self, agent_output: Dict[str, Any]) -> int:
        deliverables = agent_output.get("deliverables", {})
        score = 0

        if "test_report" in deliverables:
            report = deliverables["test_report"]
            total = report.get("total", 0)
            passed = report.get("passed", 0)
            if total > 0:
                pass_rate = passed / total
                score += int(pass_rate * 50)

        if "coverage_report" in deliverables:
            coverage = deliverables["coverage_report"]
            line_coverage = int(coverage.get("line_coverage", "0%").replace("%", ""))
            if line_coverage >= 80:
                score += 50

        return min(score, 100)

    def _generate_comments(self, agent_output: Dict[str, Any]) -> List[str]:
        comments = []
        deliverables = agent_output.get("deliverables", {})

        if "test_report" in deliverables:
            report = deliverables["test_report"]
            comments.append(f"Total tests: {report.get('total', 0)}")
            comments.append(f"Passed: {report.get('passed', 0)}")
            comments.append(f"Failed: {report.get('failed', 0)}")

        if "coverage_report" in deliverables:
            coverage = deliverables["coverage_report"]
            comments.append(f"Line coverage: {coverage.get('line_coverage', 'N/A')}")

        return comments
