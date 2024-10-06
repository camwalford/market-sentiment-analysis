package me.camwalford.xingestionservice.config


import jakarta.validation.constraints.NotBlank
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration


@Configuration
@ConfigurationProperties("x.api")
data class XApiConfig(
    @field:NotBlank("The Xapi base url must not be blank.")
    var baseUrl: String = "",

    @field:NotBlank("The Xapi bearer token must not be blank.")
    var bearerToken: String = ""
)
