package com.wa2.finalproject.warehouse.domain

import java.io.Serializable

import javax.persistence.Embeddable

/*
@Embeddable
class ProductAvailablityKey() : Serializable {
    @Column(name = "post_id")
    private var postId: Long? = null

    @Column(name = "tag_id")
    private var tagId: Long? = null

    private constructor() {}
    constructor(
        postId: Long?,
        tagId: Long?
    ) {
        this.postId = postId
        this.tagId = tagId
    }

    //Getters omitted for brevity
    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as PostTagId
        return Objects.equals(postId, that.postId) &&
                Objects.equals(tagId, that.tagId)
    }

    override fun hashCode(): Int {
        return Objects.hash(postId, tagId)
    }
}
*/


@Embeddable
class ProductAvailabilityKey : Serializable {
    // @Column(name = "product_id")
    var productId: Long? = null

    //@Column(name = "warehouse_id")
    var warehouseId: Long? = null // standard constructors, getters, and setters
    // hashcode and equals implementation
}
