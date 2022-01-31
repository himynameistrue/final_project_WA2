package it.polito.wa2.dto
import javax.validation.constraints.NotNull

data class OrderCreateWalletResponseDTO (
    @NotNull val wasCharged: Boolean
)