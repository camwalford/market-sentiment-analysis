package me.camwalford.backendapiservice.repository


import me.camwalford.backendapiservice.model.StockTicker
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StockTickerRepository : JpaRepository<StockTicker, Long> {
    fun findBySymbol(symbol: String): StockTicker?
}
