package it.polito.wa2.orchestrator.controllers

import it.polito.wa2.dto.*
import it.polito.wa2.orchestrator.services.OrchestratorService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
class OrchestratorController(
    val orchestratorService: OrchestratorService
) {

    @PostMapping("/orders")
    fun createOrder(@Valid @RequestBody requestDTO: OrderCreateOrchestratorRequestDTO): OrderCreateOrchestratorResponseDTO {
        println("Received request")
        println(requestDTO)

        val walletRequestDTO = TransactionRequestDTO(
            requestDTO.orderId,
            requestDTO.buyerId,
            -requestDTO.totalPrice
        )

        val warehouseRequestDTO = InventoryChangeRequestDTO(
            requestDTO.totalPrice,
            requestDTO.items
        )

        return orchestratorService.runCreationSaga(requestDTO.buyerId, walletRequestDTO, warehouseRequestDTO)
    }

    @DeleteMapping("/orders/{orderID}")
    fun cancelOrder(@Valid @RequestBody requestDTO: OrderDeleteOrchestratorRequestDTO) {
        println("Received request")
        println(requestDTO)

        val walletRequestDTO = TransactionRequestDTO(
            requestDTO.orderId,
            requestDTO.buyerId,
            requestDTO.totalPrice
        )

        val warehouseRequestDTO = InventoryChangeRequestDTO(
            requestDTO.totalPrice,
            requestDTO.items
        )

        return orchestratorService.runDeletionSaga(requestDTO.buyerId, walletRequestDTO, warehouseRequestDTO)
    }
}