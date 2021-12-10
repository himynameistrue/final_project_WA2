package com.group1.orchestrator.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfig {
    @Bean
    @Qualifier("wallet")
    fun paymentClient(@Value("\${service.endpoints.payment}") endpoint: String): WebClient {
        return WebClient.builder()
            .baseUrl(endpoint)
            .build()
    }

    @Bean
    @Qualifier("warehouse")
    fun inventoryClient(@Value("\${service.endpoints.inventory}") endpoint: String): WebClient {
        return WebClient.builder()
            .baseUrl(endpoint)
            .build()
    }
}