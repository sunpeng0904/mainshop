import os
import yaml
from typing import Dict, Any


class ConfigManager:
    def __init__(self, base_dir: str):
        self.base_dir = base_dir
        self.configs_dir = os.path.join(base_dir, "configs")

    def load_config(self, config_name: str) -> Dict[str, Any]:
        config_file = os.path.join(self.configs_dir, f"{config_name}.yaml")
        if not os.path.exists(config_file):
            raise FileNotFoundError(f"Config file not found: {config_file}")
        with open(config_file, "r", encoding="utf-8") as f:
            return yaml.safe_load(f)

    def load_project_config(self, project_name: str) -> Dict[str, Any]:
        project_dir = os.path.join(self.configs_dir, "projects", project_name)
        config_file = os.path.join(project_dir, "project.yaml")
        if not os.path.exists(config_file):
            raise FileNotFoundError(f"Project config not found: {config_file}")
        with open(config_file, "r", encoding="utf-8") as f:
            return yaml.safe_load(f)

    def save_config(self, config_name: str, config: Dict[str, Any]) -> None:
        os.makedirs(self.configs_dir, exist_ok=True)
        config_file = os.path.join(self.configs_dir, f"{config_name}.yaml")
        with open(config_file, "w", encoding="utf-8") as f:
            yaml.dump(config, f, allow_unicode=True)
