# tests/test_error_handling.py

def test_analyze_company_news_invalid_input(client):
    # Invalid data (e.g., missing required fields)
    invalid_data = [
        {
            "headline": None,  # Missing headline or summary
            "datetime": None,
            "id": None,
            "related": None
        }
    ]

    response = client.post("/analyze", json=invalid_data)
    assert response.status_code == 200  # Depending on your implementation

    results = response.json()
    assert results == []  # Expecting an empty list if input is skipped
