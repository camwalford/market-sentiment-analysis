import io.finnhub.api.apis.DefaultApi
import io.finnhub.api.models.MarketNews // Assuming this is the correct package
import me.camwalford.finnhubingestionservice.client.FinnHubClient
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import kotlin.test.assertNotNull

@ExtendWith(MockitoExtension::class)
class FinnHubClientTest {

  @Mock
  private lateinit var apiClient: DefaultApi

  @InjectMocks
  private lateinit var finnHubClient: FinnHubClient

  @Test
  fun `should fetch market news and return a list of MarketNews objects`() {
    // Arrange
    val category = "general"
    val minId: Long = 1
    // Create a MarketNews object with sample values
    val apiResponse = listOf(
      MarketNews(
      category = category,
      datetime = System.currentTimeMillis(),
      headline = "Sample Headline",
      id = 1,
      image = "https://sampleimage.com/image.jpg",
      related = "AAPL,GOOG",
      source = "News Source",
      summary = "This is a summary of the news article.",
      url = "https://sampleurl.com/article"
    ),
      MarketNews(
      category = category,
      datetime = System.currentTimeMillis(),
      headline = "Sample Headline",
      id = 1,
      image = "https://sampleimage.com/image.jpg",
      related = "MSFT",
      source = "News Source 2",
      summary = "This is a summary of the news article.",
      url = "https://sampleurl.com/article"
      )
    )

    `when`(apiClient.marketNews(category, minId)).thenReturn(apiResponse)

    // Act
    val result = finnHubClient.getMarketNewsList(category, minId)

    // Assert
    assertNotNull(result)
    verify(apiClient).marketNews(category, minId)
  }
}