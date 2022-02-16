package it.polito.wa2.dto

/**
 * Represents an OrderProduct as returned to the user
 */
data class InventoryChangeResponseProductDTO(
    val productId: Long,
    val warehouseId: Long,
    val amount: Long,
    val unitPrice: Float,
    val isUnderThreshold: Boolean,
    val remainingProducts: Long,
    val productName: String,
    val warehouseName: String
)