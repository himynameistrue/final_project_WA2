package it.polito.wa2.order.controllers

import it.polito.wa2.dto.OrderCreateOrchestratorRequestDTO
import it.polito.wa2.dto.OrderCreateOrchestratorResponseDTO
import it.polito.wa2.dto.OrderDTO
import it.polito.wa2.dto.OrderCreateRequestDTO
import it.polito.wa2.order.services.OrderServiceImpl
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
import polito.wa2.team1.orderservice.exceptions.OrderAlreadyCanceledException
import java.net.URI
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid
import kotlin.collections.List;

@RestController
@RequestMapping("/orders")
class OrderController(var orderService: OrderServiceImpl) {


    /**
     * Retrieves the list of all orders
     */
    @GetMapping("/all")
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
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody newOrderDTO: OrderCreateRequestDTO) {

        val pendingOrder = orderService.create(newOrderDTO)

        val host = "orchestrator"
        val port = 8082;

        val uri = URI("http", null, host, port, "/orchestrator", null, null)

        val httpMethod = HttpMethod.POST;

        val body = OrderCreateOrchestratorRequestDTO(
            pendingOrder.getId()!!,
            newOrderDTO.buyerId,
            newOrderDTO.amount,
            newOrderDTO.items
        )

        val responseEntity = RestTemplate().exchange(
            uri,
            httpMethod,
            HttpEntity<OrderCreateOrchestratorRequestDTO>(body),
            OrderCreateOrchestratorResponseDTO::class.java
        );

        val orchestratorResponse = responseEntity.body

        println(orchestratorResponse)
        /*// TODO CHECK wallet money and product availability

        // TODO NOTIFY USER ?
        // TODO NOTIFY ADMIN ?
        return pendingOrder.toDTO()*/
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
/*
    @PatchMapping("{orderID}")
    fun update(@PathVariable("orderID") orderID: Long, request): OrderDTO {

        // TODO NOTIFY USER OF CHANGES
        // TODO NOTIFY ADMIN OF CHANGES
        return orderService.updateStatus(orderID, status).toDTO()
    }
*/

    /**
     * Cancels an existing order, if possible
     */
    @DeleteMapping("/{orderID}")
    fun delete(@PathVariable("orderID") orderID: Long): OrderDTO? {
        try {
            val order = orderService.cancel(orderID)

            // TODO REFUND CUSTOMER
            // TODO RETURN ITEMS TO WAREHOUSE

            // TODO NOTIFY USER
            // TODO NOTIFY ADMIN ?

            return order.toDTO();
        } catch (e: OrderAlreadyCanceledException) {
            // TODO DEFINE HOW TO HANDLE
        }
        return null;
    }

    fun <T> restToOrchestrator(
        request: HttpServletRequest,
        responseType: Class<T>,
    ): ResponseEntity<T> {
        val host = "orchestrator"
        val port = 8082;

        val uri = URI("http", null, host, port, request.requestURI, request.queryString, null)

        val httpMethod = HttpMethod.valueOf(request.method);

        var body = "";

        val hasBody = listOf(HttpMethod.POST, HttpMethod.PUT, HttpMethod.PATCH).any { it === httpMethod }

        if (hasBody) {
            body = request.inputStream.readAllBytes().toString()
        }

        return RestTemplate().exchange(
            uri,
            httpMethod,
            if (!hasBody) null else HttpEntity<String>(body),
            responseType
        );
    }
}