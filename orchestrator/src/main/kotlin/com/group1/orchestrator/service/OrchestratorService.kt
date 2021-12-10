package com.group1.orchestrator.service


import com.group1.orchestrator.dto.OrchestratorRequestDTO
import com.group1.orchestrator.dto.OrchestratorResponseDTO
import com.group1.orchestrator.dto.WalletRequestDTO
import com.group1.orchestrator.dto.WarehouseRequestDTO
import com.group1.orchestrator.enums.OrderStatus
import com.group1.orchestrator.service.steps.WalletStep
import com.group1.orchestrator.service.steps.WarehouseStep
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class OrchestratorService {
    @Autowired
    @Qualifier("wallet")
    private lateinit var walletClient: WebClient

    @Autowired
    @Qualifier("warehouse")
    private lateinit var warehouseClient: WebClient

    fun orderProduct(requestDTO: OrchestratorRequestDTO): Mono<OrchestratorResponseDTO> {
        val orderWorkflow: Workflow = getOrderWorkflow(requestDTO)
        return Flux.fromStream { orderWorkflow.steps.stream() }
            .flatMap{it -> it.process()}
            .handle { aBoolean, synchronousSink ->
                if (aBoolean) synchronousSink.next(true) else synchronousSink.error(
                    WorkflowException("create order failed!")
                )
            }
            .then(Mono.fromCallable { getResponseDTO(requestDTO, OrderStatus.ORDER_COMPLETED) })
            .onErrorResume { ex -> revertOrder(orderWorkflow, requestDTO) }
    }

    private fun revertOrder(workflow: Workflow, requestDTO: OrchestratorRequestDTO): Mono<OrchestratorResponseDTO> {
        return Flux.fromStream { workflow.steps.stream() }
            .filter { wf -> wf.status.equals(WorkflowStepStatus.COMPLETE) }
            .flatMap(WorkflowStep::revert)
            .retry(3)
            .then(Mono.just(getResponseDTO(requestDTO, OrderStatus.ORDER_CANCELLED)))
    }

    private fun getOrderWorkflow(requestDTO: OrchestratorRequestDTO): Workflow {
        val walletStep: WorkflowStep = WalletStep(walletClient, getPaymentRequestDTO(requestDTO))
        val warehouseStep: WorkflowStep = WarehouseStep(warehouseClient, getInventoryRequestDTO(requestDTO))
        return OrderWorkflow(listOf(walletStep, warehouseStep))
    }

    private fun getResponseDTO(requestDTO: OrchestratorRequestDTO, status: OrderStatus): OrchestratorResponseDTO {
        val responseDTO = OrchestratorResponseDTO(
            requestDTO.userId,
                requestDTO.productId,
                requestDTO.orderId,
                requestDTO.amount,
                status
        )
        return responseDTO
    }

    private fun getPaymentRequestDTO(requestDTO: OrchestratorRequestDTO): WalletRequestDTO {
        val walletRequestDTO = WalletRequestDTO(
            requestDTO.userId,
            requestDTO.orderId,
            requestDTO.amount,
        )
        return walletRequestDTO
    }

    private fun getInventoryRequestDTO(requestDTO: OrchestratorRequestDTO): WarehouseRequestDTO {
        val warehouseRequestDTO = WarehouseRequestDTO(
            requestDTO.userId,
            requestDTO.productId,
            requestDTO.orderId,
        )
        return warehouseRequestDTO
    }
}