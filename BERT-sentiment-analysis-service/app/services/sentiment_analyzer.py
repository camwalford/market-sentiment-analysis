# app/services/sentiment_analyzer.py
import torch
import logging

logger = logging.getLogger(__name__)

def analyze_sentiment(text, tokenizer, model, device):
    if not text.strip():
        logger.warning("Empty text provided for sentiment analysis.")
        return "neutral", 0.0  # Default to neutral for empty text

    try:
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
    except Exception as e:
        logger.error(f"Error during sentiment analysis: {e}")
        return "neutral", 0.0  # Default response in case of error
