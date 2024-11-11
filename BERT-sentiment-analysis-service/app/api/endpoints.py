# app/api/endpoints.py
from typing import List
from fastapi import APIRouter, Depends, Request
from app.models.schemas import CompanyNews, SentimentResult
from app.services.sentiment_analyzer import analyze_sentiment
from app.api.dependencies import get_model_components

router = APIRouter()

@router.post("/analyze-list", response_model=List[SentimentResult])
async def analyze_company_news(
        news_list: List[CompanyNews],
        model_components = Depends(get_model_components)
):
    tokenizer, model, device = model_components
    results = []
    for news in news_list:
        text = news.headline or news.summary or ''
        if not text:
            continue
        sentiment, confidence = analyze_sentiment(text, tokenizer, model, device)
        result = SentimentResult(
            date=news.datetime,
            sentiment=sentiment,
            confidence=confidence,
            ticker=news.related,
            id=news.id
        )
        results.append(result)
    return results
