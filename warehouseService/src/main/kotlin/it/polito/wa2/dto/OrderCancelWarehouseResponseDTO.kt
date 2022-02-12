package it.polito.wa2.dto

import javax.validation.Valid
import javax.validation.constraints.NotNull

data class OrderCancelWarehouseResponseDTO(
    @NotNull val isCancelled: Boolean,
)