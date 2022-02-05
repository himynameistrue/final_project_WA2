package it.polito.wa2.warehouse.entities


import javax.persistence.*
import javax.persistence.MapsId

import javax.persistence.EmbeddedId




@Entity
@Table(name = "warehouse_product")
class ProductAvailability(
    @EmbeddedId
    val id: ProductAvailabilityKey,

    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    val product: Product,

    @ManyToOne
    @MapsId("warehouseId")
    @JoinColumn(name = "warehouse_id")
    val warehouse: Warehouse,

    var quantity: Int,
    val alarm: Int
    )

{
    override fun toString(): String {
        return product.name + ", " + warehouse.name + ", " + quantity.toString() + ", " + alarm.toString()
    }

}



/*
@Entity(name = "ProductAvailability")
@Table(name = "product_availability")
class ProductAvailability {
    @EmbeddedId
    private var id: PostTagId? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("warehouseId")
    private var post: Warehouse? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productId")
    private var tag: Product? = null

    @Column(name = "quantity")
    private val quantity: Int = 0

    @Column(name = "alarm")
    private val alarm: Int = 0

    private constructor() {}
    constructor(post: Post, tag: Tag) {
        this.post = post
        this.tag = tag
        id = PostTagId(post.getId(), tag.getId())
    }

    //Getters and setters omitted for brevity
    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as PostTag
        return Objects.equals(post, that.post) &&
                Objects.equals(tag, that.tag)
    }

    override fun hashCode(): Int {
        return Objects.hash(post, tag)
    }
}*/

