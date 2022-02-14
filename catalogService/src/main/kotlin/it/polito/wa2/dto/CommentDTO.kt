package it.polito.wa2.warehouse.dto

import it.polito.wa2.warehouse.entities.Warehouse
import org.springframework.data.util.ProxyUtils
import java.util.*

class CommentDTO(
    val title: String,
    val body: String,
    val stars: Int
) {
    override fun toString(): String {
        return "$title ,$body ,$stars"
    }


}