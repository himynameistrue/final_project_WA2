package it.polito.wa2.orchestrator.controllers

import it.polito.wa2.dto.OrderCreateOrchestratorRequestDTO
import it.polito.wa2.dto.OrderCreateOrchestratorResponseDTO
import it.polito.wa2.dto.OrderCreateRequestDTO
import it.polito.wa2.orchestrator.handlers.OrderCreatedEventHandler
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
class OrchestratorController(
    val orderCreatedEventHandler: OrderCreatedEventHandler
) {

    @PostMapping("/orchestrator")
    fun create(@Valid @RequestBody newOrderDTO: OrderCreateOrchestratorRequestDTO): OrderCreateOrchestratorResponseDTO {
        return orderCreatedEventHandler.consumer(newOrderDTO)
    }
}