package it.polito.wa2.dto

import javax.validation.Valid
import javax.validation.constraints.NotNull

data class OrderCreateWarehouseResponseDTO(
    @NotNull val isConfirmed: Boolean,
    @Valid @NotNull val items: List<OrderCreateWarehouseResponseProductDTO>
)