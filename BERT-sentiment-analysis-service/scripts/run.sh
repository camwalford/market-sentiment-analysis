#!/bin/bash
# Start Docker Compose services
docker-compose up -d

# Run the main application
python ../src/main.py
