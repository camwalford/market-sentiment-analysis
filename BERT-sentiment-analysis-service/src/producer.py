from confluent_kafka import Producer
import yaml
import json

with open("../config/config.yaml", "r") as config_file:
    config = yaml.safe_load(config_file)
kafka_config = config["kafka"]

producer = Producer({'bootstrap.servers': kafka_config["bootstrap_servers"]})

def delivery_report(err, msg):
    if err is not None:
        print(f"Message delivery failed: {err}")
    else:
        print(f"Message delivered to {msg.topic()} [{msg.partition()}]")

def produce_sentiment(sentiment_data):
    try:
        producer.produce(
            kafka_config["producer_topic"],
            value=json.dumps(sentiment_data).encode("utf-8"),
            callback=delivery_report
        )
        producer.poll(0)
    except Exception as e:
        print(f"Failed to produce message: {e}")
