package it.polito.wa2.dto

import javax.validation.Valid
import javax.validation.constraints.NotNull

/**
 * Represents an Order as received from an HTTPRequest
 */
data class OrderCreateRequestDTO(
    @NotNull val buyerId: Long,
    @NotNull val amount: Float,
    @Valid @NotNull val items: List<OrderCreateRequestProductDTO>
)