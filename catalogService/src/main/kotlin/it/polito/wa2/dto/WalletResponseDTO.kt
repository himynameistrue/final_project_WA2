package it.polito.wa2.dto

import it.polito.wa2.enums.PaymentStatus

data class WalletResponseDTO(
    val userId: Long,
    val orderId: Long?,
    val amount: Float,
    val status: PaymentStatus
)