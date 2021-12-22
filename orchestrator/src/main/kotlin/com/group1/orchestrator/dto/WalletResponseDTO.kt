package com.group1.orchestrator.dto
import com.group1.orchestrator.enums.PaymentStatus
import java.util.UUID

data class WalletResponseDTO (
    val userId: Int,
    val orderId: UUID,
    val amount: Double,
    val status: PaymentStatus
)