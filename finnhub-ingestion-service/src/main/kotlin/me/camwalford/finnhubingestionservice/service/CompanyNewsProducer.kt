package me.camwalford.finnhubingestionservice.service

import me.camwalford.finnhubingestionservice.model.protobuf.CompanyNewsProto
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class CompanyNewsProducer(
    private val kafkaTemplate: KafkaTemplate<String, ByteArray>,
    @Value("\${kafka.topic.company-news}") private val topic: String
) {

    private val logger = LoggerFactory.getLogger(CompanyNewsProducer::class.java)

    fun sendCompanyNews(companyNews: CompanyNewsProto, key: String) {
        logger.info("Sending companyNewsList to Kafka topic: $topic with key: $key")
        val bytes = companyNews.toByteArray()
        kafkaTemplate.send(topic, key, bytes)
    }
}