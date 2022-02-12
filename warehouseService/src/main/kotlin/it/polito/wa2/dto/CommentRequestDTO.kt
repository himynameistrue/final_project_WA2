package it.polito.wa2.dto

import java.util.UUID

data class CommentRequestDTORequestDTO (
    val title: String,
    val body: String,
    val stars: Int
)