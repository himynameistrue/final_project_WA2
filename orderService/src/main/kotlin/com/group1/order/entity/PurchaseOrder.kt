package com.group1.order.entity

import com.group1.enums.OrderStatus
import javax.persistence.Entity
import javax.persistence.Id
import java.util.UUID

@Entity
class PurchaseOrder (
    @Id
    val id: UUID,
    val userId: Int,
    val productId: Int,
    val price: Double,
    var status: OrderStatus
)