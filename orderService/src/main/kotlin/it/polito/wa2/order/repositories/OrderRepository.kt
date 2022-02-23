package it.polito.wa2.order.repositories

import it.polito.wa2.order.domain.Order
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface OrderRepository: JpaRepository<Order, Long> {
    fun findAllByBuyerId(buyerId: Long): List<Order>

    fun countByBuyerId_AndItems_ProductId(@Param("buyerId") buyerId: Long,@Param("productId") productId: Long): Int
}