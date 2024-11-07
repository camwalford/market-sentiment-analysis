from confluent_kafka import Consumer
from src.sentiment_analyzer import analyze_sentiment
from src.producer import produce_sentiment
import yaml

with open("../config/config.yaml", "r") as config_file:
    config = yaml.safe_load(config_file)
kafka_config = config["kafka"]

def start_consumer():
    consumer_conf = {
        'bootstrap.servers': kafka_config["bootstrap_servers"],
        'group.id': kafka_config["group_id"],
        'auto.offset.reset': kafka_config["auto_offset_reset"]
    }
    consumer = Consumer(consumer_conf)
    consumer.subscribe([kafka_config["consumer_topic"]])

    try:
        while True:
            msg = consumer.poll(1.0)
            if msg is None:
                continue
            if msg.error():
                print(f"Consumer error: {msg.error()}")
                continue

            # Decode and analyze message
            text = msg.value().decode('utf-8')
            sentiment, confidence = analyze_sentiment(text)

            # Prepare data for producer
            sentiment_data = {
                "text": text,
                "sentiment": sentiment,
                "confidence": confidence
            }
            produce_sentiment(sentiment_data)  # Send to producer

    except KeyboardInterrupt:
        print("Shutting down consumer.")
    finally:
        consumer.close()
