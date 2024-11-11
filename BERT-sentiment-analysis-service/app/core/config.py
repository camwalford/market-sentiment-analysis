import yaml

with open("config/config.yaml", "r") as config_file:
    config = yaml.safe_load(config_file)

class Settings:
    kafka_bootstrap_servers = config["kafka"]["bootstrap_servers"]
    # Add other configurations as needed

settings = Settings()
