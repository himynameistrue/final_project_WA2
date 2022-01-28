package it.polito.wa2.catalog.controllers

import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.startsWith
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
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
    @RequestMapping("/orders/**")
    fun proxy(request: HttpServletRequest, method: HttpMethod, @RequestBody(required = false) body: String): String? {

        val responseEntity: ResponseEntity<String>? = restTemplate(request, String::class.java)

        return responseEntity?.body
    }

    fun restTemplate(
        request: HttpServletRequest,
        responseType: Class<String>,
    ): ResponseEntity<String>? {
        // TODO change ResponseEntity type based on responseType
        var host: String = ""
        var port: Int = 8081

        val isValidRequest = with(request.requestURI) {
            when {
                startsWith("/orders/") -> {
                    host = "order"
                    port = 8081
                    true
                }

                else -> false
            }
        }

        if(!isValidRequest){
            return null;
        }

        val uri = URI("http", null, host, port, request.requestURI, request.queryString, null)

        val httpMethod = HttpMethod.valueOf(request.method);

        var restTemplate = RestTemplate().exchange(
            uri, httpMethod, null,
            responseType
        );

        val hasBody = listOf(HttpMethod.POST, HttpMethod.PUT, HttpMethod.PATCH).any { it ===  httpMethod }

        if (hasBody) {
               val body = request.inputStream.readAllBytes().toString()

            restTemplate= RestTemplate().exchange(
                uri,
                httpMethod,
                HttpEntity<String>(body),
                responseType
            )
        }

        return restTemplate;
    }

}