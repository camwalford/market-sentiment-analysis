package me.camwalford.backendapiservice.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import java.time.LocalDate


@Entity
@Table(
    name = "sentiment_data",
    indexes = [
        Index(name = "ticker_date_index", columnList = "ticker, date")
    ]
)
data class SentimentData(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val ticker: String = "",

    @Column(nullable = false)
    val date: LocalDate = LocalDate.now(),

    @Column(nullable = false)
    val sentiment: Double = 0.0
)
