package com.group1.order.dto

import lombok.Data
import java.util.UUID

@Data
class OrderRequestDTO {
    private val userId: Integer? = null
    private val productId: Integer? = null
    private val orderId: UUID? = null
}