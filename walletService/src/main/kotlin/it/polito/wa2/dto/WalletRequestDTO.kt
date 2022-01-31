package it.polito.wa2.dto

import java.util.UUID

data class WalletRequestDTO (
    val userId: Int,
    val orderId: UUID,
    val amount: Double,
)