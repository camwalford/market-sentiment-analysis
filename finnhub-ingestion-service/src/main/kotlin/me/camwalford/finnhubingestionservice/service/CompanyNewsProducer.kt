//package me.camwalford.finnhubingestionservice.service
//
//import io.finnhub.api.models.CompanyNews
//import org.slf4j.LoggerFactory
//import org.springframework.beans.factory.annotation.Value
//import org.springframework.kafka.core.KafkaTemplate
//import org.springframework.stereotype.Service
//
//@Service
//class CompanyNewsProducer(
//    private val kafkaTemplate: KafkaTemplate<String, Any>,
//    @Value("\${kafka.topic.company-news}") private val topic: String
//) {
//
//    private val logger = LoggerFactory.getLogger(CompanyNewsProducer::class.java)
//
//    fun sendCompanyNews(companyNews: CompanyNews, key: String) {
//        logger.info("Sending companyNewsList to Kafka topic: $topic with key: $key")
//        kafkaTemplate.send(topic, key, companyNews)
//    }
//}