from app.utils.model_loader import tokenizer, model, device
import torch

def analyze_sentiment(text):
    inputs = tokenizer(
        text,
        return_tensors="pt",
        truncation=True,
        max_length=512,
        padding=True
    )
    inputs = {key: value.to(device) for key, value in inputs.items()}
    with torch.no_grad():
        outputs = model(**inputs)
    logits = outputs.logits
    probabilities = torch.nn.functional.softmax(logits, dim=1)
    predicted_class = torch.argmax(probabilities, dim=1).item()
    sentiment_labels = ["positive", "neutral", "negative"]
    sentiment = sentiment_labels[predicted_class]
    confidence = probabilities[0][predicted_class].item()
    return sentiment, confidence
