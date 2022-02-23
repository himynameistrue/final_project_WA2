package it.polito.wa2.dto

data class AuthorizeCommentDTO(
    val userId: Long,
    val title: String,
    val body: String,
    val stars: Int
)