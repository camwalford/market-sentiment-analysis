# Market Sentiment Analysis Project (WIP)

This repository contains a set of microservices designed for market sentiment analysis using data ingestion from external APIs, data processing, sentiment analysis, and visualization on a web dashboard. The services are containerized and orchestrated using Docker Compose.

## Project Structure

```plaintext
├── finnhub-ingestion-service            # Service for ingesting data from the Finnhub API
├── BERT-sentiment-analysis-service      # Service for performing NLP sentiment analysis on ingested data
├── web-dashboard-service                # Web frontend for data visualization
├── backend-api-service                  # Backend API Gateway for serving data to the frontend
├── docker-compose.yml                   # Current docker Compose configuration file for orchestrating services
└── README.md                            # Project documentation
```

## Getting Started
### Prerequisites
Docker
Docker Compose
Java 21
Kotlin
Gradle

## References
- [Finnhub API](https://finnhub.io/)
- ChatGPT (Used during development)