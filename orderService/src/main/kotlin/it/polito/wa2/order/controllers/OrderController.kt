package it.polito.wa2.order.controllers

import it.polito.wa2.dto.*
import it.polito.wa2.order.services.OrderServiceImpl
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
import org.springframework.web.server.ResponseStatusException
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
            return orderService.create(newOrderDTO.buyerId, newOrderDTO.totalPrice, newOrderDTO.items)
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

        return orderService.updateStatus(orderID, request.status).toDTO()
    }

    /**
     * Cancels an existing order, if possible
     */
    @DeleteMapping("/{orderID}")
    fun delete(@RequestParam("buyer_id") buyerID: Long?, @PathVariable("orderID") orderID: Long) {

        val order = orderService.findById(orderID)

        if(buyerID !== null){
            if(order.buyerId != buyerID){
                throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "You cannot cancel this order")
            }
        }

        orderService.cancel(orderID)

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