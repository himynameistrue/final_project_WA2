package com.group1.orchestrator.service.steps

import com.group1.dto.WalletRequestDTO
import com.group1.dto.WalletResponseDTO
import com.group1.enums.PaymentStatus
import com.group1.orchestrator.service.WorkflowStep
import com.group1.orchestrator.service.WorkflowStepStatus
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

class WalletStep(webClient: WebClient, requestDTO: WalletRequestDTO) : WorkflowStep {
    private val webClient: WebClient
    private val requestDTO: WalletRequestDTO

    override var status: WorkflowStepStatus = WorkflowStepStatus.PENDING

    override fun process(): Mono<Boolean> {
        println("walletStep process")
        return webClient
            .post()
            .uri("/payment/debit")
            .body(BodyInserters.fromValue(requestDTO))
            .retrieve()
            .bodyToMono(WalletResponseDTO::class.java)
            .doOnNext { r -> println(r.toString()) }
            .map { r -> r.status == PaymentStatus.PAYMENT_APPROVED }
            .doOnNext { b -> status = if (b) WorkflowStepStatus.COMPLETE else WorkflowStepStatus.FAILED }
    }

    override fun revert(): Mono<Boolean> {
        println("walletStep revert")

        return webClient
            .post()
            .uri("/payment/credit")
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