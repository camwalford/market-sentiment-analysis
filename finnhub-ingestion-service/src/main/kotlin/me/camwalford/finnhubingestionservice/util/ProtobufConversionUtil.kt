package me.camwalford.finnhubingestionservice.util

import com.google.protobuf.util.JsonFormat
import me.camwalford.finnhubingestionservice.model.protobuf.MarketNewsList
import org.slf4j.LoggerFactory


object ProtobufConversionUtil {

    private val logger = LoggerFactory.getLogger(ProtobufConversionUtil::class.java)

    fun convertMarketNewsList(jsonNewsList: String): MarketNewsList {
        val builder = MarketNewsList.newBuilder()
        return try {
            JsonFormat.parser().merge(jsonNewsList, builder)
            builder.build()
        } catch (e: Exception) {
            logger.error("Error parsing JSON to Protobuf: ${e.message}", e)
            throw RuntimeException("Error parsing JSON to Protobuf", e)
        }
    }
}
