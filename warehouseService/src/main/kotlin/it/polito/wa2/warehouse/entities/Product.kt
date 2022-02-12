package it.polito.wa2.warehouse.entities

import it.polito.wa2.warehouse.dto.ProductDTO
import java.util.*
import javax.persistence.*

@Entity
class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_generator")
    @SequenceGenerator(
        name = "product_generator",
        sequenceName = "sequence_1", initialValue = 1, allocationSize = 1
    )
    @Column(updatable = false, nullable = false)
    val id: Long? = null,

    val name: String?,
    val description: String?,
    val picture_url: String?,
    val category: String?,
    val price: Float?,
    var average_rating: Float = 0F,
    val creation_date: Date?,

    @OneToMany
    val comments: MutableList<Comment> = mutableListOf(),

    @OneToMany(mappedBy = "product")
    val availabilities: MutableList<ProductAvailability> = mutableListOf()

) {
    //: EntityBase<Long>(){


    fun toDTO(): ProductDTO {
        var map = mutableMapOf<Long, Int>()
        for (a in availabilities) {
            map.put(a.warehouse.id!!, a.quantity)
        }
        return ProductDTO(
            id!!,
            name!!,
            description!!,
            picture_url,
            category,
            price!!,
            average_rating!!,
            creation_date!!,
            comments,
            map!!
        )
    }

}