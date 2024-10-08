package me.camwalford.finnhubingestionservice.service

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import me.camwalford.finnhubingestionservice.model.protobuf.MarketNewsList
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.apache.kafka.common.TopicPartition
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import java.util.concurrent.CompletableFuture

class MarketNewsProducerTest {

  @MockK
  private lateinit var kafkaTemplate: KafkaTemplate<String, MarketNewsList>

  private lateinit var marketNewsProducer: MarketNewsProducer

  private val topic = "market-news-topic"
  private val key = "general"

  @BeforeEach
  fun setUp() {
    MockKAnnotations.init(this)
    marketNewsProducer = MarketNewsProducer(kafkaTemplate, topic, key)
  }

  @Test
  fun `test sendMarketNews successfully`() {
    // Arrange
    val marketNewsList = MarketNewsList.newBuilder().build()
    val sendResult = SendResult(ProducerRecord(topic, key, marketNewsList), RecordMetadata(TopicPartition(topic, 0), 0, 0, 0, 0, 0))
    val future = CompletableFuture.completedFuture(sendResult)
    every { kafkaTemplate.send(topic, key, marketNewsList) } returns future

    // Act
    marketNewsProducer.sendMarketNews(marketNewsList)

    // Assert
    verify { kafkaTemplate.send(topic, key, marketNewsList) }
  }

  @Test
  fun `test sendMarketNews when KafkaTemplate throws exception`() {
    // Arrange
    val marketNewsList = MarketNewsList.newBuilder().build()
    val future = CompletableFuture<SendResult<String, MarketNewsList>>()
    future.completeExceptionally(RuntimeException("Kafka error"))
    every { kafkaTemplate.send(topic, key, marketNewsList) } returns future

    // Act
    marketNewsProducer.sendMarketNews(marketNewsList)

    // Assert
    verify { kafkaTemplate.send(topic, key, marketNewsList) }
  }
}
