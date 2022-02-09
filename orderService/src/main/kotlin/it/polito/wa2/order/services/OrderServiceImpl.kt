package it.polito.wa2.order.services

import it.polito.wa2.dto.OrderCreateOrchestratorResponseDTO
import it.polito.wa2.enums.OrderStatus
import it.polito.wa2.order.domain.Order
import it.polito.wa2.order.domain.OrderProduct
import it.polito.wa2.dto.OrderCreateRequestDTO
import it.polito.wa2.order.exceptions.OrderNotFoundException
import it.polito.wa2.order.repositories.OrderRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import it.polito.wa2.order.exceptions.OrderAlreadyCanceledException
import it.polito.wa2.order.exceptions.OrderStatusChangeFailedException
import java.util.*

@Service
@Transactional
class OrderServiceImpl(
    val orderRepository: OrderRepository,
): OrderService {

    override fun findAll(): List<Order> = orderRepository.findAll()

    override fun findAllByBuyerId(buyerId: Long): List<Order> = orderRepository.findAllByBuyerId(buyerId)

    override fun findById(orderId: Long): Order {
        val maybeOrder: Optional<Order> = orderRepository.findById(orderId);
        if (maybeOrder.isEmpty) {
            throw OrderNotFoundException();
        }

        return maybeOrder.get();
    }

    override fun create(newOrderDTO: OrderCreateRequestDTO): Order {
        val order = Order(newOrderDTO.buyerId, listOf(), OrderStatus.PENDING)

        order.items = newOrderDTO.items.map {
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



    override fun updateStatus(orderId: Long, newStatus: OrderStatus): Order {

        val order = findById(orderId)
        when (order.status) {
            OrderStatus.CANCELED -> {
                if(newStatus === OrderStatus.CANCELED){
                    throw OrderAlreadyCanceledException()
                }
                throw OrderStatusChangeFailedException()
            }
            OrderStatus.DELIVERING -> {
                if(newStatus !== OrderStatus.DELIVERED || newStatus !== OrderStatus.FAILED){
                    throw OrderStatusChangeFailedException()
                }
            }
            OrderStatus.DELIVERED,OrderStatus.FAILED -> throw OrderStatusChangeFailedException()
        }

        order.status = newStatus

        return orderRepository.save(order)
    }

    override fun cancel(orderId: Long): Order = updateStatus(orderId, OrderStatus.CANCELED)


}