package it.polito.wa2.dto

import java.util.*

data class ProductPartialUpdateRequestDTO (
    val name: String?,
    val description: String?,
    val picture_url: String?,
    val category: String?,
    val price: Float?,
    val average_rating: Float?,
    val creation_date: Date?
)