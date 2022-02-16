package it.polito.wa2.dto
import javax.validation.constraints.NotNull

data class TransactionRequestDTO (
    @NotNull val orderId: Long,
    @NotNull val buyerId: Long,
    @NotNull val amount: Float,
)