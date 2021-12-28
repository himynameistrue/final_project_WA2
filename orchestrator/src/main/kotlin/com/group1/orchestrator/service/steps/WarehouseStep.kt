package com.group1.orchestrator.service.steps

import com.group1.dto.WarehouseRequestDTO
import com.group1.dto.WarehouseResponseDTO
import com.group1.enums.InventoryStatus
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
        println("warehouseStep process")

        return webClient
            .post()
            .uri("/inventory/deduct")
            .body(BodyInserters.fromValue(requestDTO))
            .retrieve()
            .bodyToMono(WarehouseResponseDTO::class.java)
            .doOnNext { r -> println(r.toString()) }
            .map { r -> r.status == InventoryStatus.AVAILABLE }
            .doOnNext { b -> status = if (b) WorkflowStepStatus.COMPLETE else WorkflowStepStatus.FAILED }
    }

    override fun revert(): Mono<Boolean> {
        println("warehouseStep revert")

        return webClient
            .post()
            .uri("/inventory/add")
            .body(BodyInserters.fromValue(requestDTO))
            .retrieve()
            .bodyToMono(Void::class.java)
            .doOnNext { r -> println(r.toString()) }
            .map { r -> true }
            .onErrorReturn(false)
    }

    init {
        this.webClient = webClient
        this.requestDTO = requestDTO
    }
}