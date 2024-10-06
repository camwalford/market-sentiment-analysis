package me.camwalford.finnhubingestionservice.config


import jakarta.validation.constraints.NotBlank
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.validation.annotation.Validated


@Configuration
@ConfigurationProperties("finnhub.api")
@Validated
data class FinnHubApiConfig(
    @field:NotBlank("The finnhub api key must not be blank.")
    var apiKey: String = ""
)
