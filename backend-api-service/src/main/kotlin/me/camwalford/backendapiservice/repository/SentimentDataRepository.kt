package me.camwalford.backendapiservice.repository


import me.camwalford.backendapiservice.model.SentimentData
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface SentimentDataRepository : JpaRepository<SentimentData, Long> {
    fun findByTickerAndDateBetween(
        ticker: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<SentimentData>
}