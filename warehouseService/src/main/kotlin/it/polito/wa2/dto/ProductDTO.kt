package it.polito.wa2.dto

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
    val comments: List<CommentDTO> = listOf(),
    val availabilities: Map<Long, Int>
) {
    override fun toString(): String {
        return "$id ,$name ,$description, $picture_url, ${availabilities.map { print(it) }}"
    }
}