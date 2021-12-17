package com.wa2.finalproject.warehouse.dto

import java.util.UUID

data class WarehouseRequestDTO (
    val userId: Int,
    val productId: Int,
    val orderId: UUID
)