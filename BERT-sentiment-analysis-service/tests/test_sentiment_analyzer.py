# tests/test_sentiment_analyzer.py

from app.services.sentiment_analyzer import analyze_sentiment

def test_analyze_sentiment_positive():
    text = "The company has achieved record-breaking profits this quarter."
    sentiment, confidence = analyze_sentiment(text)
    assert sentiment == "positive"
    assert 0.0 <= confidence <= 1.0

def test_analyze_sentiment_negative():
    text = "The company is facing significant losses and layoffs."
    sentiment, confidence = analyze_sentiment(text)
    assert sentiment == "negative"
    assert 0.0 <= confidence <= 1.0

def test_analyze_sentiment_neutral():
    text = "The company held its annual general meeting with shareholders."
    sentiment, confidence = analyze_sentiment(text)
    assert sentiment == "neutral"
    assert 0.0 <= confidence <= 1.0

def test_analyze_sentiment_empty_string():
    text = ""
    sentiment, confidence = analyze_sentiment(text)
    # Depending on your implementation, you might handle empty strings differently.
    # For this example, we'll assume it returns neutral sentiment with 0 confidence.
    assert sentiment == "neutral"
    assert confidence == 0.0
