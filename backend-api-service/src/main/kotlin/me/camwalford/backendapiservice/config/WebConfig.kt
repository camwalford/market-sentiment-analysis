package me.camwalford.backendapiservice.config

import me.camwalford.backendapiservice.interceptor.RequestCountingInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(
    private val corsProperties: CorsProperties,
    private val requestCountingInterceptor: RequestCountingInterceptor
) : WebMvcConfigurer {
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**").allowedOrigins(*corsProperties.allowedOrigins.toTypedArray()).allowCredentials(true)
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(requestCountingInterceptor)
    }
}