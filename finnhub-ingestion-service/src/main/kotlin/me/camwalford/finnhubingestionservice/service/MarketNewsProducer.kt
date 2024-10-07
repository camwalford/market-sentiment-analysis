package me.camwalford.finnhubingestionservice.service

import me.camwalford.finnhubingestionservice.model.protobuf.MarketNewsList
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class MarketNewsProducer(private val kafkaTemplate: KafkaTemplate<String, MarketNewsList>,
                         @Value("\${kafka.topic.market-news}") private val topic: String,
                         @Value("\${kafka.key.market-news}") private val key: String) {

    private val logger = LoggerFactory.getLogger(MarketNewsProducer::class.java)

    fun sendMarketNews(marketNewsList: MarketNewsList) {
        logger.info("Sending MarketNewsList to Kafka topic: $topic with key: $key")
        kafkaTemplate.send(topic, key, marketNewsList)
    }
}