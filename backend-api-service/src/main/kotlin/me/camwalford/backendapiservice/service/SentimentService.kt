//package me.camwalford.backendapiservice.service
//
//import me.camwalford.backendapiservice.model.SentimentData
//import me.camwalford.backendapiservice.model.User
//import me.camwalford.backendapiservice.repository.SentimentDataRepository
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.stereotype.Service
//import java.time.LocalDate
//
//@Service
//class SentimentService @Autowired constructor(
//    private val sentimentDataRepository: SentimentDataRepository
//) {
//
//    fun getSentimentData(
//        ticker: String,
//        startDate: LocalDate,
//        endDate: LocalDate
//    ): List<SentimentData> {
//        return sentimentDataRepository.findByTickerAndDateBetween(ticker, startDate, endDate)
//    }
//
//    fun processSentimentRequest(user: User, request: SentimentRequest): SentimentResponse {
//        val sentimentData = getSentimentData(
//            request.ticker,
//            request.startDate,
//            request.endDate
//        )
//
//        return SentimentResponse(
//            ticker = request.ticker,
//            sentimentData = sentimentData
//        )
//    }
//}