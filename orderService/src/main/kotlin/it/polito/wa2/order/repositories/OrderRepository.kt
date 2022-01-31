package it.polito.wa2.order.repositories

import it.polito.wa2.order.domain.Order
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OrderRepository: JpaRepository<Order, Long> {
    fun findAllByBuyerId(buyer_id: Long): List<Order>
}