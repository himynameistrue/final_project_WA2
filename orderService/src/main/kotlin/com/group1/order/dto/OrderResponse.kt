package com.group1.order.dto

import com.vinsguru.enums.OrderStatus
import lombok.Data
import java.util.UUID

@Data
class OrderResponseDTO {
    private val orderId: UUID? = null
    private val userId: Integer? = null
    private val productId: Integer? = null
    private val amount: Double? = null
    private val status: OrderStatus? = null
}