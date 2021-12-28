package com.group1.dto

import com.group1.enums.PaymentStatus
import java.util.UUID

data class WalletResponseDTO (
    val userId: Int,
    val orderId: UUID,
    val amount: Double,
    val status: PaymentStatus
)