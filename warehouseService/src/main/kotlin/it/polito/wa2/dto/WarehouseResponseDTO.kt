package it.polito.wa2.dto

import it.polito.wa2.enums.InventoryStatus
import java.io.Serializable
import java.util.UUID

data class WarehouseResponseDTO (
    val orderId: UUID,
    val userId: Int,
    val productId: Int,
    val status: InventoryStatus
) : Serializable