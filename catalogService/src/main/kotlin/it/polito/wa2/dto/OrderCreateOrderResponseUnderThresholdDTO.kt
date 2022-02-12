package it.polito.wa2.dto

import javax.validation.Valid
import javax.validation.constraints.NotNull

/**
 * Represents a Warehouse having products under threshold after an order being created by orderService
 */
data class OrderCreateOrderResponseUnderThresholdDTO(
    @NotNull val warehouseId: Long,
    @NotNull val warehouseName: String,
    @Valid @NotNull val items: MutableList<OrderCreateOrderResponseUnderThresholdProductDTO>
)