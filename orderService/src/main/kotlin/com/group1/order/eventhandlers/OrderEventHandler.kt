package com.group1.order.eventhandlers

import com.group1.order.dto.OrchestratorRequestDTO
import com.group1.order.dto.OrchestratorResponseDTO
import com.group1.order.service.OrderEventUpdateService
import org.reactivestreams.Publisher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.Flux
import java.util.function.Consumer
import java.util.function.Supplier

@Configuration
class OrderEventHandler(
    val source: Publisher<OrchestratorRequestDTO>,
    val service: OrderEventUpdateService) {

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