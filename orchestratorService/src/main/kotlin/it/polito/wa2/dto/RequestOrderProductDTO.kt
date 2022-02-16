package it.polito.wa2.dto

/**
 * Represents an OrderProduct as received from an HTTPRequest
 */
data class RequestOrderProductDTO(
    val productId: Long,
    val amount: Long,
)