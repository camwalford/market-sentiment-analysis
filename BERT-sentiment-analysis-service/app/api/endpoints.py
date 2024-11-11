from typing import List

from fastapi import APIRouter
from app.models.schemas import CompanyNews, SentimentResult
from app.services.sentiment_analyzer import analyze_sentiment

router = APIRouter()

@router.post("/analyzeList", response_model=List[SentimentResult])
async def analyze_company_news(news_list: List[CompanyNews]):
    results = []
    for news in news_list:
        text = news.headline or news.summary or ''
        if not text:
            continue  # Skip if there's no text to analyze
        sentiment, confidence = analyze_sentiment(text)
        result = SentimentResult(
            date=news.datetime,
            sentiment=sentiment,
            confidence=confidence,
            ticker=news.related,
            id=news.id
        )
        results.append(result)
    return results
