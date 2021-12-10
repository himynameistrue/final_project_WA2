package com.group1.orchestrator.service.steps

import com.vinsguru.dto.PaymentRequestDTO
import com.vinsguru.dto.PaymentResponseDTO
import com.vinsguru.enums.PaymentStatus
import com.vinsguru.saga.service.WorkflowStep
import com.vinsguru.saga.service.WorkflowStepStatus
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

class PaymentStep(webClient: WebClient, requestDTO: PaymentRequestDTO) : WorkflowStep {
    private val webClient: WebClient
    private val requestDTO: PaymentRequestDTO
    private var stepStatus: WorkflowStepStatus = WorkflowStepStatus.PENDING

    @get:Override
    val status: WorkflowStepStatus
        get() = stepStatus

    @Override
    fun process(): Mono<Boolean> {
        return webClient
            .post()
            .uri("/payment/debit")
            .body(BodyInserters.fromValue(requestDTO))
            .retrieve()
            .bodyToMono(PaymentResponseDTO::class.java)
            .map { r -> r.getStatus().equals(PaymentStatus.PAYMENT_APPROVED) }
            .doOnNext { b -> stepStatus = if (b) WorkflowStepStatus.COMPLETE else WorkflowStepStatus.FAILED }
    }

    @Override
    fun revert(): Mono<Boolean> {
        return webClient
            .post()
            .uri("/payment/credit")
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