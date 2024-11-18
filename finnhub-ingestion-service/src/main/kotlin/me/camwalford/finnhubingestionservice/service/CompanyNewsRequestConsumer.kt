//package me.camwalford.finnhubingestionservice.service
//
//import me.camwalford.finnhubingestionservice.model.CompanyNewsRequest
//import org.slf4j.LoggerFactory
//import org.springframework.kafka.annotation.KafkaListener
//import org.springframework.stereotype.Service
//
//import org.springframework.beans.factory.annotation.Value
//
//@Service
//class CompanyNewsRequestConsumer(
//    private val companyNewsFetcher: CompanyNewsFetcher,
//    @Value("\${kafka.topic.user-requests}") private val userRequestsTopic: String
//) {
//
//    private val logger = LoggerFactory.getLogger(CompanyNewsRequestConsumer::class.java)
//
//    @KafkaListener(topics = ["\${kafka.topic.user-requests}"], groupId = "company-news-group")
//    fun consume(request: CompanyNewsRequest) {
//        logger.info("Received CompanyNewsRequest from Kafka: $request")
//        companyNewsFetcher.fetchAndProduceCompanyNews(
//            ticker = request.ticker,
//            fromDate = request.fromDate,
//            toDate = request.toDate
//        )
//    }
//}
