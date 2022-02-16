package it.polito.wa2.dto
import javax.validation.constraints.NotNull

data class TransactionResponseDTO (
    @NotNull val wasCharged: Boolean,
    val transactionId: Long?
)