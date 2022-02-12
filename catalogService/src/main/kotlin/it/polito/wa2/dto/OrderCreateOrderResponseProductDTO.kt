package it.polito.wa2.dto


/**
 * Represents a Product contained in an Order after being created by orderService
 */
data class OrderCreateOrderResponseProductDTO(
    val id: Long,
    val name: String,
    var quantity: Long
)