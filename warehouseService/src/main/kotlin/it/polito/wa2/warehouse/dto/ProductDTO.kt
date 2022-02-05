package it.polito.wa2.warehouse.dto

import it.polito.wa2.warehouse.entities.Warehouse
import org.springframework.data.util.ProxyUtils
import java.util.*

class ProductDTO(
    val id: Long,
    val name: String,
    val description: String,
    val picture_url: String?,
    val category: String?,
    val price: Float,
    val average_rating: Float,
    val creation_date: Date,
    val availabilities: Map<Long, Int>
) {
    override fun toString(): String {
        return "$id ,$name ,$description, $picture_url, ${availabilities.map { print(it) }}"
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other === this) return true
        if (javaClass != ProxyUtils.getUserClass(other))
            return false
        other as Warehouse
        return if (null == id) false
        else this.id == other.id
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }


}