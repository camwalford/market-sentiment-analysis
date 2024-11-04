//package me.camwalford.finnhubingestionservice.client
//
//import io.finnhub.api.apis.DefaultApi
//import io.finnhub.api.models.MarketNews
//import io.mockk.MockKAnnotations
//import io.mockk.every
//import io.mockk.impl.annotations.MockK
//import io.mockk.verify
//import org.junit.jupiter.api.Assertions.*
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//
//class FinnHubClientTest {
//
//  @MockK
//  private lateinit var apiClient: DefaultApi
//
//  private lateinit var finnHubClient: FinnHubClient
//
//  @BeforeEach
//  fun setUp() {
//    MockKAnnotations.init(this)
//    finnHubClient = FinnHubClient(apiClient)
//  }
//
//  @Test
//  fun `test getMarketNewsList returns data successfully`() {
//    // Arrange
//    val category = "general"
//    val minId: Long? = 0L
//    val marketNewsList = listOf(MarketNews())
//    every { apiClient.marketNews(category, minId) } returns marketNewsList
//
//    // Act
//    val result = finnHubClient.getMarketNewsList(category, minId)
//
//    // Assert
//    assertEquals(marketNewsList, result)
//    verify { apiClient.marketNews(category, minId) }
//  }
//
//  @Test
//  fun `test getMarketNewsList handles exception`() {
//    // Arrange
//    val category = "general"
//    val minId: Long? = 0L
//    every { apiClient.marketNews(category, minId) } throws RuntimeException("API error")
//
//    // Act & Assert
//    assertThrows(RuntimeException::class.java) {
//      finnHubClient.getMarketNewsList(category, minId)
//    }
//
//    verify { apiClient.marketNews(category, minId) }
//  }
//
//  @Test
//  fun `test getMarketNewsList with different category and minId`() {
//    // Arrange
//    val category = "technology"
//    val minId: Long? = 100L
//    val marketNewsList = listOf(MarketNews())
//    every { apiClient.marketNews(category, minId) } returns marketNewsList
//
//    // Act
//    val result = finnHubClient.getMarketNewsList(category, minId)
//
//    // Assert
//    assertEquals(marketNewsList, result)
//    verify { apiClient.marketNews(category, minId) }
//  }
//}
