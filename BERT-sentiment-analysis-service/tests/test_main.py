# tests/test_main.py

from fastapi.testclient import TestClient
from app.main import app
import pytest


def test_analyze_company_news(client):
    # Prepare test data
    test_data = [
        {
            "category": "business",
            "datetime": 1633036800,
            "headline": "Company XYZ reports record profits in Q3",
            "id": 1,
            "image": "http://example.com/image.jpg",
            "related": "XYZ",
            "source": "Example News",
            "summary": "Company XYZ has reported record profits in the third quarter...",
            "url": "http://example.com/article"
        },
        {
            "category": "business",
            "datetime": 1633123200,
            "headline": "Company ABC faces lawsuits over data breach",
            "id": 2,
            "image": "http://example.com/image2.jpg",
            "related": "ABC",
            "source": "Example News",
            "summary": "Company ABC is facing multiple lawsuits after a massive data breach...",
            "url": "http://example.com/article2"
        }
    ]

    # Send POST request to the /analyze endpoint
    response = client.post("/analyze", json=test_data)

    # Assert the response status code
    assert response.status_code == 200

    # Parse the response JSON
    results = response.json()

    # Assert that we have results for each input
    assert len(results) == len(test_data)

    # Perform assertions on the first result
    first_result = results[0]
    assert first_result["date"] == test_data[0]["datetime"]
    assert first_result["ticker"] == test_data[0]["related"]
    assert first_result["id"] == test_data[0]["id"]
    assert first_result["sentiment"] in ["positive", "neutral", "negative"]
    assert 0.0 <= first_result["confidence"] <= 1.0

    # Perform assertions on the second result
    second_result = results[1]
    assert second_result["date"] == test_data[1]["datetime"]
    assert second_result["ticker"] == test_data[1]["related"]
    assert second_result["id"] == test_data[1]["id"]
    assert second_result["sentiment"] in ["positive", "neutral", "negative"]
    assert 0.0 <= second_result["confidence"] <= 1.0
