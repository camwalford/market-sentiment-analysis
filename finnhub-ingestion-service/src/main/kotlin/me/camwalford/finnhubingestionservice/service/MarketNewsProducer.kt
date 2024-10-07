package me.camwalford.finnhubingestionservice.service

import me.camwalford.finnhubingestionservice.model.protobuf.MarketNewsList
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class MarketNewsProducer(private val kafkaTemplate: KafkaTemplate<String, MarketNewsList>) {

    private val logger = LoggerFactory.getLogger(MarketNewsProducer::class.java)

    fun sendMarketNews(topic: String, key: String, marketNewsList: MarketNewsList) {
        logger.info("Sending MarketNewsList to Kafka topic: $topic with key: $key")
        kafkaTemplate.send(topic, key, marketNewsList)
    }
}