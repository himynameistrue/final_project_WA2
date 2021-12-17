package com.wa2.finalproject.warehouse.dto

import com.wa2.finalproject.warehouse.enums.InventoryStatus
import java.util.UUID

data class WarehouseResponseDTO (
    val orderId: UUID,
    val userId: Int,
    val productId: Int,
    val status: InventoryStatus
)