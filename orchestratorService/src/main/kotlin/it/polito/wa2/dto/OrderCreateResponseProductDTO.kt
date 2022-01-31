package it.polito.wa2.dto

/**
 * Represents an OrderProduct as returned to the user
 */
data class OrderCreateResponseProductDTO(
    val productId: Long,
    val amount: Long,
    val unitPrice: Float
)