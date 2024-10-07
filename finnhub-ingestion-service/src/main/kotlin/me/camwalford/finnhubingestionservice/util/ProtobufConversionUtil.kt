package me.camwalford.finnhubingestionservice.util

import com.google.protobuf.util.JsonFormat
import me.camwalford.finnhubingestionservice.model.protobuf.MarketNewsList



object ProtobufConversionUtil {
    fun convertMarketNewsList(jsonNewsList: String): MarketNewsList {
        val builder = MarketNewsList.newBuilder()
        JsonFormat.parser().merge(jsonNewsList, builder)
        return builder.build()
    }
}