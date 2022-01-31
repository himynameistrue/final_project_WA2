package it.polito.wa2.catalog.controllers

import it.polito.wa2.catalog.exceptions.InvalidRestTemplateHostException
import it.polito.wa2.dto.OrderCreateRequestDTO
import it.polito.wa2.dto.OrderCreateResponseDTO
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import java.net.URI
import javax.servlet.http.HttpServletRequest

/**
 * ALL (without authentication)
 * list products
 * get features of products
 * get availability of products
 * register
 *
 * USERS with a wallet
 * order a product (check wallet and warehouse) -> createOrder
 * cancel an order (if not shipped) -> cancelOrder
 * retrieve orders
 * retrieve specific order
 * retrieve products
 * retrieve specific product
 * retrieve wallets
 * OK retrieve user info
 * OK update user info -> change password
 * place and evaluation of purchased product
 *
 *
 * ADMINS
 * edit product (edit properties and picture)
 * access all information -> get product, order, wallet information
 * assign admin role to a customer
 */

@RestController
class GatewayController {

    @PostMapping("/orders/create")
    fun createOrder(request: HttpServletRequest, @RequestBody newOrderDTO: OrderCreateRequestDTO): OrderCreateResponseDTO? {
        lateinit var host: String
        var port = 8080;

        with(request.requestURI) {
            when {
                startsWith("/orders/") -> {
                    host = "order"
                    port = 8081
                }

                else -> throw InvalidRestTemplateHostException()
            }
        }

        val uri = URI("http", null, host, port, request.requestURI, request.queryString, null)

        val httpMethod = HttpMethod.POST


        val responseEntity = RestTemplate().exchange(
            uri, httpMethod, HttpEntity<OrderCreateRequestDTO>(newOrderDTO), OrderCreateResponseDTO::class.java
        )

        print(responseEntity)

        return responseEntity.body
    }

    @RequestMapping("/orders/**")
    fun proxy(request: HttpServletRequest): String? {

        val responseEntity: ResponseEntity<String> = restTemplate(request, String::class.java)

        return responseEntity.body
    }

    fun <T> restTemplate(
        request: HttpServletRequest,
        responseType: Class<T>,
    ): ResponseEntity<T> {
        lateinit var host: String
        var port = 8080;

        with(request.requestURI) {
            when {
                startsWith("/orders/") -> {
                    host = "order"
                    port = 8081
                }

                else -> throw InvalidRestTemplateHostException()
            }
        }

        val uri = URI("http", null, host, port, request.requestURI, request.queryString, null)

        val httpMethod = HttpMethod.valueOf(request.method);

        var body = "";

        val hasBody = listOf(HttpMethod.POST, HttpMethod.PUT, HttpMethod.PATCH).any { it ===  httpMethod }

        if (hasBody) {
            body = request.inputStream.readAllBytes().toString()
        }

        return RestTemplate().exchange(
            uri,
            httpMethod,
            if(!hasBody) null else HttpEntity<String>(body),
            responseType
        );
    }

}