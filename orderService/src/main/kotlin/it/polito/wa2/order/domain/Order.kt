package it.polito.wa2.order.domain

import it.polito.wa2.dto.OrderDTO
import it.polito.wa2.enums.OrderStatus
import javax.persistence.*
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

@Entity
@Table(name="orders")
class Order(
    @NotNull
    @Column(name="buyer_id")
    var buyerId: Long,
    @OneToMany(mappedBy="order", cascade = [CascadeType.PERSIST], fetch = FetchType.EAGER)
    @NotEmpty
    var items: List<OrderProduct> =  listOf(),
    @NotNull
    var status: OrderStatus,
):EntityBase<Long>() {


    fun toDTO(): OrderDTO {
        return OrderDTO(getId()!!, buyerId, items.map { it.toDTO() }, status)
    }
}