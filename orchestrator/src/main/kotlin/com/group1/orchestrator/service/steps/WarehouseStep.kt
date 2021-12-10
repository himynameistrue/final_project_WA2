package com.group1.orchestrator.service.steps

import com.vinsguru.dto.InventoryRequestDTO
import com.vinsguru.dto.InventoryResponseDTO
import com.vinsguru.enums.InventoryStatus
import com.vinsguru.saga.service.WorkflowStep
import com.vinsguru.saga.service.WorkflowStepStatus
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

class InventoryStep(webClient: WebClient, requestDTO: InventoryRequestDTO) : WorkflowStep {
    private val webClient: WebClient
    private val requestDTO: InventoryRequestDTO
    private var stepStatus: WorkflowStepStatus = WorkflowStepStatus.PENDING

    @get:Override
    val status: WorkflowStepStatus
        get() = stepStatus

    @Override
    fun process(): Mono<Boolean> {
        return webClient
            .post()
            .uri("/inventory/deduct")
            .body(BodyInserters.fromValue(requestDTO))
            .retrieve()
            .bodyToMono(InventoryResponseDTO::class.java)
            .map { r -> r.getStatus().equals(InventoryStatus.AVAILABLE) }
            .doOnNext { b -> stepStatus = if (b) WorkflowStepStatus.COMPLETE else WorkflowStepStatus.FAILED }
    }

    @Override
    fun revert(): Mono<Boolean> {
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