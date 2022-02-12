package it.polito.wa2.dto

import it.polito.wa2.enums.PaymentStatus
import java.util.UUID

data class WalletResponseDTO(
    val userId: Long,
    val orderId: UUID,
    val amount: Float,
    val status: PaymentStatus
)