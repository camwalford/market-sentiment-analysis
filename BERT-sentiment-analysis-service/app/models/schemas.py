from pydantic import BaseModel
from typing import Optional

class CompanyNews(BaseModel):
    category: Optional[str]
    datetime: Optional[int]
    headline: Optional[str]
    id: Optional[int]
    image: Optional[str]
    related: Optional[str]  # This is the company ticker
    source: Optional[str]
    summary: Optional[str]
    url: Optional[str]

class SentimentResult(BaseModel):
    date: Optional[int]
    sentiment: str
    confidence: float
    ticker: Optional[str]
    id: Optional[int]
