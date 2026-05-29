#!/usr/bin/env python3
"""
AI Scaffold Framework - Main Entry Point
"""

import argparse
import sys
import os

# Add the ai-scaffold directory to sys.path so that core, agents, skills are importable
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))

from core.orchestrator import Orchestrator
from core.config_manager import ConfigManager


def main():
    parser = argparse.ArgumentParser(description="AI Scaffold Framework")
    parser.add_argument("--project", required=True, help="Project name")
    parser.add_argument("--action", choices=["run", "status", "confirm"],
                       default="run", help="Action to perform")
    parser.add_argument("--stage", help="Stage to confirm (for confirm action)")
    parser.add_argument("--status", choices=["pass", "fail"],
                       help="Status for confirm action")

    args = parser.parse_args()

    config_manager = ConfigManager(".")
    project_config = config_manager.load_project_config(args.project)

    orchestrator = Orchestrator(project_config)

    if args.action == "run":
        print(f"Starting AI Scaffold for project: {args.project}")
        result = orchestrator.run_workflow()
        if result:
            print("Workflow completed successfully!")
        else:
            print("Workflow failed!")
            sys.exit(1)
    elif args.action == "status":
        status = orchestrator.get_all_status()
        for stage, info in status.items():
            print(f"{stage}: {info}")
    elif args.action == "confirm":
        if not args.stage or not args.status:
            print("Error: --stage and --status required for confirm action")
            sys.exit(1)
        print(f"Stage {args.stage} confirmed with status: {args.status}")


if __name__ == "__main__":
    main()
