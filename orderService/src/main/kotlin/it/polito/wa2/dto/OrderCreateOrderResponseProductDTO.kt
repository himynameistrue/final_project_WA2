package it.polito.wa2.dto

/**
 * Represents an OrderProduct as returned to the user
 */
data class OrderCreateOrderResponseProductDTO(
    val productId: Long,
    val remainingProducts: Long,
)