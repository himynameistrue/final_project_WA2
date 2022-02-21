package it.polito.wa2.warehouse.entities

import it.polito.wa2.dto.CommentDTO
import javax.persistence.*


@Entity
class Comment (
        var title: String,
        var body: String,
        var stars: Int
    ): EntityBase<Long>() {

    fun toDTO(): CommentDTO {
        return CommentDTO(title, body, stars)
    }
}