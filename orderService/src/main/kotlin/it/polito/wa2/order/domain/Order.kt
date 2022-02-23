package it.polito.wa2.order.domain

import it.polito.wa2.dto.OrderDTO
import it.polito.wa2.enums.OrderStatus
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.time.LocalDateTime
import javax.persistence.*
import javax.validation.constraints.NotNull


@Entity
@Table(name = "orders")
class Order(
    @NotNull
    @Column(name = "buyer_id")
    var buyerId: Long,
    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    var items: List<OrderProduct> = listOf(),
    @NotNull
    var status: OrderStatus,
    @Column(updatable = false)
    @CreationTimestamp
    val createdAt: LocalDateTime? = null,
    @UpdateTimestamp
    val updatedAt: LocalDateTime? = null
) : EntityBase<Long>() {

    fun toDTO(): OrderDTO {
        return OrderDTO(getId()!!, buyerId, items.map { it.toDTO() }, status)
    }
}