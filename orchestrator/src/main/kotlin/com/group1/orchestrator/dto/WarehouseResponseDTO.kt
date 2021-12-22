package com.group1.orchestrator.dto
import com.group1.orchestrator.enums.InventoryStatus
import java.util.UUID

data class WarehouseResponseDTO (
    val orderId: UUID,
    val userId: Int,
    val productId: Int,
    val status: InventoryStatus
)