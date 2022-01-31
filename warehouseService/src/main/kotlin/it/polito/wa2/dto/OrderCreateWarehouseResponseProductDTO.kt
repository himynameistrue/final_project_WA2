package it.polito.wa2.dto

/**
 * Represents an OrderProduct as returned to the user
 */
data class OrderCreateWarehouseResponseProductDTO(
    val productId: Long,
    val amount: Long,
    val unitPrice: Float,
    val isUnderThreshold: Boolean,
    val remainingProducts: Long,
)