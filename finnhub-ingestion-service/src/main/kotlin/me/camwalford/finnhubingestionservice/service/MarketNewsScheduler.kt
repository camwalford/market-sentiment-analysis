package me.camwalford.finnhubingestionservice.service

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value

@Service
class MarketNewsScheduler(
    private val finnHubService: FinnHubService,
    private val marketNewsProducer: MarketNewsProducer,
) {
    private val logger = LoggerFactory.getLogger(MarketNewsScheduler::class.java)

    @Scheduled(fixedRate = 60000)
    fun fetchAndSendMarketNews() {
        logger.info("Fetching market news from FinnHubService and sending to Kafka")
        val marketNewsList = finnHubService.fetchMarketNewsList()
        logger.info("Fetched market news data: $marketNewsList")
        marketNewsProducer.sendMarketNews(marketNewsList)

    }

}