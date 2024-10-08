# Market Sentiment Analysis Project (WIP)

This repository contains a set of microservices designed for market sentiment analysis using data ingestion from external APIs, data processing, sentiment analysis, and visualization on a web dashboard. The services are containerized and orchestrated using Docker Compose.

## Project Structure

```plaintext
├── finnhub-ingestion-service            # Service for ingesting data from the Finnhub API
│   ├── src
│   │   ├── main
│   │   │   ├── kotlin
│   │   │   │   └── me/camwalford/finnhubingestionservice
│   │   │   │       ├── client          # API clients for external data sources
│   │   │   │       ├── config          # Configuration files
│   │   │   │       ├── model           # Data models
│   │   │   │       ├── service         # Business logic services
│   │   │   │       ├── util            # Utility functions
│   │   │   │       └── FinnhubIngestionServiceApplication.kt  # Main entry point
│   │   ├── proto                        # Protocol buffer definitions
│   │   └── resources                    # Resource files
│   ├── Dockerfile                       # Dockerfile for the ingestion service
│   ├── build.gradle.kts                 # Gradle build script
│   └── .env.template                    # Template for environment variables
├── kafka                                # Kafka configuration and docker-compose setup
├── nlp-sentiment-analysis-service       # Service for performing NLP sentiment analysis on ingested data
├── postgresql-service                   # Database service using PostgreSQL
├── raw-data-processing-service          # Service for processing raw ingested data
├── web-dashboard-service                # Web frontend for data visualization
├── x-ingestion-service                  # Another ingestion service for a different data source
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

## Installation
1. Clone the repo
```bash
git clone https://github.com/your-username/market-sentiment-analysis.git
cd market-sentiment-analysis
```
2. Copy the .env.template file and configure your environment variables:
```bash
cp .env.template .env
```
3. Build the ingestion service
```bash
./gradew clean build
```
4. Ensure docker desktop is running and enter
```bash
docker-compose up build
```
