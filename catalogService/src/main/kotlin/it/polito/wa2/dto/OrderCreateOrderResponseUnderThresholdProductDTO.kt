package it.polito.wa2.dto

/**
 * Represents an OrderProduct as returned to the user
 */
data class OrderCreateOrderResponseUnderThresholdProductDTO(
    val productId: Long,
    val productName: String,
    val remainingProducts: Long,
)