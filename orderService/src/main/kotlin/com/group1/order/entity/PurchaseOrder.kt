package com.group1.order.entity

import com.vinsguru.enums.OrderStatus
import lombok.Data
import lombok.ToString
import javax.persistence.Entity
import javax.persistence.Id
import java.util.UUID

@Data
@Entity
@ToString
class PurchaseOrder {
    @Id
    private val id: UUID? = null
    private val userId: Integer? = null
    private val productId: Integer? = null
    private val price: Double? = null
    private val status: OrderStatus? = null
}