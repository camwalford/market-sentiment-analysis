from fastapi import FastAPI
from app.api import endpoints

app = FastAPI()

# Include routers
app.include_router(endpoints.router)
