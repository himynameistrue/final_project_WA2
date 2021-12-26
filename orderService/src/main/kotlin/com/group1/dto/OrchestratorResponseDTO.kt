package com.group1.dto

import com.group1.enums.OrderStatus
import java.util.UUID

data class OrchestratorResponseDTO (
    val userId: Int,
    val productId: Int,
    val orderId: UUID,
    val amount: Double,
    val status: OrderStatus
)