from typing import Dict, Any, List
from agents.base_agent import BaseAgent, ReviewAgent


class Orchestrator:
    def __init__(self, config: Dict[str, Any]):
        self.config = config
        self.stages = config.get("stages", [])
        self.agents = self._initialize_agents()
        self.reviewers = self._initialize_reviewers()
        self.stage_status: Dict[str, Dict[str, Any]] = {}

    def _initialize_agents(self) -> Dict[str, BaseAgent]:
        agents = {}
        for stage in self.stages:
            agent_config = self.config.get("agents", {}).get(stage, {})
            agents[stage] = BaseAgent(agent_config)
        return agents

    def _initialize_reviewers(self) -> Dict[str, ReviewAgent]:
        reviewers = {}
        for stage in self.stages:
            reviewer_config = {"name": f"{stage}_reviewer"}
            reviewers[stage] = ReviewAgent(reviewer_config)
        return reviewers

    def run_workflow(self) -> bool:
        for stage in self.stages:
            try:
                agent = self.agents[stage]
                reviewer = self.reviewers[stage]

                output = agent.execute({"stage": stage})
                review_result = reviewer.review(output)

                self.stage_status[stage] = {
                    "completed": True,
                    "score": review_result.score,
                    "status": review_result.status
                }
            except Exception as e:
                self.stage_status[stage] = {
                    "completed": False,
                    "score": 0,
                    "status": "error",
                    "error": str(e)
                }

        # Return False if any stage failed
        for stage_status in self.stage_status.values():
            if stage_status.get("status") == "fail" or stage_status.get("status") == "error":
                return False
        return True

    def get_stage_status(self, stage: str) -> Dict[str, Any]:
        return self.stage_status.get(stage, {"completed": False})

    def get_all_status(self) -> Dict[str, Any]:
        return self.stage_status
