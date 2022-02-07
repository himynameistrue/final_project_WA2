package it.polito.wa2.dto

data class ProductCreateRequestDTO (
    val name: String,
    val description: String,
    val picture_url: String,
    val category: String,
    val price: Float
)