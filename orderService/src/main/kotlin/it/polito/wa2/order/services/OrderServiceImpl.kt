package it.polito.wa2.order.services

import it.polito.wa2.dto.*
import it.polito.wa2.enums.OrderStatus
import it.polito.wa2.order.domain.Order
import it.polito.wa2.order.domain.OrderProduct
import it.polito.wa2.order.repositories.OrderRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.web.client.RestTemplate
import org.springframework.web.server.ResponseStatusException
import java.net.URI
import java.util.*

@Service
@Transactional
class OrderServiceImpl(
    val orderRepository: OrderRepository,
) : OrderService {

    override fun findAll(): List<Order> = orderRepository.findAll()

    override fun findAllByBuyerId(buyerId: Long): List<Order> = orderRepository.findAllByBuyerId(buyerId)

    override fun findById(orderId: Long): Order {
        val maybeOrder: Optional<Order> = orderRepository.findById(orderId);
        if (maybeOrder.isEmpty) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Order Not Found")
        }

        return maybeOrder.get();
    }

    override fun create(
        buyerId: Long,
        totalPrice: Float,
        items: List<RequestOrderProductDTO>
    ): Order {
        val order = Order(buyerId, listOf(), OrderStatus.PENDING)

        order.items = items.map {
            OrderProduct(order, it.productId, it.amount, null)
        }

        return orderRepository.save(order)
    }

    override fun confirm(order: Order, confirmedOrderDTO: OrderCreateOrchestratorResponseDTO): Order {
        order.status = OrderStatus.ISSUED

        val unitPriceByProductId = mutableMapOf<Long, Float>()
        confirmedOrderDTO.items.forEach {
            unitPriceByProductId[it.productId] = it.unitPrice;
        }

        order.items = order.items.map {
            it.unitPrice = unitPriceByProductId[it.productId]
            it
        }

        return orderRepository.save(order)
    }

    override fun updateStatus(order: Order, newStatus: OrderStatus): Order {
        throwIfInvalidStatusChange(order.status, newStatus);

        order.status = newStatus

        return orderRepository.save(order)
    }

    override fun cancel(order: Order, buyerId: Long?): Order {
        return updateStatus(order, OrderStatus.CANCELED)
    }

    private fun throwIfInvalidStatusChange(currentStatus: OrderStatus, nextStatus: OrderStatus) {
        val shouldThrow = when (nextStatus) {
            OrderStatus.ISSUED -> currentStatus !== OrderStatus.PENDING
            OrderStatus.DELIVERING -> currentStatus !== OrderStatus.ISSUED
            OrderStatus.DELIVERED -> currentStatus !== OrderStatus.ISSUED && currentStatus !== OrderStatus.DELIVERING
            OrderStatus.CANCELED -> currentStatus !== OrderStatus.ISSUED
            OrderStatus.FAILED -> false
            else -> true
        }

        if (shouldThrow) {
            throwBadStatus()
        }
    }

    private fun throwBadStatus() {
        throw ResponseStatusException(
            HttpStatus.BAD_REQUEST,
            "Order status cannot be updated to the provided value"
        )
    }

}