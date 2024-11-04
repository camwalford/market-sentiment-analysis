package me.camwalford.finnhubingestionservice.service

import me.camwalford.finnhubingestionservice.model.protobuf.CompanyNewsList
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class CompanyNewsProducer(
    private val kafkaTemplate: KafkaTemplate<String, ByteArray>,
    @Value("\${kafka.topic.company-news}") private val topic: String,
    @Value("\${kafka.key.company-news}") private val key: String
) {

    private val logger = LoggerFactory.getLogger(CompanyNewsProducer::class.java)

    fun sendCompanyNews(companyNewsList: CompanyNewsList) {
        logger.info("Sending companyNewsList to Kafka topic: $topic with key: $key")
        val bytes = companyNewsList.toByteArray()
        kafkaTemplate.send(topic, key, bytes)
    }
}