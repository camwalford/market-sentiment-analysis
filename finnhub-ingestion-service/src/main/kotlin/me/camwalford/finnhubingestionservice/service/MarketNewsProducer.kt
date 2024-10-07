package me.camwalford.finnhubingestionservice.service

import me.camwalford.finnhubingestionservice.model.protobuf.MarketNewsList
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class MarketNewsProducer(private val kafkaTemplate: KafkaTemplate<String, MarketNewsList>) {
    fun sendMarketNews(topic: String, key: String, marketNewsList: MarketNewsList){
        kafkaTemplate.send(topic, key, marketNewsList)
    }
}