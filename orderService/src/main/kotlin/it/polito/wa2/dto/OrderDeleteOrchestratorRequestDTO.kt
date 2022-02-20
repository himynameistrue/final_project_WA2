package it.polito.wa2.dto

import javax.validation.Valid
import javax.validation.constraints.NotNull

/**
 * Represents an Order as received from the OrderService
 */
data class OrderDeleteOrchestratorRequestDTO(
    @NotNull val orderId: Long,
    @NotNull val buyerId: Long,
    @Valid @NotNull val items: List<RequestOrderProductDTO>
)