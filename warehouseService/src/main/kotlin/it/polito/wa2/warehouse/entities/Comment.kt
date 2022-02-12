package it.polito.wa2.warehouse.domain

import java.io.Serializable
import javax.validation.constraints.Email

import it.polito.wa2.warehouse.dto.CommentDTO
import it.polito.wa2.warehouse.entities.EntityBase
import it.polito.wa2.warehouse.entities.ProductAvailabilityKey
import javax.persistence.*


@Entity
class Comment (
        @Id
        val id: ProductAvailabilityKey,
        var title: String,
        var body: String,
        var stars: Int
    ) {

    fun toDTO(): CommentDTO {
        return CommentDTO(title, body, stars)
    }
}