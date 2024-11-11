from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from app.api import endpoints
from app.utils.model_loader import load_model
from app.config.settings import settings

app = FastAPI()

# Include routers
app.include_router(endpoints.router)

# Enable CORS
app.add_middleware(
    CORSMiddleware,
    allow_origins=settings.allowed_origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Load the model at startup
tokenizer, model, device = load_model()
app.state.tokenizer = tokenizer
app.state.model = model
app.state.device = device
