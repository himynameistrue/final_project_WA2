package it.polito.wa2.dto

import it.polito.wa2.enums.OrderStatus
import javax.validation.constraints.NotNull

/**
 * Represents an Order as received from an HTTPRequest
 */
data class OrderUpdateRequestDTO(
    @NotNull val status: OrderStatus,
)