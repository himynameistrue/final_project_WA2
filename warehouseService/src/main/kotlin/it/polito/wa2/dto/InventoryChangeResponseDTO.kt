package it.polito.wa2.dto

import javax.validation.Valid
import javax.validation.constraints.NotNull

data class InventoryChangeResponseDTO(
    @NotNull val isConfirmed: Boolean,
    @Valid @NotNull val items: List<InventoryChangeResponseProductDTO>,
)