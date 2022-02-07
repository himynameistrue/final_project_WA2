package it.polito.wa2.dto

import org.springframework.data.util.ProxyUtils
import java.util.*

data class ProductDTO(
    val id: Long,
    val name: String,
    val description: String,
    val picture_url: String?,
    val category: String?,
    val price: Float,
    val average_rating: Float,
    val creation_date: Date,
    val availabilities: Map<Long, Int>
)