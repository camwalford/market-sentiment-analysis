package me.camwalford.finnhubingestionservice.controller

//@WebMvcTest(FinnHubController::class)
//class FinnHubControllerTest {
//
//  @Autowired
//  private lateinit var mockMvc: MockMvc
//
//  @MockBean
//  private lateinit var finnHubService: FinnHubService
//
//  @Test
//  fun `should return 200 OK for valid market news request`() {
//    // Arrange
//    val request = MarketNewsRequest("general", 1L)
//    val marketNewsList = MarketNewsList.newBuilder().build()
//    `when`(finnHubService.fetchMarketNewsList(request.category, request.minId)).thenReturn(marketNewsList)
//
//    // Act & Assert
//    mockMvc.perform(
//      post("/finnhub/market-news")
//        .contentType(MediaType.APPLICATION_JSON)
//        .content("""{"category":"general","minId":1}""")
//    )
//      .andExpect(status().isOk)
//      .andExpect(content().contentType("application/x-protobuf"))
//  }
//}
