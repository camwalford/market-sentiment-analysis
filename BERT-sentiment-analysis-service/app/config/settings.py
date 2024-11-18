from pydantic_settings import BaseSettings
from typing import List

class Settings(BaseSettings):
    finbert_model_path: str = "ProsusAI/finbert"
    allowed_origins: List[str] = ["http://localhost:8080", "http://localhost:8081, http://0.0.0.0:8080"]
    port: int = 8082

    class Config:
        env_file = ".env"

settings = Settings()
