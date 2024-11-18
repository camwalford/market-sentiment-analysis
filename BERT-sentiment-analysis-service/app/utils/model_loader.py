from transformers import AutoTokenizer, AutoModelForSequenceClassification
import torch
from app.config.settings import settings

def load_model():
    tokenizer = AutoTokenizer.from_pretrained(settings.finbert_model_path)
    model = AutoModelForSequenceClassification.from_pretrained(settings.finbert_model_path)
    model.eval()
    device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
    model.to(device)
    return tokenizer, model, device
