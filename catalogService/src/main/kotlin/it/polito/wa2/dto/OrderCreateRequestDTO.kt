package it.polito.wa2.dto

import java.util.UUID

data class OrderCreateRequestDTO(
    val userId: Int,
    val productId: Int,
    val orderId: UUID,
    val amount: Double
)