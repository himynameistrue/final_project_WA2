package it.polito.wa2.order.controllers

import it.polito.wa2.dto.*
import it.polito.wa2.enums.OrderStatus
import it.polito.wa2.order.domain.Order
import it.polito.wa2.order.services.OrderServiceImpl
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
import org.springframework.web.server.ResponseStatusException
import java.net.URI
import javax.validation.Valid
import kotlin.collections.List;

@RestController
@RequestMapping("/orders")
class OrderController(var orderService: OrderServiceImpl) {

    /**
     * Retrieves the list of all orders
     */
    @GetMapping()
    fun list(
        @RequestParam("buyer_id") userID: Long?,
    ): List<OrderDTO> {
        return if (userID === null) {
            orderService.findAll()
        } else {
            orderService.findAllByBuyerId(userID)
        }.map { it.toDTO() }
    }

    /**
     * Adds a new order
     */
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody newOrderDTO: OrderCreateRequestDTO): OrderCreateOrderResponseDTO {

        val order = orderService.create(newOrderDTO.buyerId, newOrderDTO.totalPrice, newOrderDTO.items)

        val orchestratorResponse = runCreationSaga(order, newOrderDTO.totalPrice, newOrderDTO.items)

        orderService.confirm(order, orchestratorResponse)

        return orchestratorResponse.mapToOrderResponse(order.getId()!!)
    }

    /**
     * Retrieves the order identified by orderID
     */
    @GetMapping("/{orderID}")
    fun show(@PathVariable("orderID") orderID: Long): OrderDTO {
        return orderService.findById(orderID).toDTO();
    }

    /**
     * Updates the order identified by orderID
     */
    @PatchMapping("/{orderID}")
    fun updateStatus(@PathVariable("orderID") orderID: Long, request: OrderUpdateRequestDTO): OrderDTO {
        val order = orderService.findById(orderID)

        return orderService.updateStatus(order, request.status).toDTO()
    }

    /**
     * Cancels an existing order, if possible
     */
    @DeleteMapping("/{orderID}")
    fun delete(
        @RequestParam("buyer_id") buyerID: Long?,
        @PathVariable("orderID") orderID: Long
    ): OrderDeleteOrchestratorResponseDTO {

        val order = orderService.findById(orderID)

        if (buyerID !== null) {
            if (order.buyerId != buyerID) {
                throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "You cannot cancel this order")
            }
        }

        val orchestratorResponse = runDeletionSaga(order)

        orderService.cancel(order, buyerID)

        return orchestratorResponse
    }

    /**
     * Gets the URI towards the orchestrator for the given path
     */
    private fun getOrchestratorUri(path: String): URI {
        val host = "orchestrator"
        val port = 8082;

        return URI("http", null, host, port, path, null, null)
    }

    /**
     * Handles the orchestrator request && response handling for the Order creation saga
     */
    private fun runCreationSaga(
        order: Order,
        totalPrice: Float,
        items: List<RequestOrderProductDTO>
    ): OrderCreateOrchestratorResponseDTO {
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
            orderService.updateStatus(order, OrderStatus.FAILED)
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Order creation failed")
        }

        return responseEntity.body!!
    }

    /**
     * Handles the orchestrator request && response handling for the Order deletion saga
     */
    private fun runDeletionSaga(
        order: Order
    ): OrderDeleteOrchestratorResponseDTO {
        val uri = getOrchestratorUri("/orders/${order.getId()!!}")

        val requestItems = order.items.map {
            RequestOrderProductDTO(it.productId, it.amount)
        }
        val responseEntity = RestTemplate().exchange(
            uri,
            HttpMethod.DELETE,
            HttpEntity(OrderDeleteOrchestratorRequestDTO(order.getId()!!, order.buyerId, requestItems)),
            OrderDeleteOrchestratorResponseDTO::class.java
        )

        if (!responseEntity.statusCode.is2xxSuccessful || responseEntity.body === null || !responseEntity.body!!.isSuccessful) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Order deletion failed")
        }

        return responseEntity.body!!
    }
}