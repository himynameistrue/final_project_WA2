package it.polito.wa2.dto

data class OrderProductDTO(
    val product_id: Long,
    val amount: Long,
    val unit_price: Float?
)