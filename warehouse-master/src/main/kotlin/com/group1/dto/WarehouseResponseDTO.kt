package com.group1.dto

import com.group1.enums.InventoryStatus
import java.util.UUID

data class WarehouseResponseDTO (
    val orderId: UUID,
    val userId: Int,
    val productId: Int,
    val status: InventoryStatus
)