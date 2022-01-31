package it.polito.wa2.dto

import javax.validation.Valid
import javax.validation.constraints.NotNull

data class OrderCreateWarehouseRequestDTO(
    @NotNull val amount: Float,
    @Valid @NotNull val items: List<OrderCreateRequestProductDTO>
)