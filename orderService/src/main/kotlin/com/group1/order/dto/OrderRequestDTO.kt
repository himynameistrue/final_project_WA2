package com.group1.order.dto

import java.util.UUID

data class OrderRequestDTO (
    val userId: Int,
    val productId: Int,
    var orderId: UUID?
)