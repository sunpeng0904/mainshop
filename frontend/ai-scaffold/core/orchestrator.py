from typing import Dict, Any, List
from agents.base_agent import BaseAgent, ReviewAgent


class Orchestrator:
    def __init__(self, config: Dict[str, Any]):
        self.config = config
        self.stages = config.get("stages", [])
        self.agents = self._initialize_agents()
        self.reviewers = self._initialize_reviewers()
        self.stage_status = {}

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
            agent = self.agents[stage]
            reviewer = self.reviewers[stage]

            output = agent.execute({"stage": stage})
            review_result = reviewer.review(output)

            self.stage_status[stage] = {
                "completed": True,
                "score": review_result.score,
                "status": review_result.status
            }

        return True

    def get_stage_status(self, stage: str) -> Dict[str, Any]:
        return self.stage_status.get(stage, {"completed": False})

    def get_all_status(self) -> Dict[str, Any]:
        return self.stage_status
