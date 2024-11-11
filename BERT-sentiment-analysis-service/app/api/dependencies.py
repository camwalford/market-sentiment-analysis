# app/api/dependencies.py
from fastapi import Depends, Request

def get_model_components(request: Request):
    return request.app.state.tokenizer, request.app.state.model, request.app.state.device
