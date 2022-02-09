package it.polito.wa2.dto

import javax.validation.Valid
import javax.validation.constraints.NotNull

/**
 * Represents an Order after being validated by the orchestrator
 */
data class OrderCreateOrderResponseDTO(
    @NotNull val isSuccessful: Boolean,
    @Valid @NotNull val productsUnderThresholdByWarehouseId: Map<Long, List<OrderCreateOrderResponseProductDTO>>
)