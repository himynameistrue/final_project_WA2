package com.group1.orchestrator.dto
import java.util.UUID

data class WarehouseRequestDTO (
    val userId: Int,
    val productId: Int,
    val orderId: UUID
)