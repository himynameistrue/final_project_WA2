package com.group1.order.config

import com.vinsguru.dto.OrchestratorRequestDTO
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.DirectProcessor
import reactor.core.publisher.FluxSink

@Configuration
class OrderConfig {
    @Bean
    fun publisher(): DirectProcessor<OrchestratorRequestDTO> {
        return DirectProcessor.create()
    }

    @Bean
    fun sink(publisher: DirectProcessor<OrchestratorRequestDTO?>): FluxSink<OrchestratorRequestDTO> {
        return publisher.sink()
    }
}