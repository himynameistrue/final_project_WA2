package it.polito.wa2.order.services

import it.polito.wa2.dto.OrderCreateOrchestratorRequestDTO
import it.polito.wa2.dto.OrderCreateOrchestratorResponseDTO
import it.polito.wa2.dto.OrderCreateOrderResponseDTO
import it.polito.wa2.enums.OrderStatus
import it.polito.wa2.order.domain.Order
import it.polito.wa2.order.domain.OrderProduct
import it.polito.wa2.dto.OrderCreateRequestProductDTO
import it.polito.wa2.order.exceptions.OrderNotFoundException
import it.polito.wa2.order.repositories.OrderRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import it.polito.wa2.order.exceptions.OrderAlreadyCanceledException
import it.polito.wa2.order.exceptions.OrderCreationFailedException
import it.polito.wa2.order.exceptions.OrderStatusChangeFailedException
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.web.client.RestTemplate
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
            throw OrderNotFoundException();
        }

        return maybeOrder.get();
    }

    override fun create(buyerId: Long, totalPrice: Float, items: List<OrderCreateRequestProductDTO>): OrderCreateOrderResponseDTO {
        var order = Order(buyerId, listOf(), OrderStatus.PENDING)

        order.items = items.map {
            OrderProduct(order, it.productId, it.amount, null)
        }

        order = orderRepository.save(order)

        val orchestratorResponse = runCreationSaga(order, totalPrice, items)

        confirm(order, orchestratorResponse)

        return orchestratorResponse.mapToOrderResponse()
    }

    private fun runCreationSaga(order: Order, totalPrice: Float, items: List<OrderCreateRequestProductDTO>) : OrderCreateOrchestratorResponseDTO {
        val uri = getOrchestratorUri("/orders")

        val body = OrderCreateOrchestratorRequestDTO(
            order.getId()!!,
            order.buyerId,
            totalPrice,
            items
        )

        val responseEntity = RestTemplate().exchange(
            uri,
            HttpMethod.POST,
            HttpEntity<OrderCreateOrchestratorRequestDTO>(body),
            OrderCreateOrchestratorResponseDTO::class.java
        )

        if (!responseEntity.statusCode.is2xxSuccessful || responseEntity.body === null || !responseEntity.body!!.isSuccessful) {
            updateStatus(order.getId()!!, OrderStatus.FAILED)
            throw OrderCreationFailedException()
        }

        return responseEntity.body!!
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

        throwIfInvalidStatusChange(order.status, newStatus);

        order.status = newStatus

        return orderRepository.save(order)
    }

    override fun cancel(orderId: Long): Order = updateStatus(orderId, OrderStatus.CANCELED)

    private fun throwIfInvalidStatusChange(currentStatus: OrderStatus, nextStatus: OrderStatus) {
        when (currentStatus) {
            OrderStatus.CANCELED -> {
                if (nextStatus === OrderStatus.CANCELED) {
                    throw OrderAlreadyCanceledException()
                }
                throw OrderStatusChangeFailedException()
            }
            OrderStatus.DELIVERING -> {
                if (!(nextStatus === OrderStatus.DELIVERED || nextStatus === OrderStatus.FAILED)) {
                    throw OrderStatusChangeFailedException()
                }
            }
            OrderStatus.DELIVERED, OrderStatus.FAILED -> throw OrderStatusChangeFailedException()
            else -> {}
        }
    }

    private fun getOrchestratorUri(path: String): URI {
        val host = "orchestrator"
        val port = 8082;

        return URI("http", null, host, port, path, null, null)
    }

}