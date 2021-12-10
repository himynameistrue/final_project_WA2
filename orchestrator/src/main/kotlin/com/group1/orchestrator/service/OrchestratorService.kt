package com.group1.orchestrator.service


import com.vinsguru.dto.InventoryRequestDTO
import com.vinsguru.dto.OrchestratorRequestDTO
import com.vinsguru.dto.OrchestratorResponseDTO
import com.vinsguru.dto.PaymentRequestDTO
import com.vinsguru.enums.OrderStatus
import com.vinsguru.saga.service.steps.InventoryStep
import com.vinsguru.saga.service.steps.PaymentStep
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.List

@Service
class OrchestratorService {
    @Autowired
    @Qualifier("payment")
    private val paymentClient: WebClient? = null

    @Autowired
    @Qualifier("inventory")
    private val inventoryClient: WebClient? = null
    fun orderProduct(requestDTO: OrchestratorRequestDTO): Mono<OrchestratorResponseDTO> {
        val orderWorkflow: Workflow = getOrderWorkflow(requestDTO)
        return Flux.fromStream { orderWorkflow.getSteps().stream() }
            .flatMap(WorkflowStep::process)
            .handle { aBoolean, synchronousSink ->
                if (aBoolean) synchronousSink.next(true) else synchronousSink.error(
                    WorkflowException("create order failed!")
                )
            }
            .then(Mono.fromCallable { getResponseDTO(requestDTO, OrderStatus.ORDER_COMPLETED) })
            .onErrorResume { ex -> revertOrder(orderWorkflow, requestDTO) }
    }

    private fun revertOrder(workflow: Workflow, requestDTO: OrchestratorRequestDTO): Mono<OrchestratorResponseDTO> {
        return Flux.fromStream { workflow.getSteps().stream() }
            .filter { wf -> wf.getStatus().equals(WorkflowStepStatus.COMPLETE) }
            .flatMap(WorkflowStep::revert)
            .retry(3)
            .then(Mono.just(getResponseDTO(requestDTO, OrderStatus.ORDER_CANCELLED)))
    }

    private fun getOrderWorkflow(requestDTO: OrchestratorRequestDTO): Workflow {
        val paymentStep: WorkflowStep = PaymentStep(paymentClient, getPaymentRequestDTO(requestDTO))
        val inventoryStep: WorkflowStep = InventoryStep(inventoryClient, getInventoryRequestDTO(requestDTO))
        return OrderWorkflow(List.of(paymentStep, inventoryStep))
    }

    private fun getResponseDTO(requestDTO: OrchestratorRequestDTO, status: OrderStatus): OrchestratorResponseDTO {
        val responseDTO = OrchestratorResponseDTO()
        responseDTO.setOrderId(requestDTO.getOrderId())
        responseDTO.setAmount(requestDTO.getAmount())
        responseDTO.setProductId(requestDTO.getProductId())
        responseDTO.setUserId(requestDTO.getUserId())
        responseDTO.setStatus(status)
        return responseDTO
    }

    private fun getPaymentRequestDTO(requestDTO: OrchestratorRequestDTO): PaymentRequestDTO {
        val paymentRequestDTO = PaymentRequestDTO()
        paymentRequestDTO.setUserId(requestDTO.getUserId())
        paymentRequestDTO.setAmount(requestDTO.getAmount())
        paymentRequestDTO.setOrderId(requestDTO.getOrderId())
        return paymentRequestDTO
    }

    private fun getInventoryRequestDTO(requestDTO: OrchestratorRequestDTO): InventoryRequestDTO {
        val inventoryRequestDTO = InventoryRequestDTO()
        inventoryRequestDTO.setUserId(requestDTO.getUserId())
        inventoryRequestDTO.setProductId(requestDTO.getProductId())
        inventoryRequestDTO.setOrderId(requestDTO.getOrderId())
        return inventoryRequestDTO
    }
}