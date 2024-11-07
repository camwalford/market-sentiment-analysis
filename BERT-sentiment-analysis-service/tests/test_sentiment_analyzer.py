from sentiment_analyzer import analyze_sentiment

def test_analyze_sentiment():
    text = "The company's stock is expected to perform well this quarter."
    sentiment, confidence = analyze_sentiment(text)
    print(sentiment, confidence)
    assert sentiment in ["negative", "neutral", "positive"]
    assert 0 <= confidence <= 1

if __name__ == '__main__':
    test_analyze_sentiment()