from setuptools import setup, find_packages

setup(
    name="sentiment_analysis_project",
    version="0.1",
    packages=find_packages("src"),
    package_dir={"": "src"},
    install_requires=[
        "confluent-kafka",
        "transformers",
        "torch",
        "PyYAML"
    ],
    entry_points={
        "console_scripts": [
            "sentiment-analysis=src.main:start_consumer"
        ]
    }
)
