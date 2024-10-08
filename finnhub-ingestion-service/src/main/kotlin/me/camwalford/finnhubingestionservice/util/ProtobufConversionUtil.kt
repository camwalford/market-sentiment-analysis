package me.camwalford.finnhubingestionservice.util

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.protobuf.util.JsonFormat
import me.camwalford.finnhubingestionservice.model.protobuf.MarketNewsList
import org.slf4j.LoggerFactory

object ProtobufConversionUtil {

    private val logger = LoggerFactory.getLogger(ProtobufConversionUtil::class.java)
    private val objectMapper = ObjectMapper()

    fun convertMarketNewsList(jsonNewsList: String): MarketNewsList {
        val builder = MarketNewsList.newBuilder()
        return try {
            logger.info("Converting JSON to Protobuf for MarketNewsList")
            val dataArrayNode = objectMapper.readTree(jsonNewsList)

            val rootNode = objectMapper.createObjectNode()

            logger.info("Setting data node in root node")
            rootNode.set<JsonNode>("data", dataArrayNode)

            logger.info("Converting root node to JSON string")
            val jsonString = objectMapper.writeValueAsString(rootNode)

            logger.info("Parsing JSON to Protobuf")
            JsonFormat.parser().merge(jsonString, builder)
            builder.build()
        } catch (e: Exception) {
            logger.error("Error parsing JSON to Protobuf: ${e.message}", e)
            throw RuntimeException("Error parsing JSON to Protobuf", e)
        }
    }
}
