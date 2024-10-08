package me.camwalford.finnhubingestionservice.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.retry.policy.SimpleRetryPolicy
import org.springframework.retry.support.RetryTemplate


@Configuration
class RetryConfig {

    @Bean
    fun retryTemplate(): RetryTemplate {
        val retryTemplate = RetryTemplate()
        val retryPolicy = SimpleRetryPolicy()
        retryPolicy.maxAttempts = 3
        retryTemplate.setRetryPolicy(retryPolicy)
        return retryTemplate

    }
}