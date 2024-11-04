package me.camwalford.finnhubingestionservice.service

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import me.camwalford.finnhubingestionservice.model.protobuf.MarketNewsList
import me.camwalford.finnhubingestionservice.retrycontext.RetryContextSupport
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.retry.RetryCallback

import org.springframework.retry.support.RetryTemplate



class MarketNewsSchedulerTest {

    @MockK
    private lateinit var finnHubService: FinnHubService

    @MockK
    private lateinit var companyNewsProducer: CompanyNewsProducer

    @MockK
    private lateinit var retryTemplate: RetryTemplate

    private lateinit var companyNewsScheduler: CompanyNewsScheduler

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        companyNewsScheduler = CompanyNewsScheduler(finnHubService, companyNewsProducer, retryTemplate)
    }

    @Test
    fun `test fetchAndSendMarketNews successfully`() {
        // Arrange
        val marketNewsList = MarketNewsList.newBuilder().build()
        val callbackSlot = slot<RetryCallback<Unit, Exception>>()
        every { retryTemplate.execute(capture(callbackSlot)) } answers {
            callbackSlot.captured.doWithRetry(RetryContextSupport())
        }
        every { finnHubService.fetchMarketNewsList() } returns marketNewsList
        every { companyNewsProducer.sendMarketNews(marketNewsList) } returns Unit

        // Act
        companyNewsScheduler.fetchAndSendMarketNews()

        // Assert
        verify { finnHubService.fetchMarketNewsList() }
        verify { companyNewsProducer.sendMarketNews(marketNewsList) }
        verify { retryTemplate.execute(any<RetryCallback<Unit, Exception>>()) }
    }

    @Test
    fun `test fetchAndSendMarketNews when FinnHubService throws exception`() {
        // Arrange
        val callbackSlot = slot<RetryCallback<Unit, Exception>>()
        every { retryTemplate.execute(capture(callbackSlot)) } answers {
            try {
                callbackSlot.captured.doWithRetry(RetryContextSupport())
            } catch (e: Exception) {
                // Simulate retry behavior by rethrowing the exception
                throw e
            }
        }
        every { finnHubService.fetchMarketNewsList() } throws RuntimeException("Service error")

        // Act & Assert
        try {
            companyNewsScheduler.fetchAndSendMarketNews()
        } catch (e: Exception) {
            // Exception is rethrown after retries are exhausted
        }

        // Assert
        verify { finnHubService.fetchMarketNewsList() }
        verify { retryTemplate.execute(any<RetryCallback<Unit, Exception>>()) }
    }

    @Test
    fun `test fetchAndSendMarketNews when MarketNewsProducer throws exception`() {
        // Arrange
        val marketNewsList = MarketNewsList.newBuilder().build()
        val callbackSlot = slot<RetryCallback<Unit, Exception>>()
        every { retryTemplate.execute(capture(callbackSlot)) } answers {
            try {
                callbackSlot.captured.doWithRetry(RetryContextSupport())
            } catch (e: Exception) {
                // Simulate retry behavior by rethrowing the exception
                throw e
            }
        }
        every { finnHubService.fetchMarketNewsList() } returns marketNewsList
        every { companyNewsProducer.sendMarketNews(marketNewsList) } throws RuntimeException("Producer error")

        // Act & Assert
        try {
            companyNewsScheduler.fetchAndSendMarketNews()
        } catch (e: Exception) {
            // Exception is rethrown after retries are exhausted
        }

        // Assert
        verify { finnHubService.fetchMarketNewsList() }
        verify { companyNewsProducer.sendMarketNews(marketNewsList) }
        verify { retryTemplate.execute(any<RetryCallback<Unit, Exception>>()) }
    }

}
