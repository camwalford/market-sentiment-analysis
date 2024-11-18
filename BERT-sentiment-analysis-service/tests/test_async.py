# # tests/test_async.py
#
# import pytest
# from httpx import AsyncClient
# from app.main import app
#
# @pytest.mark.asyncio
# async def test_analyze_company_news_async():
#     async with AsyncClient(app=app, base_url="http://test") as ac:
#         response = await ac.post("/analyze", json=test_data)
#     assert response.status_code == 200

