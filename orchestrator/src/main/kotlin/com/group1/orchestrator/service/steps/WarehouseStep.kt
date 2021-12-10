package com.group1.orchestrator.service.steps

import com.group1.orchestrator.dto.WarehouseRequestDTO
import com.group1.orchestrator.dto.WarehouseResponseDTO
import com.group1.orchestrator.enums.WarehouseStatus
import com.group1.orchestrator.service.WorkflowStep
import com.group1.orchestrator.service.WorkflowStepStatus
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

class WarehouseStep(webClient: WebClient, requestDTO: WarehouseRequestDTO) : WorkflowStep {
    private val webClient: WebClient
    private val requestDTO: WarehouseRequestDTO

    override var status: WorkflowStepStatus = WorkflowStepStatus.PENDING

    override fun process(): Mono<Boolean> {
        return webClient
            .post()
            .uri("/inventory/deduct")
            .body(BodyInserters.fromValue(requestDTO))
            .retrieve()
            .bodyToMono(WarehouseResponseDTO::class.java)
            .map { r -> r.status == WarehouseStatus.AVAILABLE }
            .doOnNext { b -> status = if (b) WorkflowStepStatus.COMPLETE else WorkflowStepStatus.FAILED }
    }

    override fun revert(): Mono<Boolean> {
        return webClient
            .post()
            .uri("/inventory/add")
            .body(BodyInserters.fromValue(requestDTO))
            .retrieve()
            .bodyToMono(Void::class.java)
            .map { r -> true }
            .onErrorReturn(false)
    }

    init {
        this.webClient = webClient
        this.requestDTO = requestDTO
    }
}