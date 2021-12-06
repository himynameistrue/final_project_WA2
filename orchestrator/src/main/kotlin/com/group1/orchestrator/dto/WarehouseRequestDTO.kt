package com.vinsguru.dto

import lombok.Data
import java.util.UUID

@Data
class InventoryRequestDTO {
    private val userId: Integer? = null
    private val productId: Integer? = null
    private val orderId: UUID? = null
}