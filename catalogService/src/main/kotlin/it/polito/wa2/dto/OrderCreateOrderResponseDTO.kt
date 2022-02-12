package it.polito.wa2.dto

import javax.validation.Valid
import javax.validation.constraints.NotNull

/**
 * Represents an Order after being created by orderService
 */
data class OrderCreateOrderResponseDTO(
    @NotNull val isSuccessful: Boolean,
    @Valid val warehousesUnderThresholdById: List<OrderCreateOrderResponseUnderThresholdDTO>,
    @Valid @NotNull val products: List<OrderCreateOrderResponseProductDTO>
)