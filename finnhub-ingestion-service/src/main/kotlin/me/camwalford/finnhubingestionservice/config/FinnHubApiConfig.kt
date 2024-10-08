package me.camwalford.finnhubingestionservice.config


import jakarta.validation.constraints.NotBlank
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.validation.annotation.Validated


@Configuration
@ConfigurationProperties("finnhub.api")
@Validated
data class FinnHubApiConfig(
    @NotBlank
    var apiKey: String = ""
)
