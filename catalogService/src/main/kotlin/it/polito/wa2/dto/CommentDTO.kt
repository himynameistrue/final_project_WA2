package it.polito.wa2.dto

class CommentDTO(
    val title: String,
    val body: String,
    val stars: Int
) {
    override fun toString(): String {
        return "$title ,$body ,$stars"
    }
}