import me.camwalford.finnhubingestionservice.model.protobuf.MarketNewsList
import me.camwalford.finnhubingestionservice.service.MarketNewsProducer
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.kafka.core.KafkaTemplate
import kotlin.test.Test

@ExtendWith(MockitoExtension::class)
class MarketNewsProducerTest {

  @Mock
  private lateinit var kafkaTemplate: KafkaTemplate<String, MarketNewsList>

  @InjectMocks
  private lateinit var marketNewsProducer: MarketNewsProducer

  @Test
  fun `should send market news to Kafka`() {
    // Arrange
    val topic = "market_news_topic"
    val key = "news_key"
    val marketNewsList = MarketNewsList.newBuilder().build()

    // Act
    marketNewsProducer.sendMarketNews(marketNewsList)

    // Assert
    verify(kafkaTemplate).send(topic, key, marketNewsList)
  }
}
