package com.group1.order.eventhandlers

import com.vinsguru.dto.OrchestratorRequestDTO
import com.vinsguru.dto.OrchestratorResponseDTO
import com.vinsguru.order.service.OrderEventUpdateService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.DirectProcessor
import reactor.core.publisher.Flux
import java.util.function.Consumer
import java.util.function.Supplier

@Configuration
class OrderEventHandler {
    @Autowired
    private val source: DirectProcessor<OrchestratorRequestDTO>? = null

    @Autowired
    private val service: OrderEventUpdateService? = null
    @Bean
    fun supplier(): Supplier<Flux<OrchestratorRequestDTO>> {
        return Supplier<Flux<OrchestratorRequestDTO>> { Flux.from(source) }
    }

    @Bean
    fun consumer(): Consumer<Flux<OrchestratorResponseDTO>> {
        return Consumer<Flux<OrchestratorResponseDTO>> { flux ->
            flux
                .subscribe { responseDTO -> service.updateOrder(responseDTO) }
        }
    }
}