import pytest
import tempfile
import os
import yaml
from core.config_manager import ConfigManager


class TestConfigManager:
    def test_load_default_config(self):
        with tempfile.TemporaryDirectory() as tmpdir:
            configs_dir = os.path.join(tmpdir, "configs")
            os.makedirs(configs_dir)
            config_file = os.path.join(configs_dir, "default.yaml")
            with open(config_file, "w") as f:
                yaml.dump({"project": {"name": "test"}}, f)

            cm = ConfigManager(tmpdir)
            config = cm.load_config("default")
            assert config["project"]["name"] == "test"

    def test_load_project_config(self):
        with tempfile.TemporaryDirectory() as tmpdir:
            projects_dir = os.path.join(tmpdir, "configs", "projects", "test-project")
            os.makedirs(projects_dir)
            config_file = os.path.join(projects_dir, "project.yaml")
            with open(config_file, "w") as f:
                yaml.dump({"name": "test-project", "version": "1.0"}, f)

            cm = ConfigManager(tmpdir)
            config = cm.load_project_config("test-project")
            assert config["name"] == "test-project"

    def test_save_config(self):
        with tempfile.TemporaryDirectory() as tmpdir:
            cm = ConfigManager(tmpdir)
            cm.save_config("test", {"key": "value"})

            config = cm.load_config("test")
            assert config["key"] == "value"

    def test_load_nonexistent_config(self):
        with tempfile.TemporaryDirectory() as tmpdir:
            cm = ConfigManager(tmpdir)
            with pytest.raises(FileNotFoundError):
                cm.load_config("nonexistent")
