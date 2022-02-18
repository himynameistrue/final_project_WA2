package it.polito.wa2.dto

import javax.validation.Valid
import javax.validation.constraints.NotNull

data class InventoryCancelOrderRequestDTO(
    @Valid @NotNull val items: List<RequestOrderProductDTO>
)