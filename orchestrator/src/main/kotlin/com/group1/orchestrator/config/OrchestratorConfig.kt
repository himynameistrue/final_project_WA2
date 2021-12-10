package com.group1.orchestrator.config

import com.group1.orchestrator.dto.OrchestratorRequestDTO
import com.group1.orchestrator.dto.OrchestratorResponseDTO
import com.group1.orchestrator.service.OrchestratorService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.Flux
import java.util.function.Function


@Configuration
class OrchestratorConfig {
    @Autowired
    private val orchestratorService: OrchestratorService? = null
    @Bean
    fun processor(): Function<Flux<OrchestratorRequestDTO>, Flux<OrchestratorResponseDTO>> {
        return Function<Flux<OrchestratorRequestDTO>, Flux<OrchestratorResponseDTO>> { flux ->
            flux
                .flatMap { dto -> orchestratorService!!.orderProduct(dto) }
                .doOnNext { dto -> System.out.println("Status : " + dto.status) }
        }
    }
}