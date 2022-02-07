package it.polito.wa2.dto

import java.util.*

data class ProductPartialUpdateRequestDTO (
    val name: String? = null,
    val description: String?,
    val picture_url: String? = null,
    val category: String? = null,
    val price: Float? = null,
    val average_rating: Float? = null,
    val creation_date: Date? = null
)