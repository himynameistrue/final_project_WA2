package it.polito.wa2.dto

import it.polito.wa2.enums.PaymentStatus
import java.util.UUID

data class WalletResponseDTO (
    val userId: Int,
    val orderId: UUID,
    val amount: Double,
    val status: PaymentStatus
)