package com.group1.orchestrator.service


import com.group1.dto.OrchestratorRequestDTO
import com.group1.dto.OrchestratorResponseDTO
import com.group1.dto.WalletRequestDTO
import com.group1.dto.WarehouseRequestDTO
import com.group1.enums.OrderStatus
import com.group1.orchestrator.service.steps.WalletStep
import com.group1.orchestrator.service.steps.WarehouseStep
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.SynchronousSink

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
        println("orderProduct")


        return Flux.fromStream { orderWorkflow.steps.stream() }
            .flatMap(WorkflowStep::process)
            .handle { aBoolean, synchronousSink : SynchronousSink<Boolean> ->
                if (aBoolean) synchronousSink.next(true) else synchronousSink.error(
                    WorkflowException("create order failed!")
                )

            }
            .then(Mono.fromCallable { getResponseDTO(requestDTO, OrderStatus.ORDER_COMPLETED) })
            /* TODO HANDLE via Debezium .onErrorResume { ex -> revertOrder(orderWorkflow, requestDTO) }*/
    }

  /*  private fun revertOrder(workflow: Workflow, requestDTO: OrchestratorRequestDTO): Mono<OrchestratorResponseDTO> {
        println("Reverting order")
        return Flux.fromStream { workflow.steps.stream() }
            .filter { wf -> wf.status.equals(WorkflowStepStatus.COMPLETE) }
            .flatMap(WorkflowStep::revert)
            .retry(3)
            .then(Mono.just(getResponseDTO(requestDTO, OrderStatus.ORDER_CANCELLED)))
    }*/

    private fun getOrderWorkflow(requestDTO: OrchestratorRequestDTO): Workflow {
        val walletStep: WorkflowStep = WalletStep(walletClient, getPaymentRequestDTO(requestDTO))
        val warehouseStep: WorkflowStep = WarehouseStep(warehouseClient, getInventoryRequestDTO(requestDTO))
        return OrderWorkflow(listOf(walletStep, warehouseStep))
    }

    private fun getResponseDTO(requestDTO: OrchestratorRequestDTO, status: OrderStatus): OrchestratorResponseDTO {
        return OrchestratorResponseDTO(
            requestDTO.userId,
            requestDTO.productId,
            requestDTO.orderId,
            requestDTO.amount,
            status
        )
    }

    private fun getPaymentRequestDTO(requestDTO: OrchestratorRequestDTO): WalletRequestDTO {
        return WalletRequestDTO(
            requestDTO.userId,
            requestDTO.orderId,
            requestDTO.amount,
        )
    }

    private fun getInventoryRequestDTO(requestDTO: OrchestratorRequestDTO): WarehouseRequestDTO {
        return WarehouseRequestDTO(
            requestDTO.userId,
            requestDTO.productId,
            requestDTO.orderId,
        )
    }
}