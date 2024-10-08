//package me.camwalford.finnhubingestionservice.util
//
//import me.camwalford.finnhubingestionservice.model.protobuf.MarketNewsList
//import org.junit.jupiter.api.Assertions.*
//import org.junit.jupiter.api.Test
//
//class ProtobufConversionUtilTest {
//
//    @Test
//    fun `test convertMarketNewsList with valid JSON`() {
//        // Arrange
//        val jsonNewsList = """
//            [
//                {
//                    "category": "general",
//                    "headline": "Sample News",
//                    "source": "News Source",
//                    "summary": "This is a summary",
//                    "url": "http://example.com/news",
//                    "related": "AAPL",
//                    "datetime": 1609459200,
//                    "image": "http://example.com/image.jpg"
//                }
//            ]
//        """.trimIndent()
//
//        // Act
//        val result = ProtobufConversionUtil.convertMarketNewsList(jsonNewsList)
//
//        // Assert
//        assertNotNull(result)
//        assertEquals(1, result.dataCount)
//        val news = result.getData(0)
//        assertEquals("general", news.category)
//        assertEquals("Sample News", news.headline)
//        assertEquals("News Source", news.source)
//        assertEquals("This is a summary", news.summary)
//        assertEquals("http://example.com/news", news.url)
//        assertEquals("AAPL", news.related)
//        assertEquals(1609459200L, news.datetime)
//        assertEquals("http://example.com/image.jpg", news.image)
//    }
//
//    @Test
//    fun `test convertMarketNewsList with invalid JSON`() {
//        // Arrange
//        val invalidJson = "{ invalid json }"
//
//        // Act & Assert
//        val exception = assertThrows<RuntimeException>(RuntimeException::class.java) {
//            ProtobufConversionUtil.convertMarketNewsList(invalidJson)
//        }
//        assertEquals("Error parsing JSON to Protobuf", exception.message)
//    }
//}
