package it.polito.wa2.warehouse.domain

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Index
import javax.persistence.Table
import javax.validation.constraints.Email

import it.polito.wa2.warehouse.dto.CommentDTO
import it.polito.wa2.warehouse.entities.EntityBase

@Entity
class Comment (
    var title: String,
    var body: String,
    var stars: Int
    ): EntityBase<Long>(), Serializable {

    fun toDTO(): CommentDTO {
        return CommentDTO(title, body, stars)
    }
}