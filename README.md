# market-sentiment-analysis
A monorepo containing several containerized microservices for stock market data collection and both historical and real-time sentiment analysis. 


Project structure
kafka-data-engineering-project/
├── charts/                        # Helm charts directory
│   ├── kafka/                     # Kafka Helm chart
│   │   ├── templates/             # Kubernetes YAML templates for Kafka
│   │   │   ├── kafka-deployment.yaml  # Kafka deployment template
│   │   │   ├── zookeeper-deployment.yaml # Zookeeper deployment template
│   │   │   ├── kafka-service.yaml  # Kafka service template
│   │   │   ├── configmap.yaml      # ConfigMap template for Kafka config
│   │   └── values.yaml             # Default values for Kafka chart (e.g., replicas, ports)
│   │
│   ├── ingestion-service/         # Ingestion service Helm chart
│   │   ├── templates/             # Kubernetes YAML templates for ingestion service
│   │   └── values.yaml            # Default values for ingestion service (e.g., image, replicas)
│   │
│   ├── processing-service/        # Processing service Helm chart
│   │   ├── templates/             # Kubernetes YAML templates for processing service
│   │   └── values.yaml            # Default values for processing service
│   │
│   ├── frontend-dashboard/        # Frontend dashboard Helm chart
│   │   ├── templates/             # Kubernetes YAML templates for frontend service
│   │   └── values.yaml            # Default values for frontend service
│   │
│   └── requirements.yaml          # (Optional) Defines dependencies between charts
│
├── ingestion-service/             # Kotlin service for ingesting data to Kafka
│   ├── src/                       # Kotlin source code
│   ├── Dockerfile                 # Dockerfile for building the ingestion service container
│   └── build.gradle.kts           # Gradle build file
│
├── processing-service/            # Kotlin/Java service for processing Kafka data
│   ├── src/                       # Service source code
│   ├── Dockerfile                 # Dockerfile for building the processing service container
│   └── build.gradle.kts           # Gradle build file
│
├── frontend-dashboard/            # Frontend for real-time dashboard (React, Angular, etc.)
│   ├── src/                       # Frontend source code
│   ├── Dockerfile                 # Dockerfile for building the frontend container
│   └── package.json               # Package dependencies (if using npm)
│
├── kafka-server-config/           # Configurations for Kafka and Zookeeper (Optional for local development)
│   ├── kafka-config.yml           # Kafka broker configuration
│   ├── zookeeper-config.yml       # Zookeeper configuration
│   ├── topics-config.yml          # Topic definitions (optional)
│   └── log4j.properties           # Logging configuration for Kafka
├── docker-compose.yml             # Optional Docker Compose for local development
└── README.md                      # Project documentation