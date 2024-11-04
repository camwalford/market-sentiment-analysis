//package me.camwalford.finnhubingestionservice.service
//
//import io.finnhub.api.models.MarketNews
//import io.mockk.MockKAnnotations
//import io.mockk.every
//import io.mockk.impl.annotations.MockK
//import io.mockk.mockkObject
//import io.mockk.verify
//import me.camwalford.finnhubingestionservice.client.FinnHubClient
//import me.camwalford.finnhubingestionservice.model.protobuf.MarketNewsList
//import me.camwalford.finnhubingestionservice.util.ProtobufConversionUtil
//import org.junit.jupiter.api.Assertions.*
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//
//class FinnHubServiceTest {
//
//    @MockK
//    private lateinit var finnHubClient: FinnHubClient
//
//    private lateinit var finnHubService: FinnHubService
//
//    @BeforeEach
//    fun setUp() {
//        MockKAnnotations.init(this)
//        finnHubService = FinnHubService(finnHubClient)
//        mockkObject(ProtobufConversionUtil)
//    }
//
//    @Test
//    fun `test fetchMarketNewsList successfully`() {
//        // Arrange
//        val category = "general"
//        val minId: Long? = 0L
//        val marketNewsJson = listOf(MarketNews())
//        val protobufData = MarketNewsList.newBuilder().build()
//
//        every { finnHubClient.getMarketNewsList(category, minId) } returns marketNewsJson
//        every { ProtobufConversionUtil.convertMarketNewsList(any()) } returns protobufData
//
//        // Act
//        val result = finnHubService.fetchMarketNewsList(category, minId)
//
//        // Assert
//        assertEquals(protobufData, result)
//        verify { finnHubClient.getMarketNewsList(category, minId) }
//        verify { ProtobufConversionUtil.convertMarketNewsList(any()) }
//    }
//
//    @Test
//    fun `test fetchMarketNewsList throws exception when ProtobufConversionUtil fails`() {
//        // Arrange
//        val category = "general"
//        val minId: Long? = 0L
//        val marketNewsJson = listOf(MarketNews())
//
//        every { finnHubClient.getMarketNewsList(category, minId) } returns marketNewsJson
//        every { ProtobufConversionUtil.convertMarketNewsList(any()) } throws RuntimeException("Conversion error")
//
//        // Act & Assert
//        val exception = assertThrows<RuntimeException>(RuntimeException::class.java) {
//            finnHubService.fetchMarketNewsList(category, minId)
//        }
//        assertEquals("Failed to convert JSON to Protobuf", exception.message)
//        verify { finnHubClient.getMarketNewsList(category, minId) }
//        verify { ProtobufConversionUtil.convertMarketNewsList(any()) }
//    }
//
//    @Test
//    fun `test fetchMarketNewsList throws exception when FinnHubClient fails`() {
//        // Arrange
//        val category = "general"
//        val minId: Long? = 0L
//
//        every { finnHubClient.getMarketNewsList(category, minId) } throws RuntimeException("API error")
//
//        // Act & Assert
//        assertThrows<RuntimeException>(RuntimeException::class.java) {
//            finnHubService.fetchMarketNewsList(category, minId)
//        }
//        verify { finnHubClient.getMarketNewsList(category, minId) }
//    }
//}
