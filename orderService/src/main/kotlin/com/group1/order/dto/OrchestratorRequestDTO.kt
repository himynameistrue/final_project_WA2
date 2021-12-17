package com.group1.order.dto

import java.util.UUID

data class OrchestratorRequestDTO (
    val userId: Int,
    val productId: Int,
    val orderId: UUID,
    val amount: Double
)