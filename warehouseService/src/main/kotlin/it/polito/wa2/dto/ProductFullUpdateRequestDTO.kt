package it.polito.wa2.dto

import org.springframework.web.bind.annotation.PathVariable

data class ProductFullUpdateRequestDTO(
    val name: String,
    val description: String,
    val picture_url: String,
    val category: String,
    val price: Float,
    val average_rating: Float
)