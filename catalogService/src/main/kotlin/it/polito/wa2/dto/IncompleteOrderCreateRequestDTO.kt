package it.polito.wa2.dto

import javax.validation.Valid
import javax.validation.constraints.NotNull

/**
 * Represents an Order without the buyerId
 */
data class IncompleteOrderCreateRequestDTO(
    @NotNull val totalPrice: Float,
    @Valid @NotNull val items: List<OrderCreateRequestProductDTO>
)