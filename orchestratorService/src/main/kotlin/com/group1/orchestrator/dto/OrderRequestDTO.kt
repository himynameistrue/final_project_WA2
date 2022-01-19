package com.group1.orchestrator.dto
import java.util.UUID

data class OrderRequestDTO (
    val userId: Int,
    val productId: Int,
    val orderId: UUID
)