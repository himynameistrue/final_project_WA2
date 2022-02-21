package it.polito.wa2.dto

import it.polito.wa2.warehouse.entities.Warehouse
import org.springframework.data.util.ProxyUtils
import java.util.*

data class CommentDTO(
    val title: String,
    val body: String,
    val stars: Int
) {
    override fun toString(): String {
        return "$title ,$body ,$stars"
    }

}