package com.group1.orchestrator.dto
import com.group1.enums.OrderStatus
import java.util.UUID

data class OrderResponseDTO (
    val orderId: UUID,
    val userId: Int,
    val productId: Int,
    val amount: Double,
    val status: OrderStatus,
)