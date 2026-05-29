from setuptools import setup, find_packages

setup(
    name="ai-scaffold",
    version="1.0.0",
    packages=find_packages(),
    install_requires=[
        "pyyaml>=6.0",
    ],
    entry_points={
        "console_scripts": [
            "scaffold=scaffold:main",
        ],
    },
    author="AI Scaffold Team",
    description="AI-powered software development lifecycle management framework",
    python_requires=">=3.10",
)
