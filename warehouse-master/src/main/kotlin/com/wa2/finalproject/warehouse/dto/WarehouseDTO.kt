package com.wa2.finalproject.warehouse.dto

import com.wa2.finalproject.warehouse.domain.ProductAvailability
import com.wa2.finalproject.warehouse.domain.Warehouse
import org.springframework.data.util.ProxyUtils

class WarehouseDTO(
    val id: Long,
    val name: String,
    val location: String,
    val availabilities: Map<Long, Int>
) {
    override fun toString(): String {
        return "$id ,$name ,$location , ${availabilities.map { print(it) }}"
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