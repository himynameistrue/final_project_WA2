package it.polito.wa2.dto

import java.util.UUID

data class WarehouseRequestDTO (
    val userId: Int,
    val productId: Int,
    val orderId: UUID
)