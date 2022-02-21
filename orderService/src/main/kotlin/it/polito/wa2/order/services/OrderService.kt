package it.polito.wa2.order.services

import it.polito.wa2.dto.OrderCreateOrchestratorResponseDTO
import it.polito.wa2.dto.OrderCreateOrderResponseDTO
import it.polito.wa2.dto.OrderDeleteOrchestratorResponseDTO
import it.polito.wa2.enums.OrderStatus
import it.polito.wa2.order.domain.Order
import it.polito.wa2.dto.RequestOrderProductDTO

interface OrderService {
    fun findAll(): List<Order>
    fun findAllByBuyerId(buyerId: Long): List<Order>
    fun findById(orderId: Long): Order
    fun create(buyerId: Long, totalPrice: Float, items: List<RequestOrderProductDTO>): Order
    fun confirm(order: Order, confirmedOrderDTO: OrderCreateOrchestratorResponseDTO): Order
    fun updateStatus(order: Order, newStatus: OrderStatus): Order
    fun cancel(order: Order, buyerId: Long?): Order
    fun getOrderTotal(order: Order): Float
}